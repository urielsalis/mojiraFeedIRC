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

import nedhyett.crimson.eventreactor.Event;
import nedhyett.crimson.eventreactor.EventReactor;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.HttpStatusCodes;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.server.IHttpServerProvider;
import nedhyett.crimson.networking.http.server.sse.GroupingSSEPool;
import nedhyett.crimson.networking.http.server.sse.SSEStream;

import java.net.Socket;

/**
 * (Created on 30/06/2015)
 *
 * @author Ned Hyett
 */
public class GroupingSSEProvider<T extends SSEStream> implements IHttpServerProvider {

	public static class NewGroupingSSEStreamEvent<T extends SSEStream> extends Event {

		public final String param;
		public final T stream;

		public NewGroupingSSEStreamEvent(String param, T stream) {
			this.param = param;
			this.stream = stream;
		}

		@Override
		public boolean canCancel() {
			return true;
		}

	}

	public static class DisconnectGroupingSSEStreamEvent<T extends SSEStream> extends Event {

		public final String param;
		public final T stream;

		public DisconnectGroupingSSEStreamEvent(String param, T stream) {
			this.param = param;
			this.stream = stream;
		}

	}

	public final String parameter;
	public final GroupingSSEPool<T> pool;
	public final EventReactor reactor;
	public final ISSEStreamFactory<T> factory;

	public GroupingSSEProvider(String parameter, ISSEStreamFactory<T> factory) {
		this(new GroupingSSEPool<T>(), parameter, factory);
	}

	public GroupingSSEProvider(GroupingSSEPool<T> pool, String parameter, ISSEStreamFactory<T> factory) {
		this.pool = pool;
		this.parameter = parameter;
		this.factory = factory;
		reactor = new EventReactor("GroupingSSEProvider - " + parameter, NewGroupingSSEStreamEvent.class, DisconnectGroupingSSEStreamEvent.class);
	}

	@Override
	public HttpResponse handle(HttpRequest request, HttpQueryData data, Socket s) throws Exception {
		if(!data.hasParameter(parameter)) return new HttpResponse(HttpStatusCodes._400);
		T stream = factory.makeStream(data.getParam(parameter));
		pool.addStream(data.getParam(parameter), stream);
		if(!reactor.publish(new NewGroupingSSEStreamEvent<>(data.getParam(parameter), stream))) {
			pool.removeStream(data.getParam(parameter));
			return new HttpResponse(HttpStatusCodes._400);
		}
		stream.handleConnection(s, request);
		reactor.publish(new DisconnectGroupingSSEStreamEvent<>(data.getParam(parameter), stream));
		return new HttpResponse();
	}

	@Override
	public boolean requestIsValid(HttpRequest request, Socket socket) {
		return true;
	}

}
