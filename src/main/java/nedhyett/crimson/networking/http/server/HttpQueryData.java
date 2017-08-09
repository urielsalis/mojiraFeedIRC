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

package nedhyett.crimson.networking.http.server;

import nedhyett.crimson.networking.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * (Created on 27/03/2015)
 *
 * @author Ned Hyett
 */
public class HttpQueryData {

	private final Map<String, List<String>> data;

	public HttpQueryData(String raw) {
		this(raw.getBytes(), HttpMethod.GET, null);
	}

	public HttpQueryData(byte[] raw, HttpMethod method, String encoding) {
		if(method == HttpMethod.GET) {
			this.data = HttpServerUtils.getQueryParams("?" + new String(raw));
		} else {
			if("somethinggoeshere".equalsIgnoreCase(encoding) && false) {
				try {
					//this.data = HttpServerUtils.parsePostData(raw);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else {
				this.data = HttpServerUtils.getQueryParams("?" + new String(raw));
			}
		}
	}

	public HttpQueryData(Map<String, List<String>> data) {
		this.data = data;
	}

	public boolean hasParameter(String name) {
		return data.containsKey(name);
	}

	public String getParam(String name) {
		return getParam(name, 0);
	}

	public int getParamAsInt(String name) {
		return Integer.parseInt(getParam(name));
	}

	public String getParam(String name, int index) {
		if(!hasParameter(name)) return null;
		return data.get(name).get(index);
	}

	public List<String> getAllParams(String name) {
		if(!hasParameter(name)) return null;
		return data.get(name);
	}

}
