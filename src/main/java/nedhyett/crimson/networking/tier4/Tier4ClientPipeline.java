/*
 * The MIT License
 *
 * Copyright 2017 Ned Hyett.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nedhyett.crimson.networking.tier4;

import nedhyett.crimson.eventreactor.EventReactor;
import nedhyett.crimson.eventreactor.IEvent;
import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.logging.MiniLogger;
import nedhyett.crimson.networking.tier4.event.DisconnectedFromServerEvent;
import nedhyett.crimson.networking.tier4.packet.AckPacket;
import nedhyett.crimson.threading.CrimsonDaemon;
import nedhyett.crimson.threading.IProgressReportTask;
import nedhyett.crimson.threading.ITask;
import nedhyett.crimson.utility.Semver;
import nedhyett.crimson.utility.StreamUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tier4 is a simple implementation of a packet-based server communication system.
 * Each packet is represented by a class that extends the base class of "Tier4Packet". Every transmitted item
 * must implement the "serializable" interface.
 * <p>
 * Each packet is also its own listener. When it is reconstructed at the destination, the corresponding
 * handler method is called in the packet class allowing for easy handling of the packet. The data is
 * compressed before sending and decompressed after sending to minimise the network load.
 * <p>
 * Each packet MUST have a blank constructor to allow for serialization, else it will fail to reconstruct
 * at the destination.
 * <p>
 * The tier4 also handles the handshake between the client and the server and is capable of performing
 * compatibility checks between the server and the client to make sure that the protocols are compatible
 * between the two. The compatibility checking uses the Semver specification.
 * <p>
 * TODO: channels
 * TODO: more robust handling of errors
 * TODO: toggle compression
 * TODO: stop hijacking the CrimsonDaemon to send packets in order.
 *
 * @author Ned Hyett
 */
public class Tier4ClientPipeline implements IPipeline, IEndpoint, Runnable {

	//Pipeline resources
	static final MiniLogger logger = CrimsonLog.spawnLogger("Tier4 - Client");

	protected final ArrayList<IPacketMorpher> morphers = new ArrayList<>();
	protected Thread thread = null;

	private final EventReactor reactor = new EventReactor("Tier4 - Client", Tier4Event.class);
	private final String protocolName;
	private final Semver protocolVersion;
	CountDownLatch connectLatch = new CountDownLatch(1);
	private boolean alreadyConnected = false;
	private String target;
	private int port;

	private String encryptionKey = null;

	private HashMap<String, CountDownLatch> ackLatches = new HashMap<>();
	private HashMap<String, Tier4Packet> ackPackets = new HashMap<>();

	//Endpoint resources
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private String closeReason = null;

	/**
	 * Used by the client to create a new pipeline. Provides default version information so the server should do the same. Effectively disables version checking.
	 *
	 * @param target
	 * @param port
	 *
	 * @throws IOException
	 */
	public Tier4ClientPipeline(String target, int port) throws IOException {
		this(target, port, "BaseProtocolName", new Semver(0, 0, 0));
	}

	/**
	 * Used by the client to create a new pipeline. Allows for the version information to be specified to allow the server to verify if it safe to accept the connection!
	 *
	 * @param target
	 * @param port
	 * @param protocolName
	 * @param protocolVersion
	 *
	 * @throws IOException
	 */
	public Tier4ClientPipeline(String target, int port, String protocolName, Semver protocolVersion) throws IOException {
		this.target = target;
		this.port = port;
		this.protocolName = protocolName;
		this.protocolVersion = protocolVersion;
	}

	/**
	 * Connect this tier4 to the other side.
	 *
	 * @return
	 */
	@Override
	public boolean connect() {
		try {
		    if(thread != null && thread.isAlive()) thread.join();
		    alreadyConnected = false;
		    connectLatch = new CountDownLatch(1);
			socket = new Socket(target, port);
			inputStream = StreamUtils.wrapUncompressedStream(socket.getInputStream());
			outputStream = StreamUtils.wrapUncompressedStream(socket.getOutputStream());
			thread = new Thread(this);
			thread.start();
			if(!awaitConnection()) return false; //Sometimes the client can hang here. TODO: figure out why.
			return isConnected();
		} catch(IOException | InterruptedException e) {
			logger.warning(e);
			return false;
		}
    }

	private boolean awaitConnection() {
		try {
			if(alreadyConnected) return true;
			connectLatch.await();
			return true;
		} catch(InterruptedException e) {
			logger.warning(e);
			return false;
		}
	}

