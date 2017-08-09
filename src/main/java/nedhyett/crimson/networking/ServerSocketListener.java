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

package nedhyett.crimson.networking;

import nedhyett.crimson.logging.CrimsonLog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Plug-n-play system to create a standard network server.
 *
 * @author Ned Hyett
 */
public class ServerSocketListener extends Thread {

	private final ServerSocket socket;
	private final IConnectionDelegate delegate;

	/**
	 * Create a new listener
	 *
	 * @param port     the port to listen for connections on
	 * @param delegate the delegate for the server.
	 *
	 * @throws IOException
	 */
	public ServerSocketListener(int port, IConnectionDelegate delegate) throws IOException {
		socket = new ServerSocket(port);
		this.delegate = delegate;
		this.setName("ServerSocketListener on port " + port);
		this.setDaemon(false);
	}

	@Override
	public void run() {
		try {
			while(!this.isInterrupted()) {
				final Socket client = socket.accept();
				Thread t = new Thread() {

					@Override
					public void run() {
						try {
							delegate.handleConnection(client);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}

				};
				t.setDaemon(true);
				t.setName("IConnectionDelegate for " + socket.getInetAddress().getHostAddress() + " on port " + socket.getLocalPort());
				t.start(); //Put the delegate in a new thread to make sure that we can continue accepting connections.
			}
		} catch(Exception e) {
			CrimsonLog.critical("Exception in ServerSocketListener on port %s!", socket.getLocalPort());
			CrimsonLog.critical(e);
		} finally {
			try {
				socket.close();
			} catch(IOException e) {
				CrimsonLog.critical("Failed to close the ServerSocket!");
				CrimsonLog.critical(e);
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		socket.close();
	}
}
