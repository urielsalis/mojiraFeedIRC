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

import nedhyett.crimson.logging.CrimsonLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents and constructs a response to send to the client.
 *
 * @author Ned Hyett
 */
public class HttpResponse extends OutputStream {

	public static HttpResponse createOneLiner(String text) {
		HttpResponse response = new HttpResponse();
		response.addResponseBodyLine(text);
		return response;
	}

	public HttpStatusCodes code;

	/**
	 * List of headers to be sent to the client.
	 */
	private final ArrayList<String> headers = new ArrayList<>();

//	private final ArrayList<Cookie> cookies = new ArrayList<>();

	/**
	 * The response body, this is where the file to be sent goes.
	 */
	private final ByteArrayOutputStream response_body = new ByteArrayOutputStream();

	/**
	 * Indicates that the header/status code has not been written. Prevents the creation of response data.
	 */
	private boolean headless = true;

	/**
	 * Indicates that the response has been written out, preventing subsequent uses of the instance.
	 */
	private boolean written_out = false;

	public HttpResponse() {
		this(HttpStatusCodes._200);
	}

	public HttpResponse(int responseCode) {
		this(HttpStatusCodes.getCodeForNumber(responseCode));
	}

	/**
	 * Create a new HttpResponse.
	 *
	 * @param responseCode
	 */
	public HttpResponse(HttpStatusCodes responseCode) {
		addResponseCode(responseCode);
	}

	public HttpResponse(int responseCode, String... headers) {
		this(HttpStatusCodes.getCodeForNumber(responseCode), headers);
	}

	/**
	 * Create a new HttpResponse with stringified headers.
	 *
	 * @param responseCode
	 * @param headers
	 */
	public HttpResponse(HttpStatusCodes responseCode, String... headers) {
		this(responseCode);
		this.headers.addAll(Arrays.asList(headers));
	}

	public HttpResponse(String... headers) {
		this.headers.addAll(Arrays.asList(headers));
	}

	/**
	 * Add a status code to this response.
	 *
	 * @param responseCode
	 */
	public void addResponseCode(HttpStatusCodes responseCode) {
		if(!headless) return;
		headers.add(0, responseCode.getCode());
		code = responseCode;
		headless = false;
	}

	public void setResponseCode(HttpStatusCodes responseCode) {
		if(headless) return;
		headers.set(0, responseCode.getCode());
		code = responseCode;
	}

	/**
	 * Add a response header to the list.
	 *
	 * @param key   The key to assign
	 * @param value The value to assign
	 */
	public void addResponseHeader(String key, Object value) {
		headers.add(key + ": " + value);
	}

	/**
	 * Add a response body line to the list.
	 *
	 * @param line The line to add
	 */
	public void addResponseBodyLine(String line) {
		try {
			response_body.write(line.getBytes());
		} catch(IOException ex) {
			CrimsonLog.severe("Error while adding response body line: ");
			CrimsonLog.severe(ex);
		}
	}

	/**
	 * Append a byte array to the response body.
	 *
	 * @param b
	 */
	public void addResponseBytes(byte[] b) {
		try {
			response_body.write(b);
		} catch(IOException ex) {
			CrimsonLog.severe("Error while adding response body bytes: ");
			CrimsonLog.severe(ex);
		}
	}

	public byte[] getResponseBytes() {
		return response_body.toByteArray();
	}

	@Override
	public void write(byte[] b) throws IOException {
		response_body.write(b);
	}

	@Override
	public void write(int b) throws IOException {
		response_body.write(b);
	}

	/**
	 * Clear the response body.
	 */
	public void clearResponse() {
		response_body.reset();
	}

	public void clearHead() {
		headers.clear();
		headless = true;
	}

	public void writeOutHead(OutputStream out) throws IOException {
		for(String header : headers) out.write((header + "\r\n").getBytes());
//		for (Cookie cookie : cookies) {
//			cookie.writeOut(out);
//		}
		out.write("\r\n".getBytes());
	}


	/**
	 * Write out this request to the provided OutputStream
	 *
	 * @param out The OutputStream to write this request to.
	 *
	 * @throws IOException
	 */
	public void writeOut(OutputStream out) throws IOException {
		if(written_out) throw new IllegalStateException("Already written out the response!");
		if(headless) throw new IllegalStateException("Can't write out headless response!");
		writeOutHead(out);
		if(response_body.toByteArray().length == 0 && code != HttpStatusCodes._200) code.generatePage(this);
		out.write(response_body.toByteArray());
		written_out = true;
	}

//	/**
//	 * Add a cookie to this response (set it)
//	 *
//	 * @param cookie The cookie to add
//	 */
//	public void addCookie(Cookie cookie) {
//		cookies.add(cookie);
//	}
//
//	/**
//	 * Pull a cookie from this response.
//	 *
//	 * @param name The name of the cookie
//	 * @return The cookie from this response, null if not found.
//	 */
//	public Cookie getCookie(String name) {
//		for (Cookie c : cookies) {
//			if (c.name.equalsIgnoreCase(name)) return c;
//		}
//		return null;
//	}

}
