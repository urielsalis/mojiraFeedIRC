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

package nedhyett.crimson.utility.json;

import nedhyett.crimson.utility.ArrayUtils;
import nedhyett.crimson.utility.StreamUtils;
import nedhyett.crimson.utility.json.nodes.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Reads a string and interprets it as JSON. Returns a JSONObject that is the root node of the JSON string.
 * <p>
 * Things to take into account:
 * 1. This implementation will attempt to not cause errors. Badly formatted JSON will cause weird effects.
 * 2. The JSON must adhere EXACTLY to regulations or it won't get parsed correctly and will cause weird effects.
 * 3. The entire file is loaded into memory, so there is going to be a memory limit.
 * 4. New lines in strings don't matter and may be removed.
 */
public class JSONReader {


	public static JSONObject parseJSON(String json) {
		JSONObject obj = new JSONObject();
		doParse(json, JSONNodeType.OBJECT, obj);
		return obj;
	}

	public static JSONObject parseJSON(InputStream in) {
		String s = new String(StreamUtils.getBytes(in));
		return parseJSON(s);
	}

	public static JSONObject parseJSON(byte[] bytes) {
		return parseJSON(new String(bytes));
	}


	public static JSONArray parseJSONAsArray(InputStream in) {
		String s = new String(StreamUtils.getBytes(in));
		JSONArray obj = new JSONArray();
		doParse(s, JSONNodeType.ARRAY, obj);
		return obj;
	}

	public static JSONArray parseJSONAsArray(String json) {
		return parseJSONAsArray(new ByteArrayInputStream(json.getBytes()));
	}

	private static void doParse(String ln, JSONNodeType type, JSONNode parent) {
		for(String a : parseLevel(ln)) {
			for(String b : getNodes(a)) {
				if(type == JSONNodeType.OBJECT) {
					String[] c = b.split(":");
					String d = ArrayUtils.concatArray(c, ":", 1);
					if(d.equalsIgnoreCase("null")) continue;
					if(c[1].trim().startsWith("\"")) {
						((JSONObject) parent).children.put(c[0].substring(1, c[0].length() - 1), new JSONString(d.substring(1, d.length() - 1)));
					} else if(d.startsWith("[")) {
						JSONArray array = new JSONArray();
						((JSONObject) parent).children.put(c[0].substring(1, c[0].length() - 1), array);
						doParse(d, JSONNodeType.ARRAY, array);
					} else if(d.startsWith("{")) {
						JSONObject object = new JSONObject();
						((JSONObject) parent).children.put(c[0].substring(1, c[0].length() - 1), object);
						doParse(d, JSONNodeType.OBJECT, object);
					} else {
						if(d.equalsIgnoreCase("true") || d.equalsIgnoreCase("false")) {
							((JSONObject) parent).children.put(c[0].substring(1, c[0].length() - 1), new JSONBoolean(d.equalsIgnoreCase("true")));
						} else {
							JSONNumber jn;
							try {
								jn = new JSONNumber(Long.parseLong(d));
							} catch (Exception e) {
								jn = new JSONNumber(Double.parseDouble(d));
							}
							((JSONObject) parent).children.put(c[0].substring(1, c[0].length() - 1), jn);
						}
					}
				} else if(type == JSONNodeType.ARRAY) {
					if(b.equalsIgnoreCase("null")) continue;
					if(b.startsWith("\"")) {
						((JSONArray) parent).children.add(new JSONString(b.substring(1, b.length() - 1)));
					} else if(b.startsWith("[")) {
						JSONArray array = new JSONArray();
						((JSONArray) parent).children.add(array);
						doParse(b, JSONNodeType.ARRAY, array);
					} else if(b.startsWith("{")) {
						JSONObject object = new JSONObject();
						((JSONArray) parent).children.add(object);
						doParse(b, JSONNodeType.OBJECT, object);
					} else {
						if(b.equalsIgnoreCase("true") || b.equalsIgnoreCase("false")) {
							((JSONArray) parent).children.add(new JSONBoolean(b.equalsIgnoreCase("true")));
						} else {
							JSONNumber jn;
							try {
								jn = new JSONNumber(Long.parseLong(b));
							} catch (Exception e) {
								jn = new JSONNumber(Double.parseDouble(b));
							}
							((JSONArray) parent).children.add(jn);
						}
					}
				}
			}
		}
	}

	private static ArrayList<String> parseLevel(String s) {
		ArrayList<String> r = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		long l = 0;
		long c = 0;

		for(char st : s.toCharArray()) {
//			System.out.println(c + "/" + s.length() + ": " + st);
			c++;
			if(st == '{' || st == '[') {
				if(l != 0) sb.append(st);
				l += 1;
			} else if(st == ']' || st == '}') {
				l -= 1;
				if(l == 0) {
					r.add(sb.toString().trim());
					sb.delete(0, sb.length());
				} else {
					sb.append(st);
				}
			} else {
				sb.append(st);
			}
		}
		return r;
	}

	private static ArrayList<String> getNodes(String s) {
		ArrayList<String> r = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		int l = 0;
		boolean iq = false;
		boolean sk = false;

		for(char a : s.toCharArray()) {
			if(sk) {
				sb.append(a);
				sk = false;
				continue;
			}
			if(a == '\\') {
				sb.append(a);
				sk = true;
				continue;
			}
			if(a == '"') {
//                if(iq){
//                    System.out.println("Closing quotes...");
//                } else {
//                    System.out.println("Opening quotes...");
//                }
				iq = !iq;
			}
			if(a == '{' || a == '[') {
				l += 1;
				sb.append(a);
			} else if(a == ']' || a == '}') {
				l -= 1;
				sb.append(a);
			} else if(a == ',' && l == 0 && !iq) {
				r.add(sb.toString().trim());
				sb.delete(0, sb.length());
			} else {
				sb.append(a);
			}
		}
		if(sb.length() > 0) r.add(sb.toString().trim());

		return r;
	}


}
