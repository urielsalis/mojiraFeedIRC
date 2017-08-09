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

package nedhyett.crimson.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * An argument parser used during initialisation.
 *
 * @author Ned Hyett
 */
public class ArgumentParser {

	private final HashMap<String, String> args = new HashMap<>();

	public ArgumentParser(String[] inargs) {
		List<String> list = new ArrayList<>();
		Collections.addAll(list, inargs);

		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).startsWith("--")) {
				if(i + 1 < list.size() && !list.get(i + 1).startsWith("--")) {
					args.put(list.get(i).replace("--", "").toLowerCase(), list.get(i + 1));
				} else {
					args.put(list.get(i).replace("--", "").toLowerCase(), "true");
				}
			}
		}
	}

	public String getArgument(String key) {
		return getArgument(key, null);
	}

	/**
	 * Get an argument from the parsed set of arguments.
	 *
	 * @param key
	 *
	 * @return
	 */
	public String getArgument(String key, String def) {
		if(!hasArgument(key)) return def;
		return args.get(key.toLowerCase());
	}

	public int getArgument(String key, int def) {
		if(!hasArgument(key)) return def;
		return Integer.parseInt(args.get(key.toLowerCase()));
	}

	/**
	 * Check if the provided argument has been read by the parser.
	 *
	 * @param key
	 *
	 * @return
	 */
	public boolean hasArgument(String key) {
		return args.containsKey(key.toLowerCase());
	}

}
