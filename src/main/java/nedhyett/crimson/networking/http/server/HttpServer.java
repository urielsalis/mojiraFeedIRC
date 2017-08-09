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

package nedhyett.crimson.networking.http.server;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.networking.IConnectionDelegate;
import nedhyett.crimson.networking.ServerSocketListener;
import nedhyett.crimson.networking.http.HttpMethod;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.utility.ArrayUtils;
import nedhyett.crimson.utility.GenericUtils;
import nedhyett.crimson.logging.MiniLogger;
import nedhyett.crimson.networking.http.HttpStatusCodes;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Implements a basic multithreaded HTTP server.
 * <p>
 * The server runs on one thread, and then each connection is handled in a separate thread so there is no delay.
 * It also means that if content is being streamed to a client, other clients can still connect to the server.
 *
 * @author Ned Hyett
 */
public class HttpServer implements IConnectionDelegate, IProviderAcceptor {

	private static final byte[] header_break = "\r\n\r\n".getBytes();
	private static final MiniLogger log = CrimsonLog.spawnLogger("HttpServer");

	private final ArrayList<IHttpServerProvider> providers = new ArrayList<>();
	private final ArrayList<IRequestMangler> requestManglers = new ArrayList<>();

	private final ServerSocketListener listener;

	/**
	 * Create a HTTP server on the specified port.
	 *
	 * @param port the port to bind to.
	 *
	 * @throws IOException
	 */
	public HttpServer(int port) throws IOException {
		listener = new ServerSocketListener(port, this);
	}

	@Override
	public void addProvider(IHttpServerProvider provider) {
		providers.add(provider);
	}

	@Override
	public void removeProvider(IHttpServerProvider provider) {
		providers.remove(provider);
	}

	/**
	 * Add a request mangler to mangle requests before the server handles them.
	 *
	 * @param mangler
	 */
	public void addRequestMangler(IRequestMangler mangler) {
		requestManglers.add(mangler);
	}

	/**
	 * Remove a mangler.
	 *
	 * @param mangler
	 */
	public void removeRequestMangler(IRequestMangler mangler) {
		requestManglers.remove(mangler);
	}

	/**
	 * Start the HTTP Server.
	 */
	public void start() {
		listener.start();
	}

	/**
	 * Stop the HTTP Server.
	 */
	public void stop() {
		listener.interrupt();
	}

	@Override
	public void handleConnection(Socket socket) throws Exception {
		//log.debug("Connection from %s", socket.getInetAddress().getHostAddress());
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		try {
			ArrayList<String> requestData = new ArrayList<>();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int time = GenericUtils.getSecondTime();
			while(true) {
				if(ArrayUtils.compareLast(baos.toByteArray(), header_break)) break;
				if(new String(baos.toByteArray()).endsWith("\r\n\r\n")) break;
				baos.write(in.read());
				if(GenericUtils.getSecondTime() > (time + 15)) {
					log.warning("Read timeout for " + socket.getInetAddress().getHostAddress() + " (" + new String(baos.toByteArray()) + ")");
					return;
				}
			}
			Scanner headerScanner = new Scanner(new ByteArrayInputStream(new String(baos.toByteArray()).trim().getBytes()));
			String line;
			while(headerScanner.hasNextLine() && (line = headerScanner.nextLine()) != null) requestData.add(line);
			HttpRequest request = new HttpRequest(requestData);
			for(IRequestMangler mangler : requestManglers) mangler.mangle(request);
			HttpResponse response = new HttpResponse(HttpStatusCodes._404);
			if(request.method == HttpMethod.POST) {
				byte[] buf = new byte[request.contentLength];
				for(int i = 0; i < buf.length; i++) buf[i] = (byte) in.read();
				request.updatePostData(buf);
			}
			for(IHttpServerProvider provider : providers) {
				if(!provider.requestIsValid(request, socket)) continue;
				HttpResponse response1 = provider.handle(request, request.getQueryData(), socket);
				if(response1 != null && response1.code != HttpStatusCodes._404) {
					response = response1;
					break;
				}
			}
			response.writeOut(out);
		} catch(Exception e) {
			e.printStackTrace();
			HttpResponse response = new HttpResponse(HttpStatusCodes._500);
			response.addResponseHeader("Content-Type", "text/html");
			response.addResponseBodyLine("Error while handling request!<br><br><br>Stack Trace:<br>");
			for(String s : GenericUtils.pullException(e)) response.addResponseBodyLine(s + "<br>");
			try {
				response.writeOut(socket.getOutputStream());
			} catch(Exception e1) {
				// swallow
			}
		}
		socket.close();
	}
}
