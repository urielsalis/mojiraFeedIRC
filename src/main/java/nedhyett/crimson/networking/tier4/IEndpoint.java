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

import nedhyett.crimson.threading.IProgressReportTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;

/**
 * Represents a target connection
 *
 * @author Ned Hyett
 */
public interface IEndpoint {

	/**
	 * Get the input stream to read raw data from the endpoint.
	 *
	 * @return
	 */
	DataInputStream getInputStream();

	/**
	 * Get the output stream to send raw data to the endpoint.
	 *
	 * @return
	 */
	DataOutputStream getOutputStream();

	/**
	 * Get the network address of the endpoint.
	 *
	 * @return
	 */
	InetAddress getAddress();

	/**
	 * Check if this connection is still active.
	 *
	 * @return
	 */
	boolean isConnected();

	/**
	 * Close this connection
	 *
	 * @return
	 */
	boolean close();

	/**
	 * Close the connection with a reason
	 *
	 * @param reason
	 *
	 * @return
	 */
	boolean close(String reason);

	/**
	 * Get the reason why this connection was closed.
	 *
	 * @return
	 */
	String getCloseReason();

	/**
	 * Send a packet to the endpoint.
	 *
	 * @param p
	 */
	void sendPacket(Tier4Packet p);

	void sendPacketNow(Tier4Packet p);

	Tier4Packet sendPacketAck(Tier4Packet p, IProgressReportTask... progressListeners);


}
