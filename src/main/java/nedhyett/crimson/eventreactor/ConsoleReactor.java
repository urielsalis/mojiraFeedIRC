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

import java.util.Scanner;

/**
 * (Created on 09/11/2015)
 *
 * @author Ned Hyett
 */
public class ConsoleReactor extends EventReactor implements Runnable {

	private final Scanner scanner;
	public boolean alive = true;

	public ConsoleReactor(String name) {
		super(name);
		this.scanner = new Scanner(System.in);
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Create a new EventReactor with the specified name. This name will be used
	 * in logging.
	 *
	 * @param name         the name to assign to this reactor.
	 * @param lockedEvents a list of classes that the events must extend to be allowed into this reactor.
	 */
	public ConsoleReactor(String name, Class<? extends IEvent>... lockedEvents) {
		super(name, lockedEvents);
		this.scanner = new Scanner(System.in);
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();

	}

	@Override
	public void run() {
		while(alive) {
			String s = scanner.nextLine();
			this.publish(new ConsoleLineEvent(s));
		}
	}

	public static class ConsoleLineEvent extends Event {

		public final String line;

		public ConsoleLineEvent(String line) {
			this.line = line;
		}

		@Override
		public boolean canCancel() {
			return false;
		}

	}

}