	@Override
	public void run() {
		try {
			while(!thread.isInterrupted() && !socket.isClosed()) {
				Tier4Packet packet;
                try {
                    packet = Tier4Utils.readPacket(inputStream);
                } catch (EOFException | SocketException e) {
                    e.printStackTrace();
                    reactor.publish(new DisconnectedFromServerEvent(this, e.getMessage() != null ? e.getMessage() : "Remote host unexpectedly closed the connection."));
                    disconnect();
                    return;
                }
                if(!packet.canProcessOnSide(Side.CLIENT)) {
					logger.critical("Packet %s cannot be processed on the client!", packet.getClass().getName());
					continue;
				}
				Thread t = new Thread() {
					@Override
					public void run() {
						packet.processPacketClient(Tier4ClientPipeline.this);
						if(packet.ackCode != null) sendPacket(new AckPacket(packet.ackCode));
					}
				};
				t.setDaemon(true);
				t.start();
			}
		} catch(Exception e) {
			logger.critical("Critical failure in the Tier4 pipeline!");
			logger.critical(e);
		}
	}

	@Override
	public String getProtocolName() {
		return protocolName;
	}

	@Override
	public Semver getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public void disconnect() {
	    close();
	}

	@Override
	public void registerConnectionListener(Object o) {
		reactor.register(o);
	}

	@Override
	public void unregisterConnectionListener(Object o) {

	}

	@Override
	public boolean publishEvent(IEvent e) {
		return reactor.publish(e);
	}

	@Override
	public void publishAckEvent(String id, AckPacket packet) {
		if(!ackLatches.containsKey(id)) return;
		ackPackets.put(id, packet);
		ackLatches.get(id).countDown();
	}

	@Override
	public void setEncryptionKey(String key) {
		encryptionKey = key;
	}

	@Override
	public String getEncryptionKey() {
		return encryptionKey;
	}

    @Override
    public void addPacketMorpher(IPacketMorpher morpher) {
        morphers.add(morpher);
    }

    @Override
    public void removePacketMorpher(IPacketMorpher morpher) {
        morphers.remove(morpher);
    }

    /**
	 * Internal use only!
	 */
	public void unlockLatch() {
		connectLatch.countDown();
		alreadyConnected = true;
	}


	//Endpoint methods

	@Override
	public DataInputStream getInputStream() {
		return inputStream;
	}

	@Override
	public DataOutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public InetAddress getAddress() {
		return socket.getInetAddress();
	}

	@Override
	public boolean isConnected() {
		return !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown();
	}

	@Override
	public void sendPacket(final Tier4Packet p) {
		if(!isConnected()) {
			Tier4ClientPipeline.logger.warning("Attempted to send packet to server, but the connection has been shut down!");
			return;
		}
        for(IPacketMorpher morpher : morphers) {
            morpher.morphPacket(p);
        }
		ITask task = new ITask() {

			@Override
			public void call() {
				try {
					Tier4Utils.writePacket(outputStream, p);
				} catch(Exception e) {
					Tier4ClientPipeline.logger.warning("Failed to send packet to server!");
					Tier4ClientPipeline.logger.warning(e);
					disconnect();
				}
			}
		};
		CrimsonDaemon.queueTask(task); //Commandeer the daemon to make sure that packets are not written to the stream at the same time.
	}

	@Override
	public void sendPacketNow(Tier4Packet p) {
		if(!isConnected()) {
			Tier4ClientPipeline.logger.warning("Attempted to send packet to server, but the connection has been shut down!");
			return;
		}
        for(IPacketMorpher morpher : morphers) {
            morpher.morphPacket(p);
        }
		try {
			Tier4Utils.writePacket(outputStream, p);
		} catch(Exception e) {
			Tier4ClientPipeline.logger.warning("Failed to send packet to server!");
			Tier4ClientPipeline.logger.warning(e);
            reactor.publish(new DisconnectedFromServerEvent(this, e.getMessage()));
			disconnect();
		}
	}

	@Override
	public Tier4Packet sendPacketAck(Tier4Packet p, IProgressReportTask... progressListeners) {
		p.ackCode = UUID.randomUUID().toString();
		ackLatches.put(p.ackCode, new CountDownLatch(1));
		if(!isConnected()) {
			Tier4ClientPipeline.logger.warning("Attempted to send packet to server, but the connection has been shut down!");
			return null;
		}
        for(IPacketMorpher morpher : morphers) {
            morpher.morphPacket(p);
        }
		ITask task = new ITask() {

			@Override
			public void call() {
				try {
					Tier4Utils.writePacket(outputStream, p, progressListeners);
				} catch(Exception e) {
					Tier4ClientPipeline.logger.warning("Failed to send packet to server!");
					Tier4ClientPipeline.logger.warning(e);
					disconnect();
				}
			}
		};
		CrimsonDaemon.queueTask(task);//Prevent stream corruption
		try {
			ackLatches.get(p.ackCode).await(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ackLatches.remove(p.ackCode);
		return ackPackets.remove(p.ackCode);
	}

	@Override
	public String getCloseReason() {
		return closeReason;
	}

	@Override
	public boolean close(String reason) {
		closeReason = reason;
		try {
			socket.close();
			return true;
		} catch(IOException e) {
			return false;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		socket.close();
	}

	@Override
	public boolean close() {
		return close("");
	}

}
