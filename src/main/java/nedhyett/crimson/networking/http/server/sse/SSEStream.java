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

package nedhyett.crimson.networking.http.server.sse;

import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.server.sse.json.JSONMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents the connection to the client through the SSE protocol.
 * <p>
 * (Created on 20/03/2015)
 *
 * @author Ned
 */
public class SSEStream {

	/**
	 * The the last pool that this stream was added to (if any).
	 */
	protected ISSEPool<SSEStream> pool = null;

	/**
	 * Flag to indicate that this stream should disconnect during the next cycle.
	 */
	protected boolean disconnected = false;

	private Socket socket;

	/**
	 * The thread-safe queue of data to send to the client. Do not assume that messages are sent on a first-come-first-serve
	 * basis. They may not be.
	 */
	private final CopyOnWriteArrayList<Object> queue = new CopyOnWriteArrayList<>();

	/**
	 * Queue new data to be sent to the client. JSONMessages will be automatically converted into a string.
	 *
	 * @param data the data to queue.
	 */
	public void queueData(Object data) {
		if(data instanceof JSONMessage) {
			queue.add(((JSONMessage) data).write().replace("\n", "").replace("\t", ""));
		} else {
			queue.add(data);
		}
		synchronized(this) {
			notify();
		}
	}

	/**
	 * Call this with the socket when a client requests a page that triggers this behaviour. Do not
	 * trigger this method on the main server thread; it blocks until the client disconnects.
	 *
	 * @param s
	 * @param request
	 */
	public void handleConnection(Socket s, HttpRequest request) {
		this.socket = s;
		PrintWriter pw;
		try {
			pw = new PrintWriter(s.getOutputStream(), true);
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
		HttpResponse response = new HttpResponse();
		response.addResponseHeader("Content-Type", "text/event-stream");
		response.addResponseHeader("Cache-Control", "no-cache");
		try {
			response.writeOutHead(s.getOutputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
		while(s.isConnected()) {
			if(pw.checkError()) break;
			if(disconnected) break;
			if(queue.isEmpty()) {
				try {
					synchronized(this) {
						wait();
					}
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			try {
				pw.print("data: " + queue.remove(0) + "\n\n");
			} catch(Exception ignored) {
				ignored.printStackTrace();
			}
		}
		onDisconnect();
		if(pool != null) {
			pool.removeStream(this);
		}
	}

	/**
	 * Force the stream to disconnect during the next cycle. To disconnect immediately, try pushing some data.
	 */
	public void disconnect() {
		disconnected = true;
		synchronized(this) {
			notify();
		}
	}

	/**
	 * Override this in subclasses to handle what happens when the stream is disconnected.
	 */
	public void onDisconnect() {

	}

	public boolean isConnected() {
		if(socket != null) {
			return socket.isInputShutdown();
		}
		return false;
	}

	public void resetConnection() {
		disconnected = false;
	}

}
