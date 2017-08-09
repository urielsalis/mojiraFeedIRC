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

package nedhyett.crimson.utility.json.nodes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * (Created on 27/04/2015)
 *
 * @author Ned Hyett
 */
public class JSONObject implements JSONNode {

	public final HashMap<String, JSONNode> children = new HashMap<>();

	@Override
	public String getAsString() {
		return Arrays.toString(children.keySet().toArray(new String[0]));
	}

	@Override
	public boolean getAsBool() {
		return false;
	}

	@Override
	public int getAsInt() {
		return 0;
	}

	@Override
	public long getAsLong() {
		return 0;
	}

	@Override
	public double getAsDouble() {
		return 0;
	}

	@Override
	public JSONObject getAsObject() {
		return this;
	}

	@Override
	public JSONArray getAsArray() {
		return null;
	}

	public int size() {
		return children.size();
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public JSONNode get(String index) {
		return children.get(index);
	}

	public Set<String> keySet() {
		return children.keySet();
	}

	public Set<Map.Entry<String, JSONNode>> entrySet() {
		return children.entrySet();
	}

}
