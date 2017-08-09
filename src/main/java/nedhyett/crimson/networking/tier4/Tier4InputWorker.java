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

import java.io.DataInputStream;
import java.io.EOFException;

import static nedhyett.crimson.networking.tier4.Tier4ClientPipeline.logger;

/**
 * Pulls data from the allocated client and processes it.
 *
 * @author Ned Hyett
 */
public class Tier4InputWorker extends Thread {

	private final DataInputStream in;
	private final Tier4Client client;
	private final Tier4ServerPipeline parent;

	public Tier4InputWorker(Tier4ServerPipeline parent, Tier4Client c) {
		this.setDaemon(true);
		this.setName("Pipeline Worker " + c.uuid);
		this.parent = parent;
		client = c;
		in = client.getInputStream();
		c.worker = this;
	}

	public Tier4Client getActiveClient() {
		return client;
	}

	@Override
	public void run() {
		try {
			while(!this.isInterrupted()) {
				Tier4Packet o = Tier4Utils.readPacket(in);
				if(!o.canProcessOnSide(Side.SERVER)) {
					logger.critical("Packet %s cannot be processed on the server!", o.getClass().getName());
					continue;
				}
				Thread t = new Tier4InputExecutorThread(client) {
					@Override
					public void run() {
						o.processPacketServer(client, parent);
						if(o.ackCode != null) client.sendPacket(o.buildAckPacket());
					}
				};
				t.setDaemon(true);
				t.start();
			}
			System.out.println("Input Worker interrupted.");
		} catch(EOFException e) {
			Tier4ServerPipeline.logger.info("Client %s has reached end of stream. Assuming disconnect!", client.uuid);
			client.close();
			client.kick("Disconnect");
		} catch(Exception e) {
			Tier4ServerPipeline.logger.severe("Error in worker thread for %s! Disconnecting!", client.uuid);
			Tier4ServerPipeline.logger.severe(e);
			client.kick("Error in worker thread!");
		}
	}


}
