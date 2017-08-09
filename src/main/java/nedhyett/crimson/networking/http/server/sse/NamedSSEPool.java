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

import nedhyett.crimson.utility.ArrayUtils;
import nedhyett.crimson.eventreactor.EventReactor;
import nedhyett.crimson.types.BooleanCallback;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.ArrayList;
import java.util.Map;

/**
 * Implements a set of SSEStreams that are referenced by a string identifier.
 * <p>
 * This object is thread-safe.
 * <p>
 * (Created on 25/03/2015)
 *
 * @author Ned Hyett
 */
public class NamedSSEPool<T extends SSEStream> implements ISSEPool<T> {

	protected NonBlockingHashMap<String, T> streams = new NonBlockingHashMap<>();
	public final EventReactor reactor = new EventReactor("NamedSSEPool");

	/**
	 * Add a stream to the pool.
	 *
	 * @param name   the name of the stream.
	 * @param stream the stream.
	 */
	public void addStream(String name, T stream) {
		streams.put(name, stream);
	}

	/**
	 * Remove a stream from the pool.
	 *
	 * @param stream the stream.
	 */
	public void removeStream(T stream) {
		String name = ArrayUtils.flip(streams).get(stream);
		try {
			reactor.publish(new NamedSSEStreamRemovedEvent(name, streams.remove(name)));
		} catch (Exception e) {}
	}

	/**
	 * Remove a stream from the pool.
	 *
	 * @param name the name of the stream.
	 */
	public void removeStream(String name) {
		reactor.publish(new NamedSSEStreamRemovedEvent(name, streams.remove(name)));
	}

	/**
	 * Check if a stream by the provided name is registered.
	 *
	 * @param name the name of the stream.
	 *
	 * @return true if the stream exists.
	 */
	public boolean hasStream(String name) {
		return streams.containsKey(name);
	}

	/**
	 * Get the stream instance by name.
	 *
	 * @param name the name of the stream.
	 *
	 * @return the stream.
	 */
	public T getStream(String name) {
		try {
			return streams.get(name);
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Get all streams in the pool. Note: don't use this too much; it copies all the streams into a new list in the
	 * process of converting a collection to a list.
	 *
	 * @return the list of streams.
	 */
	public ArrayList<T> getAllStreams() {
		return new ArrayList<T>(streams.values());
	}

	/**
	 * Queue a piece of data to be sent out on a specific stream in this pool.
	 *
	 * @param name the name of the stream.
	 * @param data the data to be queued.
	 */
	public void queueDataOn(String name, Object data) {
		if(!streams.containsKey(name)) return;
		streams.get(name).queueData(data);
	}

	/**
	 * Queue a piece of data to be sent out on all streams in this pool.
	 *
	 * @param data the data to be queued.
	 */
	public void queueDataOnAll(Object data) {
		for(SSEStream s : streams.values()) s.queueData(data);
	}

	/**
	 * Queue data on all streams except those specified.
	 *
	 * @param data   the data to be queued.
	 * @param except a list of names not to queue data on.
	 */
	public void queueDataOnAllExcept(Object data, String... except) {
		for(Map.Entry<String, T> e : streams.entrySet()) {
			boolean can = true;
			for(String exc : except) {
				if(exc.equalsIgnoreCase(e.getKey())) {
					can = false;
					break;
				}
			}
			if(!can) continue;
			e.getValue().queueData(data);
		}
	}

	public void queueDataOnAllIf(Object data, BooleanCallback<SSEStream> iff) {
		for(Map.Entry<String, T> e : streams.entrySet()) {
			if(iff.test(e.getValue())) e.getValue().queueData(data);
		}
	}

	/**
	 * Get the number of streams in this pool.
	 *
	 * @return the number of streams.
	 */
	public int numStreams() {
		return streams.size();
	}

}
