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

package nedhyett.crimson.networking.tier4.event;

import nedhyett.crimson.networking.tier4.Tier4Client;
import nedhyett.crimson.networking.tier4.Tier4Event;
import nedhyett.crimson.networking.tier4.Tier4ServerPipeline;

/**
 * Fired on the server to indicate that a new client has connected.
 *
 * @author Ned Hyett
 */
public class ClientConnectedEvent extends Tier4Event {

	public final Tier4Client client;
	public final Tier4ServerPipeline pipeline;

	public ClientConnectedEvent(Tier4Client client, Tier4ServerPipeline pipeline) {
		this.client = client;
		this.pipeline = pipeline;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

}
