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

package nedhyett.crimson.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream that counts bytes written to it.
 *
 * @author Ned Hyett
 */
public class ByteCounterOutputStream extends OutputStream {

	private final OutputStream delegate;
	private long size = 0L;

	public ByteCounterOutputStream(OutputStream delegate) {
		if(delegate == null) throw new IllegalArgumentException("Delegate cannot be null!");
		this.delegate = delegate;
	}

	@Override
	public void write(int b) throws IOException {
		size++;
		delegate.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		size += len;
		delegate.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		size += b.length;
		delegate.write(b);
	}

	@Override
	public void flush() throws IOException {
		delegate.flush();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	public long size() {
		return size;
	}
}
