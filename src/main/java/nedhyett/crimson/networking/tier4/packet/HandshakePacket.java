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

package nedhyett.crimson.networking.tier4.packet;

import nedhyett.crimson.networking.tier4.*;
import nedhyett.crimson.utility.Semver;
import nedhyett.crimson.networking.tier4.event.ClientConnectedEvent;

/**
 * Allows the client/server to verify that they are compatible with each other.
 * <p>
 * This class can be serialised and used in a Tier4 exchange.
 *
 * @author Ned Hyett
 */
public class HandshakePacket extends Tier4Packet {

	public String uuid;
	public String protocolName;
	public Semver protocolVersion;

	@Deprecated //Used only for serialization!
	@NetworkConstructor
	public HandshakePacket() {

	}

	public HandshakePacket(String uuid) {
		this(uuid, null, null);
	}

	public HandshakePacket(String uuid, String protocolName, Semver protocolVersion) {
		this.uuid = uuid;
		this.protocolName = protocolName;
		this.protocolVersion = protocolVersion;
	}

	@Override
	public void processPacketServer(Tier4Client c, Tier4ServerPipeline p) {
		if(!protocolName.equals(p.getProtocolName())) {
			c.kick("Incompatible protocol type!");
			return;
		}
		if(!protocolVersion.isSafe(p.getProtocolVersion())) {
			c.kick("Incompatible protocol version!");
			return;
		}
		p.publishEvent(new ClientConnectedEvent(c, p));
		c.sendPacket(new ConnectPacket());
		//System.out.println("SERVER HANDSHAKE");
	}

	@Override
	public void processPacketClient(Tier4ClientPipeline p) {
		//Communicate the UUID sent by the server to somewhere for storage, how?
		//Respond with a new Handshake packet detailing our connection details. At this point we don't know what version the server is. This is intentional to prevent version bias!
		HandshakePacket newPacket = new HandshakePacket(uuid, p.getProtocolName(), p.getProtocolVersion());
		p.sendPacket(newPacket);
		//System.out.println("CLIENT HANDSHAKE");
	}

	@Override
	public boolean canProcessOnSide(Side side) {
		return true;
	}
}
