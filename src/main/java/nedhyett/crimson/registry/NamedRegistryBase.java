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

package nedhyett.crimson.registry;

/**
 * Acts as a registry for items (a kind of HashMap that only requires one type)
 *
 * @param <T> Type of object being stored.
 *
 * @author Ned Hyett
 */
public class NamedRegistryBase<T> extends RegistryBase<String, T> implements Iterable<T> {


	/**
	 * Checks if the registry has the entry. Is not case sensitive.
	 *
	 * @param name
	 *
	 * @return
	 */
	@Override
	public boolean hasEntry(String name) {
		return hasEntry(name, false);
	}

	/**
	 * Checks if the registry has the entry. Can be case sensitive depending on the second parameter.
	 *
	 * @param name
	 * @param caseSensitive
	 *
	 * @return
	 */
	public boolean hasEntry(String name, boolean caseSensitive) {
		if(caseSensitive) {
			return items.containsKey(name);
		} else {
			for(String s : items.keySet()) {
				if(s.equalsIgnoreCase(name)) return true;
			}
			return false;
		}
	}

	/**
	 * Pulls an entry from the registry denoted by the provided key. Is not case sensitive.
	 *
	 * @param name
	 *
	 * @return
	 */
	@Override
	public T getEntry(String name) {
		return getEntry(name, false);
	}

	/**
	 * Pulls an entry from the registry denoted by the provided key. Can be case sensitive depending on the second
	 * parameter.
	 *
	 * @param name
	 * @param caseSensitive
	 *
	 * @return
	 */
	public T getEntry(String name, boolean caseSensitive) {
		if(caseSensitive) {
			return items.get(name);
		} else {
			for(String s : items.keySet()) {
				if(s.equalsIgnoreCase(name)) return items.get(s);
			}
			return null;
		}
	}

}
