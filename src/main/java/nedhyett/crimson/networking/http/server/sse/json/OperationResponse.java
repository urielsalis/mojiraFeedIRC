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

package nedhyett.crimson.networking.http.server.sse.json;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.HttpStatusCodes;
import nedhyett.crimson.utility.json.JSONWriter;

import java.io.OutputStream;

/**
 * (Created on 28/10/2015)
 *
 * @author Ned Hyett
 */
public class OperationResponse extends JSONWriter {

	public static HttpResponse flushToResponse(boolean result) {
		return flushToResponse(result, "OK");
	}

	public static HttpResponse flushToResponse(boolean result, String message) {
		return flushToResponse(HttpStatusCodes._200, result, message);
	}

	public static HttpResponse flushToResponse(HttpStatusCodes status, boolean result, String message) {
		HttpResponse response = new HttpResponse(status);
		OperationResponse or = new OperationResponse(response, result, message);
		or.flush();
		return response;
	}

	private boolean flushed = false;

	public OperationResponse() {
		this(new HttpResponse(HttpStatusCodes._200), true, "");
	}

	public OperationResponse(boolean result) { this(new HttpResponse(HttpStatusCodes._200), result, "OK"); }

	public OperationResponse(boolean result, String message) { this(new HttpResponse(HttpStatusCodes._200), result, message); }

	public OperationResponse(OutputStream out) {
		this(out, true, "");
	}

	public OperationResponse(OutputStream out, String message) {
		this(out, true, message);
	}

	public OperationResponse(OutputStream out, boolean result) {
		this(out, result, "OK");
	}

	public OperationResponse(OutputStream out, boolean result, String message) {
		super(out);
		object();
		name("result");
		value(result);
		name("message");
		value(message);
		name("payload");
		object();
	}

	@Override
	public void flush() {
		if(flushed) return;
		endObject();
		endObject();
		flushed = true;
		super.flush();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(!flushed) CrimsonLog.warning("OperationResponse not flushed before destruction! Check your code!");
	}

	public HttpResponse getResponse() {
		if(!(out instanceof HttpResponse)) throw new IllegalStateException("Encapsulated stream is NOT HttpResponse!");
		flush();
		return (HttpResponse)out;
	}
}
