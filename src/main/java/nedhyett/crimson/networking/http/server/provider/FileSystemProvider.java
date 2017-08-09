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

import nedhyett.crimson.networking.http.MimeTypeLibrary;
import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.HttpStatusCodes;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.server.IHttpServerProvider;
import nedhyett.crimson.utility.StreamUtils;
import nedhyett.crimson.utility.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 * Wraps the filesystem and sends files available to it as a response, after attempting to identify the mime type.
 * <p>
 * (Created on 19/05/2015)
 *
 * @author Ned Hyett
 */
public class FileSystemProvider implements IHttpServerProvider {

	/**
	 * The root path to prepend to all requests made.
	 */
	public final String rootPath;

	/**
	 * Create a FileSystemProvider with default settings.
	 */
	public FileSystemProvider() {
		this("");
	}

	/**
	 * Create a FileSystemProvider with a root path.
	 *
	 * @param rootPath the root path to use.
	 */
	public FileSystemProvider(String rootPath) {
		this.rootPath = rootPath;
	}


	@Override
	public HttpResponse handle(HttpRequest request, HttpQueryData data, Socket s) throws Exception {
		String aPath = (new File(rootPath + request.page).isDirectory() ? rootPath + request.page + "/index.html" : rootPath + request.page);
		CrimsonLog.debug(aPath);
		if(!new File(aPath).exists()) return new HttpResponse(HttpStatusCodes._404);
		HttpResponse response = new HttpResponse();
		response.addResponseHeader("Content-Type", new File(rootPath + request.page).isDirectory() ? MimeTypeLibrary.resolve("html") + "; charset=utf-8" : MimeTypeLibrary.resolve(StringUtils.getFileExtension(request.page)) + "; charset=utf-8");
		InputStream in = null;
		try {
			in = new FileInputStream(aPath);
		} catch(Exception ignored) {

		}
		if(in == null) return new HttpResponse(HttpStatusCodes._404);
		StreamUtils.bridge(in, response);
		return response;
	}

	@Override
	public boolean requestIsValid(HttpRequest request, Socket socket) {
		String aPath = (request.page.endsWith("/") ? rootPath + request.page + "index.html" : rootPath + request.page);
		return new File(aPath).exists();
	}
}
