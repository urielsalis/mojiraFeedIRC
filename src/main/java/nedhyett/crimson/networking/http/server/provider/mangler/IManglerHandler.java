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

package nedhyett.crimson.networking.http.server.provider.mangler;

import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.server.HttpQueryData;

import java.net.Socket;

/**
 * Implements mangler logic for the ManglerProvider.
 *
 * @author Ned Hyett
 * @see ManglerProvider
 * <p>
 * (Created on 14/06/2015)
 */
public interface IManglerHandler {

	/**
	 * Mangles a response to make it appear different for the client. Used to grep/replace parts of text to insert variables and whatnot.
	 *
	 * @param request  the request that was made to generate this response.
	 * @param data     the data that was sent to generate this response.
	 * @param s        the socket that this request was generated from.
	 * @param response the mutable response to change
	 *
	 * @return true to prevent any further processing from other handlers.
	 */
	boolean mangle(HttpRequest request, HttpQueryData data, Socket s, HttpResponse response);

}
