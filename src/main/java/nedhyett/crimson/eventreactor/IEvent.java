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

/**
 * Allows custom events to be written.
 *
 * @author Ned Hyett
 */
public interface IEvent {

	/**
	 * Return true here if this Event can be cancelled by the listener.
	 * Defaults to false.
	 *
	 * @return true if the event can be cancelled.
	 */
	boolean canCancel();

	/**
	 * Used by listeners to cancel the event. Throws an IllegalStateException
	 * if the event cannot be cancelled.
	 */
	void cancel();

	/**
	 * Used by listeners to uncancel the event if it was previously cancelled by a
	 * different listener.
	 * Throws an IllegalStateException if the event cannot be cancelled or
	 * if it cannot be uncancelled (i.e. if returnOnCancel() returns false)
	 */
	void uncancel();

	/**
	 * Check if this event has been cancelled.
	 *
	 * @return true if the event was cancelled.
	 */
	boolean isCancelled();

	/**
	 * Return false here to allow all listeners to receive the event even if
	 * it was cancelled. Also allows for the event to be uncancelled.
	 *
	 * @return true if the event should not be passed further into the reactor after being cancelled.
	 */
	boolean returnOnCancel();

	/**
	 * Used internally by the EventReactor to update the event.
	 *
	 * @param sender the reactor that the event was published to.
	 *
	 * @return true if the event is fresh, or false if the event is being re-used and should not be sent into the reactor.
	 */
	boolean setReactor(EventReactor sender);

	/**
	 * Get the reactor that sent this event. Helpful if registered to multiple reactors.
	 *
	 * @return the reactor that published this event.
	 */
	EventReactor getSender();

}
