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
import nedhyett.crimson.networking.tier4.event.DisconnectedFromServerEvent;

/**
 * Sent by the server to the client to notify them that they have been disconnected. Also sent by the client to the server
 * to request a disconnect.
 * <p>
 * This class can be serialised and used in a Tier4 exchange.
 *
 * @author Ned Hyett
 */
public class DisconnectPacket extends Tier4Packet {

	public String reason;

	@Deprecated
	@NetworkConstructor
	public DisconnectPacket() {

	}

	public DisconnectPacket(String reason) {
		this.reason = reason;
	}

	@Override
	public void processPacketServer(Tier4Client c, Tier4ServerPipeline p) {
		c.kick(reason);
	}

	@Override
	public void processPacketClient(Tier4ClientPipeline p) {
		p.publishEvent(new DisconnectedFromServerEvent(p, reason));
		p.close(reason);
		p.unlockLatch();
	}

	@Override
	public boolean canProcessOnSide(Side side) {
		return true;
	}
}
