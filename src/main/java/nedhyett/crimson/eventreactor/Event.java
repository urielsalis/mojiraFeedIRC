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

package nedhyett.crimson.eventreactor;

import java.io.Serializable;

/**
 * An base event implementation that can be posted to an EventReactor and given to all listeners.
 *
 * @author Ned Hyett
 */
public abstract class Event implements Serializable, IEvent {

	/**
	 * Determines if the event was cancelled.
	 */
	private boolean isCancelled = false;

	/**
	 * The reactor that sent this event
	 */
	private EventReactor senderReactor = null;

	@Override
	public boolean canCancel() {
		return false;
	}

	@Override
	public void cancel() {
		if(!this.canCancel()) {
			throw new IllegalStateException("Cannot cancel event!");
		}
		isCancelled = true;
	}

	@Override
	public void uncancel() {
		if(!this.canCancel()) {
			throw new IllegalStateException("Cannot uncancel event!");
		}
		if(!this.returnOnCancel()) {
			throw new IllegalStateException("Cannot uncancel event!");
		}
		isCancelled = false;
	}

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public boolean returnOnCancel() {
		return true;
	}

	@Override
	public boolean setReactor(EventReactor sender) {
		if(this.senderReactor != null) {
			senderReactor.getLogger().warning("Event %s cannot be used in reactor %s because it is configured to use reactor %s!", this, sender, senderReactor);
			return false;
		}
		this.senderReactor = sender;
		return true;
	}

	@Override
	public final EventReactor getSender() {
		return senderReactor;
	}

}
