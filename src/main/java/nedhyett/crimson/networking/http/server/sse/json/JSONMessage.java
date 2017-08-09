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

import nedhyett.crimson.utility.json.JSONWriter;

import java.io.ByteArrayOutputStream;

/**
 * Allows SSE messages to be encoded in JSON instead of just plaintext. The SSEStream class implements logic for handling
 * this class specifically.
 * <p>
 * TODO: make SSEStream only call this once inside a SSEPool instead of for every client.
 * <p>
 * (Created on 25/03/2015)
 *
 * @author Ned Hyett
 */
public abstract class JSONMessage {

	public final transient String type;

	protected JSONMessage(String type) {
		this.type = type;
	}

	/**
	 * Write out the JSONMessage as a string.
	 * <p>
	 * The information provided in <code>writeExtraData(JSONWriter)</code> will be encoded in an object.
	 *
	 * @return
	 */
	public final String write() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JSONWriter writer = new JSONWriter(baos);
		try {
			writer.object();
			writer.name("Type");
			writer.value(type);
			writer.name("Payload");
			writer.object();
			writeExtraData(writer);
			writer.endObject();
			writer.endObject();
			writer.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new String(baos.toByteArray()).replaceAll("[\u0000-\u001f]", "");
	}

	/**
	 * Add extra data to the JSON object. This is where you place the information that you want to send to the client.
	 * <p>
	 * Notice: do <b>NOT</b> include a variable on the top level called "Type" as this will conflict with the payload system.
	 *
	 * @param writer the writer to write the information to.
	 *
	 * @throws Exception
	 */
	protected void writeExtraData(JSONWriter writer) throws Exception {
		writer.writeClass(this);
	}

}
