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

import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.server.sse.SSEStream;
import nedhyett.crimson.eventreactor.Event;
import nedhyett.crimson.eventreactor.EventReactor;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.HttpStatusCodes;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.server.IHttpServerProvider;
import nedhyett.crimson.networking.http.server.sse.NamedSSEPool;

import java.net.Socket;

/**
 * Wraps a NamedSSEPool and automatically handles the addition and removal of streams from it depending on a parameter in
 * the request url.
 *
 * @author Ned Hyett
 * @see ISSEStreamFactory
 * <p>
 * (Created on 20/05/2015)
 */
public class NamedSSEProvider<T extends SSEStream> implements IHttpServerProvider {

	public static class NewNamedSSEStreamEvent<T extends SSEStream> extends Event {

		public final String param;
		public final T stream;

		public NewNamedSSEStreamEvent(String param, T stream) {
			this.param = param;
			this.stream = stream;
		}

		@Override
		public boolean canCancel() {
			return true;
		}

	}

	public static class DisconnectNamedSSEStreamEvent<T extends SSEStream> extends Event {

		public final String param;
		public final T stream;

		public DisconnectNamedSSEStreamEvent(String param, T stream) {
			this.param = param;
			this.stream = stream;
		}

	}

	public final String parameter;
	public final NamedSSEPool<T> pool = new NamedSSEPool<>();
	public final EventReactor reactor;
	public final ISSEStreamFactory<T> factory;

	public NamedSSEProvider(String parameter, ISSEStreamFactory<T> factory) {
		this.parameter = parameter;
		this.factory = factory;
		reactor = new EventReactor("NamedSSEProvider - " + parameter, NewNamedSSEStreamEvent.class, DisconnectNamedSSEStreamEvent.class);
	}

	@Override
	public HttpResponse handle(HttpRequest request, HttpQueryData data, Socket s) throws Exception {
		if(!data.hasParameter(parameter)) return new HttpResponse(HttpStatusCodes._400);
		T stream = factory.makeStream(data.getParam(parameter));
		pool.addStream(data.getParam(parameter), stream);
		if(!reactor.publish(new NewNamedSSEStreamEvent<>(data.getParam(parameter), stream))) {
			pool.removeStream(data.getParam(parameter));
			return new HttpResponse(HttpStatusCodes._400);
		}
		stream.handleConnection(s, request);
		reactor.publish(new DisconnectNamedSSEStreamEvent<>(data.getParam(parameter), stream));
		return new HttpResponse();
	}

	@Override
	public boolean requestIsValid(HttpRequest request, Socket socket) {
		return true;
	}
}
