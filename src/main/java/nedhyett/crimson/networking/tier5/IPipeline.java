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

package nedhyett.crimson.networking.tier5;

import nedhyett.crimson.eventreactor.IEvent;
import nedhyett.crimson.networking.tier4.IPacketMorpher;
import nedhyett.crimson.networking.tier4.packet.AckPacket;
import nedhyett.crimson.utility.Semver;

/**
 * Defines a basic pipeline regardless of whether it is a client or server pipeline.
 *
 * @author Ned Hyett
 */
public interface IPipeline {

	/**
	 * Connect to the target.
	 *
	 * @return
	 */
	boolean connect();

	/**
	 * Disconnect the pipeline from the other side. If this is the client, the socket is simply closed. If this is the
	 * server then each client is kicked and then the socket is shut.
	 */
	void disconnect();

	/**
	 * Get the information for the protocol that this pipeline is handling.
	 *
	 * @return
	 */
	ProtocolInfo getProtocolInfo();

	/**
	 * Register a class that is subscribed to a network event.
	 *
	 * @param o
	 */
	void registerConnectionListener(Object o);

	/**
	 * Unregister a class that is subscribed to a network event.
	 *
	 * @param o
	 */
	void unregisterConnectionListener(Object o);

	void setEncryptionKey(String key);

	String getEncryptionKey();

	void addPacketMorpher(IPacketMorpher morpher);

	void removePacketMorpher(IPacketMorpher morpher);

}
