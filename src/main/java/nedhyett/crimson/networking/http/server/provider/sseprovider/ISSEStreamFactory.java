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

package nedhyett.crimson.networking.http.server.provider.sseprovider;

import nedhyett.crimson.networking.http.server.sse.SSEStream;

/**
 * Defines a factory for stream types used by the (Named)SSEProvider.
 *
 * @author Ned Hyett
 * @see NamedSSEProvider
 * @see SSEProvider
 * <p>
 * (Created on 10/16/2015)
 */
public interface ISSEStreamFactory<T extends SSEStream> {

	/**
	 * Build a stream for the provider. If this factory is assigned to a <code>SSEProvider</code>, the <code>param</code>
	 * parameter will always be null.
	 *
	 * @param param the value of the parameter that the provider extracted from the query.
	 *
	 * @return the new stream.
	 */
	T makeStream(String param);

}
