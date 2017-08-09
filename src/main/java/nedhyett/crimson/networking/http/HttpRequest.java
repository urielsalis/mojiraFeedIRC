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

package nedhyett.crimson.networking.http;

import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.utility.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Parses HTTP requests for the HttpServer.
 *
 * @author Ned Hyett
 */
public class HttpRequest {

	/**
	 * The method type that is being used for the request.
	 */
	public final HttpMethod method;

	public String rawPage;

	/**
	 * The page that is being requested from the server.
	 */
	public String page;

	/**
	 * The protocol that is being used (i.e. HTTP/1.1)
	 */
	public String protocol;

	//public final ArrayList<Cookie> cookies = new ArrayList<>();

	/**
	 * The raw header lines that are sent to the server.
	 */
	private final HashMap<String, String> rawHeaders = new HashMap<>();

	/**
	 * The expected content length.
	 */
	public int contentLength;

	/**
	 * A list of data sent to the server.
	 */
	public HttpQueryData queryData = null;

	public HttpRequest(HttpMethod method, String page) {
		this.method = method;
		this.page = page;
	}

	public HttpRequest(List<String> requestData) {
		for(int i = 1; i < requestData.size(); i++) {
			String[] split = requestData.get(i).split(": ");
			rawHeaders.put(split[0], split[1]);
		}
		method = HttpMethod.getMethod(requestData.get(0).split(" ")[0]);
		String localPage = requestData.get(0).split(" ")[1].replace("%20", " "); //TODO: Make this more robust
		if(localPage.indexOf('?') != -1) {
			String[] split = StringUtils.robustSplit(localPage, "?");
			updateGetData(split[1]);
			page = split[0];
		} else {
			page = localPage;
			updateGetData("");
		}
		rawPage = localPage;
		protocol = requestData.get(0).split(" ")[0];
		if(hasHeader("Content-Length") && StringUtils.isNumber(getHeader("Content-Length"))) {
			contentLength = Integer.parseInt(getHeader("Content-Length"));
		} else {
			contentLength = -1;
		}
	}

	public HttpQueryData getQueryData() {
		return queryData;
	}

	public void updateGetData(String raw) {
		queryData = new HttpQueryData(raw);
	}

	public void updatePostData(byte[] data) {
		this.queryData = new HttpQueryData(new String(data));
	}

	public Set<String> getHeaderKeys() {
		return rawHeaders.keySet();
	}

	public boolean hasHeader(String key) {
		for(String s : rawHeaders.keySet()) {
			if(s.equalsIgnoreCase(key)) return true;
		}
		return false;
	}

	public String getHeader(String key) {
		if(!hasHeader(key)) return null;
		for(String s : rawHeaders.keySet()) {
			if(s.equalsIgnoreCase(key)) return rawHeaders.get(s);
		}
		return null;
	}

}
