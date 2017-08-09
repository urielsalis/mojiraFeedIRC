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

package nedhyett.crimson.networking.http.server.provider.mangler.handler;

import nedhyett.crimson.networking.http.HttpResponse;
import nedhyett.crimson.networking.http.HttpRequest;
import nedhyett.crimson.networking.http.server.HttpQueryData;
import nedhyett.crimson.networking.http.server.provider.mangler.IManglerHandler;

import java.net.Socket;
import java.util.HashMap;

/**
 * (Created on 04/07/2015)
 *
 * @author Ned Hyett
 */
public class BasicRenameHandler implements IManglerHandler {

	private HashMap<String, String> renames = new HashMap<>();

	public void addRename(String from, String to) {
		renames.put(from, to);
	}

	public void removeRename(String from) {
		renames.remove(from);
	}

	public boolean hasRename(String from) {
		return renames.containsKey(from);
	}


	@Override
	public boolean mangle(HttpRequest request, HttpQueryData data, Socket s, HttpResponse response) {
		if(renames.containsKey(request.page)) {
			request.page = renames.get(request.page);
		}
		return false;
	}
}
