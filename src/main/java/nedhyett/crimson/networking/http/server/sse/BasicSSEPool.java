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

package nedhyett.crimson.networking.http.server.sse;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implements a basic set of SSE streams that is addressed by stream object only (advanced ArrayList of SSEStreams).
 * <p>
 * This object is thread-safe. Accessing the streams field directly may be a bad idea.
 *
 * @author Ned Hyett
 * @see SSEStream
 * @see ISSEPool
 * <p>
 * (Created on 20/03/2015)
 */
public class BasicSSEPool<T extends SSEStream> implements ISSEPool<T> {

	public final CopyOnWriteArrayList<T> streams = new CopyOnWriteArrayList<>();

	public void addStream(T stream) {
		streams.add(stream);
	}

	public void removeStream(T stream) {
		streams.remove(stream);
	}

	public void queueDataOnAll(Object data) {
		for(T s : streams) s.queueData(data);
	}

}
