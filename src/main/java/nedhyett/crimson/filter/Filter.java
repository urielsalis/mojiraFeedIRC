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

package nedhyett.crimson.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Very powerful system that allows filtering of objects via booleans. Yes, I know you can do this with lambda
 * expressions, but this is intended as an alternative in prior versions of Java.
 *
 * @author Ned Hyett
 */
public abstract class Filter<T> {

	/**
	 * This is where the filter logic is implemented. Returning true from this method indicates that the
	 * provided object is OK to pass through this filter.
	 *
	 * @param object the object to filter.
	 *
	 * @return
	 */
	public abstract boolean filter(T object);

	/**
	 * Internal method for "using" the filter logic.
	 *
	 * @param object the object to filter.
	 * @param invert should the result be inverted.
	 *
	 * @return the result of the filter, optionally inverted depending on invert.
	 */
	private final boolean useFilter(T object, boolean invert) {
		return (invert) ? !filter(object) : filter(object);
	}

	/**
	 * Filters an entire list through the filter logic defined in the abstract filter method.
	 *
	 * @param input the list of objects to filter.
	 *
	 * @return the filtered list of objects.
	 */
	public final List<T> filterList(List<T> input) {
		return filterList(input, false);
	}

	/**
	 * Filters an entire list through the filter logic defined in the abstract filter method.
	 *
	 * @param input  the list of objects to filter.
	 * @param invert providing true makes the filter work backwards (true is false and false is true)
	 *
	 * @return the filtered list of objects.
	 */
	public final List<T> filterList(List<T> input, boolean invert) {
		List<T> ret;
		try {
			ret = input.getClass().newInstance(); //Create a new version of the input class. This means that the user can cast the output back.
		} catch(InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		//Now filter the input list and put it into a new list.
		for(T in : input) {
			if(useFilter(in, invert)) ret.add(in);
		}
		return ret;
	}

	/**
	 * Works in the same way as the other filter list method, but REMOVES the items from the provided list.
	 * This method directly modifies the provided list and returns the rejected items as a new list.
	 *
	 * @param input the list of objects to filter.
	 *
	 * @return the list of objects that were REMOVED from the list.
	 */
	public final List<T> filterDirect(List<T> input) {
		return filterDirect(input, false);
	}

	/**
	 * Works in the same way as the other filter list method, but REMOVES the items from the provided list.
	 * This method directly modifies the provided list and returns the rejected items as a new list.
	 *
	 * @param input  the list of objects to filter.
	 * @param invert providing true makes the filter work backwards (true is false and false is true)
	 *
	 * @return the list of objects that were REMOVED from the list.
	 */
	public final List<T> filterDirect(List<T> input, boolean invert) {
		List<T> invalid = new ArrayList<>();
		for(T in : input) {
			if(!useFilter(in, invert)) invalid.add(in);
		}
		input.removeAll(invalid);
		return invalid;
	}

}
