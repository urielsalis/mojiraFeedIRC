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

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * (Created on 30/06/2015)
 *
 * @author Ned Hyett
 */
public class GroupingSSEPool<T extends SSEStream> implements ISSEPool<T> {

	protected NonBlockingHashMap<String, CopyOnWriteArrayList<T>> streams = new NonBlockingHashMap<>();

	/**
	 * Add a stream to the pool.
	 *
	 * @param name   the name of the stream.
	 * @param stream the stream.
	 */
	public void addStream(String name, T stream) {
		if(!streams.contains(name)) streams.put(name, new CopyOnWriteArrayList<T>());
		streams.get(name).add(stream);
	}

	/**
	 * Remove a stream from the pool.
	 *
	 * @param stream the stream.
	 */
	public void removeStream(T stream) {
		for(CopyOnWriteArrayList<T> cowal : streams.values()) {
			cowal.remove(stream);
		}
	}

	/**
	 * Remove a stream from the pool.
	 *
	 * @param name the name of the stream.
	 */
	public void removeStream(String name) {
		streams.remove(name);
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
	 * @param name  the name of the stream.
	 * @param index the index of the stream.
	 *
	 * @return the stream.
	 */
	public T getStream(String name, int index) {
		return streams.get(name).get(index);
	}

	/**
	 * Get all streams in the pool. Note: don't use this too much; it copies all the streams into a new list in the
	 * process of converting a collection to a list.
	 *
	 * @return the list of streams.
	 */
	public ArrayList<T> getAllStreams() {
		ArrayList<T> ret = new ArrayList<>();
		for(CopyOnWriteArrayList<T> cowal : streams.values()) {
			ret.addAll(cowal);
		}
		return ret;
	}

	/**
	 * Queue a piece of data to be sent out on a specific stream in this pool.
	 *
	 * @param name the name of the stream.
	 * @param data the data to be queued.
	 */
	public void queueDataOn(String name, Object data) {
		if(!streams.containsKey(name)) return;
		for(T stream : streams.get(name)) {
			stream.queueData(data);
		}
	}

	/**
	 * Queue a piece of data to be sent out on all streams in this pool.
	 *
	 * @param data the data to be queued.
	 */
	public void queueDataOnAll(Object data) {
		for(String s : streams.keySet()) queueDataOn(s, data);
	}

	/**
	 * Get the number of streams in this pool.
	 *
	 * @return the number of streams.
	 */
	public int numStreams() {
		int count = 0;
		for(CopyOnWriteArrayList<T> cowal : streams.values()) {
			count += cowal.size();
		}
		return count;
	}

}
