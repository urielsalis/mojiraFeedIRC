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

package nedhyett.crimson.networking.http.server.provider;

import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.HttpStatusCodes;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.server.IHttpServerProvider;
import nedhyett.crimson.utility.InternetUtils;
import nedhyett.crimson.utility.StreamUtils;

import java.net.HttpURLConnection;
import java.net.Socket;

/**
 * Proxies all requests to a remote web server.
 * <p>
 * (Created on 20/05/2015)
 *
 * @author Ned Hyett
 */
public class ProxyProvider implements IHttpServerProvider {

	/**
	 * The target URL to proxy to.
	 */
	public final String target;

	public ProxyProvider(String target) {
		this.target = target;
	}

	@Override
	public HttpResponse handle(HttpRequest request, HttpQueryData data, Socket s) throws Exception {
		HttpResponse response = new HttpResponse();
		HttpURLConnection connection = InternetUtils.getConnection(target + request.rawPage);
		connection.setRequestMethod("GET");
		connection.setInstanceFollowRedirects(true);
		response.addResponseCode(HttpStatusCodes.getCodeForNumber(connection.getResponseCode()));
		connection.connect();
		StreamUtils.bridge(connection.getInputStream(), response);
		return response;
	}

	@Override
	public boolean requestIsValid(HttpRequest request, Socket socket) {
		return true;
	}
}
