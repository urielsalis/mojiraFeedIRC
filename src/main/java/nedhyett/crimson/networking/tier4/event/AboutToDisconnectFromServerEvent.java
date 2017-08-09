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

import nedhyett.crimson.networking.tier4.Tier4ClientPipeline;
import nedhyett.crimson.networking.tier4.Tier4Event;

/**
 * Fired on the client just before it shuts down the socket. Enables last-second packets to be sent to the server.
 * <p>
 * Note: this is not fired if the server disconnects us.
 *
 * @author Ned Hyett
 */
public class AboutToDisconnectFromServerEvent extends Tier4Event {

	public final Tier4ClientPipeline pipeline;

	public AboutToDisconnectFromServerEvent(Tier4ClientPipeline pipeline) {
		this.pipeline = pipeline;
	}

}
