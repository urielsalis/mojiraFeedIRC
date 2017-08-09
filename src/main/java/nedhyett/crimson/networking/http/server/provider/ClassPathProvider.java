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
import nedhyett.crimson.networking.http.MimeTypeLibrary;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.server.IHttpServerProvider;
import nedhyett.crimson.utility.StreamUtils;
import nedhyett.crimson.utility.StringUtils;

import java.io.InputStream;
import java.net.Socket;

/**
 * Retrieves items from the classpath, attempts to identify the mime type, and wraps them into a response.
 * <p>
 * (Created on 19/05/2015)
 *
 * @author Ned Hyett
 */
public class ClassPathProvider implements IHttpServerProvider {

	/**
	 * The root path will be prepended to all requests made.
	 */
	public final String rootPath;

	/**
	 * Create a ClassPathProvider with default settings.
	 */
	public ClassPathProvider() {
		this("");
	}

	/**
	 * Create a ClassPathProvider with a root path.
	 *
	 * @param rootPath the root path to use.
	 */
	public ClassPathProvider(String rootPath) {
		this.rootPath = rootPath;
	}

	@Override
	public HttpResponse handle(HttpRequest request, HttpQueryData data, Socket s) throws Exception {
		HttpResponse response = new HttpResponse();
		response.addResponseHeader("Content-Type", request.page.endsWith("/") ? MimeTypeLibrary.resolve("html") + "; charset=utf-8" : MimeTypeLibrary.resolve(StringUtils.getFileExtension(request.page)) + "; charset=utf-8");
		InputStream in = request.page.endsWith("/") ? ClassLoader.getSystemResourceAsStream(rootPath + request.page + "index.html") : ClassLoader.getSystemResourceAsStream(rootPath + request.page);
		if(in == null) return new HttpResponse(HttpStatusCodes._404);
		response.addResponseHeader("Content-Length", in.available());
		StreamUtils.bridge(in, response);
		return response;
	}

	@Override
	public boolean requestIsValid(HttpRequest request, Socket socket) {
		return (request.page.endsWith("/") ? ClassLoader.getSystemResourceAsStream(rootPath + request.page + "index.html") : ClassLoader.getSystemResourceAsStream(rootPath + request.page)) != null;
	}
}
