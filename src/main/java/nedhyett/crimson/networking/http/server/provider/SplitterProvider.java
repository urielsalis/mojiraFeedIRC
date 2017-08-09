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

import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.server.IHttpServerProvider;

import java.net.Socket;
import java.util.ArrayList;

/**
 * (Created on 30/06/2015)
 *
 * @author Ned Hyett
 */
public class SplitterProvider implements IHttpServerProvider {

	private final ArrayList<IHttpServerProvider> delegates = new ArrayList<>();

	public void addDelegate(IHttpServerProvider delegate) {
		delegates.add(delegate);
	}

	public void removeDelegate(IHttpServerProvider delegate) {
		delegates.remove(delegate);
	}

	public boolean hasDelegate(IHttpServerProvider delegate) {
		return delegates.contains(delegate);
	}

	@Override
	public boolean requestIsValid(HttpRequest request, Socket socket) {
		for(IHttpServerProvider delegate : delegates) {
			if(delegate.requestIsValid(request, socket)) return true;
		}
		return false;
	}

	@Override
	public HttpResponse handle(HttpRequest request, HttpQueryData data, Socket s) throws Exception {
		for(IHttpServerProvider delegate : delegates) {
			HttpResponse response = delegate.handle(request, data, s);
			if(response != null) return response;
		}
		return null;
	}

}
