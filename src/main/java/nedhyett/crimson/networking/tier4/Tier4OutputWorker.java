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

import nedhyett.crimson.utility.GenericUtils;

import java.io.DataOutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Runs on the server and pushes packets sequentially into the output stream and then destroys itself afterwards.
 *
 * @author Ned Hyett
 */
public class Tier4OutputWorker extends Thread {

	public final CopyOnWriteArrayList<Tier4Packet> packets = new CopyOnWriteArrayList<>();
	private final DataOutputStream out;
	private final Tier4Client parent;
	private boolean processedFirstPacket = false;

	public Tier4OutputWorker(Tier4Client client) {
		this.setName("Output Worker " + client.uuid);
		this.setDaemon(true);
		this.parent = client;
		this.out = client.getOutputStream();
	}

	@Override
	public void run() {
		while(!packets.isEmpty() || !processedFirstPacket) {
			while(packets.isEmpty()) { //Don't exit early!
				if(packets.isEmpty() && processedFirstPacket) return;
				GenericUtils.wait(1);
			}
			processedFirstPacket = true;
			try {
				Tier4Utils.writePacket(out, packets.remove(0));
			} catch(Exception e) {
				System.out.println("Error in Tier4 Output Thread:");
				e.printStackTrace();
			}
		}
	}

}
