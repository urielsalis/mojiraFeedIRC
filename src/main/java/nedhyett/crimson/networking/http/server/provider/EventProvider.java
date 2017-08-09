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

import nedhyett.crimson.eventreactor.IEvent;
import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.eventreactor.EventReactor;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.server.IHttpServerProvider;
import nedhyett.crimson.toolbox.ClassToolbox;

import java.net.Socket;
import java.util.HashMap;

/**
 * (Created on 29/02/2016)
 *
 * @author Ned Hyett
 */
public class EventProvider implements IHttpServerProvider {

	public final String base;
	public final EventReactor reactor;
	private final HashMap<String, Class<IHttpEvent>> event_mappings = new HashMap<>();

	public EventProvider(String base) {
		this(base, new EventReactor("EventProvider - " + base));
	}

	public EventProvider(String base, EventReactor reactor) {
		this.base = base;
		this.reactor = reactor;
	}

	public void addEventMapping(String path, Class<IHttpEvent> event) {
		event_mappings.put(path, event);
	}

	public void removeEventMapping(String path) {
		event_mappings.remove(path);
	}

	@Override
	public boolean requestIsValid(HttpRequest request, Socket socket) {
		return request.page.startsWith(base);
	}

	@Override
	public HttpResponse handle(HttpRequest request, HttpQueryData data, Socket s) throws Exception {
		IHttpEvent evt = new ClassToolbox(event_mappings.get(request.page.replace(base, ""))).instance(IHttpEvent.class);
		evt.setRequest(request);
		evt.setSocket(s);
		reactor.publish(evt);
		return evt.getResponse();
	}


	public abstract class IHttpEvent implements IEvent {

		private HttpRequest request;
		private Socket socket;

		public void setRequest(HttpRequest request) {
			this.request = request;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}

		public abstract HttpResponse getResponse();

	}

}
