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

import nedhyett.crimson.eventreactor.IEvent;
import nedhyett.crimson.filter.Filter;
import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.networking.IConnectionDelegate;
import nedhyett.crimson.networking.ServerSocketListener;
import nedhyett.crimson.networking.tier4.event.ClientDisconnectedEvent;
import nedhyett.crimson.networking.tier4.packet.AckPacket;
import nedhyett.crimson.networking.tier4.packet.HandshakePacket;
import nedhyett.crimson.utility.ArrayUtils;
import nedhyett.crimson.utility.Semver;
import nedhyett.crimson.eventreactor.EventReactor;
import nedhyett.crimson.logging.MiniLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Tier4 is a simple implementation of a packet-based server communication system.
 * Each packet is represented by a class that extends the base class of "Packet". Every transmitted item
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
 *
 * @author Ned Hyett
 */
public class Tier4ServerPipeline implements IPipeline, IConnectionDelegate {

	static final MiniLogger logger = CrimsonLog.spawnLogger("Tier4 - Server");

	protected final ArrayList<IPacketMorpher> morphers = new ArrayList<>();

	private final EventReactor reactor = new EventReactor("Tier4 - Server", Tier4Event.class);
	private final ServerSocketListener listener;
	private final HashMap<String, Tier4Client> clients = new HashMap<>();
	private final String protocolName;
	private final Semver protocolVersion;

	protected HashMap<String, CountDownLatch> ackLatches = new HashMap<>();
	protected HashMap<String, Tier4Packet> ackPackets = new HashMap<>();


	/**
	 * Used by the server to create a new pipeline. Provides default version information so clients should do the same. Effectively disables version checking.
	 *
	 * @param port
	 *
	 * @throws IOException
	 */
	public Tier4ServerPipeline(int port) throws IOException {
		this(port, "BaseProtocolName", new Semver(0, 0, 0));
	}

	/**
	 * Used by the server to create a new pipeline. Allows for the version information to be specified to make sure that the client is safe to connect!
	 *
	 * @param port
	 * @param protocolName
	 * @param protocolVersion
	 *
	 * @throws IOException
	 */
	public Tier4ServerPipeline(int port, String protocolName, Semver protocolVersion) throws IOException {
		listener = new ServerSocketListener(port, this);
		this.protocolName = protocolName;
		this.protocolVersion = protocolVersion;
	}

	/**
	 * Connect this tier4 to the other side.
	 *
	 * @return
	 */
	public boolean connect() {
		listener.start();
		return true;
	}

	@Override
	public void handleConnection(Socket socket) throws Exception {
		String uuid = UUID.randomUUID().toString();
		System.out.println("Got new connection... " + uuid);
		Tier4Client c = new Tier4Client(socket, uuid, this);
		clients.put(uuid, c);
		Tier4InputWorker worker = new Tier4InputWorker(this, c);
		worker.start();
		c.sendPacket(new HandshakePacket(uuid));
	}

	/**
	 * Used internally to remove a client from the tier4.
	 *
	 * @param c
	 * @param reason
	 */
	void stopTrackingClient(Tier4Client c, String reason) {
		clients.remove(ArrayUtils.flip(clients).get(c));
		publishEvent(new ClientDisconnectedEvent(c, this, reason));
	}

	/**
	 * Get a client by its UUID
	 *
	 * @param clientUUID
	 *
	 * @return
	 */
	public Tier4Client getClientFromUUID(String clientUUID) {
		return clients.get(clientUUID);
	}

	/**
	 * Get a collection of all connected clients.
	 *
	 * @return
	 */
	public Collection<Tier4Client> getConnectedClients() {
		return clients.values();
	}

	@Override
	public String getProtocolName() {
		return protocolName;
	}

	@Override
	public Semver getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * Broadcast a packet to all clients.
	 *
	 * @param p
	 */
	public void broadcast(Tier4Packet p) {
		for(Tier4Client c : clients.values()) c.sendPacket(p);
	}

	public void broadcastExcept(Tier4Packet p, String... clientUUIDs) {
		mainLoop:
		for(Tier4Client c : clients.values()) {
			for(String clientUUID : clientUUIDs) {
				if(c.uuid.equalsIgnoreCase(clientUUID)) continue mainLoop;
			}
			c.sendPacket(p);
		}
	}

	/**
	 * Broadcast a packet to the clients speficied in the second argument.
	 *
	 * @param p
	 * @param clientUUIDs
	 */
	public void broadcastTo(Tier4Packet p, String... clientUUIDs) {
		for(String clientUUID : clientUUIDs) {
			Tier4Client c = getClientFromUUID(clientUUID);
			if(c != null) c.sendPacket(p);
		}
	}

	/**
	 * Broadcast to the clients accepted by the filter.
	 *
	 * @param p
	 * @param filter
	 */
	public void broadcast(Tier4Packet p, Filter<Tier4Client> filter) {
		for(Tier4Client c : clients.values()) {
			if(filter.filter(c)) c.sendPacket(p);
		}
	}

	@Override
	public void disconnect() {
		for(Tier4Client c : clients.values()) c.kick("Server is disconnecting!");
		listener.interrupt();
	}

	@Override
	public void registerConnectionListener(Object o) {
		reactor.register(o);
	}

	@Override
	public void unregisterConnectionListener(Object o) {

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		listener.interrupt();
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

	}

	@Override
	public String getEncryptionKey() {
		return null;
	}

    @Override
    public void addPacketMorpher(IPacketMorpher morpher) {
        morphers.add(morpher);
    }

    @Override
    public void removePacketMorpher(IPacketMorpher morpher) {
        morphers.remove(morpher);
    }

    public static Tier4Client getProcessingClient() {
		if(Thread.currentThread() instanceof Tier4InputExecutorThread) {
			return ((Tier4InputExecutorThread)Thread.currentThread()).client;
		}
		return null;
	}


}
