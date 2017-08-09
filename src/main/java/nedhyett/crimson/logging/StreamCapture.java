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

package nedhyett.crimson.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple styler for captured streams.
 *
 * @author Ned Hyett
 */
public class StreamCapture extends ByteArrayOutputStream {

	private final StringBuilder c = new StringBuilder();
	private final MiniLogger minilog;
	private boolean severe = false;

	public StreamCapture(MiniLogger minilog) {
		this.minilog = minilog;
	}

	public StreamCapture(MiniLogger minilog, boolean severe) {
		this(minilog);
		this.severe = severe;
	}

	@Override
	public void flush() throws IOException {
		String r;
		super.flush();
		r = this.toString();
		super.reset();
		c.append(r.replace(System.getProperty("line.separator"), "\n"));
		int li = -1;
		int i = c.indexOf("\n", li + 1);
		while(i >= 0) {
			if(!severe) {
				minilog.info(c.substring(li + 1, i));
			} else {
				minilog.severe(c.substring(li + 1, i));
			}
			li = i;
			i = c.indexOf("\n", li + 1);
		}
		if(li >= 0) c.setLength(0);
	}

}
