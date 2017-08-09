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

import nedhyett.crimson.networking.tier4.packet.DisconnectPacket;
import nedhyett.crimson.networking.tier4.packet.ReprimandPacket;
import nedhyett.crimson.threading.IProgressReportTask;
import nedhyett.crimson.types.DataStore;
import nedhyett.crimson.utility.StreamUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Represents a client that is connected to the server (as seen by the server).
 *
 * @author Ned Hyett
 */
public class Tier4Client implements IEndpoint {

	/**
	 * The unique identifier for this client.
	 */
	public final String uuid;

	/**
	 * A data store to keep arbitrary data about the client.
	 */
	public final DataStore data = new DataStore();

	private final Socket s;
	private final Tier4ServerPipeline parent;
	private final DataInputStream inputStream;
	private final DataOutputStream outputStream;
	Tier4InputWorker worker = null;
	private Tier4OutputWorker outWorker = null;

	/**
	 * Create a new client.
	 *
	 * @param s      the socket to communicate through.
	 * @param uuid   the unique identifier for this client.
	 * @param parent the parent pipeline that created this client.
	 *
	 * @throws IOException
	 */
	public Tier4Client(Socket s, String uuid, Tier4ServerPipeline parent) throws IOException {
		this.s = s;
		this.parent = parent;
		inputStream = StreamUtils.wrapUncompressedStream(s.getInputStream());
		outputStream = StreamUtils.wrapUncompressedStream(s.getOutputStream());
		this.uuid = uuid;
	}

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
		return s.getInetAddress();
	}

	@Override
	public boolean isConnected() {
		return s.isConnected();
	}

	@Override
	public boolean close(String reason) {
		try {
			parent.stopTrackingClient(this, reason);
			if(worker != null) worker.interrupt();
			s.close();
			return true;
		} catch(IOException e) {
			return false;
		}
	}

	@Override
	public boolean close() {
		return close("");
	}

	@Override
	public String getCloseReason() {
		return null;
	}

	@Override
	public void sendPacket(final Tier4Packet p) {
		if(!isConnected()) {
			Tier4ServerPipeline.logger.warning("Attempted to send packet to client %s, but the connection has been shut down!", uuid);
			return;
		}
		if(outWorker == null || !outWorker.isAlive()) {
			outWorker = new Tier4OutputWorker(this);
			outWorker.start();
		}
		for(IPacketMorpher morpher : parent.morphers) {
		    morpher.morphPacket(p);
        }
		outWorker.packets.add(p);
	}

	@Override
	public void sendPacketNow(Tier4Packet p) {
		if(!isConnected()) {
			Tier4ServerPipeline.logger.warning("Attempted to send packet to client %s, but the connection has been shut down!", uuid);
			return;
		}
        for(IPacketMorpher morpher : parent.morphers) {
            morpher.morphPacket(p);
        }
		try {
			Tier4Utils.writePacket(outputStream, p);
		} catch(Exception e) {
			System.out.println("Error in Tier4 Output Thread:");
			e.printStackTrace();
		}
	}

	@Override
	public Tier4Packet sendPacketAck(Tier4Packet p, IProgressReportTask... progressListeners) {
		p.ackCode = UUID.randomUUID().toString();
		parent.ackLatches.put(p.ackCode, new CountDownLatch(1));
		sendPacket(p);
		try {
			parent.ackLatches.get(p.ackCode).await(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		parent.ackLatches.remove(p.ackCode);
		return parent.ackPackets.remove(p.ackCode);
	}

	/**
	 * Disconnect this client.
	 */
	public void kick() {
		kick("No reason given!");
	}

	/**
	 * Disconnect this client with a reason.
	 *
	 * @param reason
	 */
	public void kick(String reason) {
		kick(reason, true);
	}

	/**
	 * Disconnect this client with a reason and specify whether to notify the client that they have been kicked.
	 *
	 * @param reason
	 * @param notify
	 */
	public void kick(String reason, boolean notify) {
		if(notify) sendPacket(new DisconnectPacket("Kicked from server: " + reason));
		parent.stopTrackingClient(this, reason);
		try {
			s.close();
		} catch(IOException e) {
			Tier4ServerPipeline.logger.warning("Failed to close socket for client %s!", uuid);
			Tier4ServerPipeline.logger.warning(e);
		}
		if(worker != null) worker.interrupt();
	}

	public void reprimand(String reason) {
		sendPacket(new ReprimandPacket(reason));
	}

}
