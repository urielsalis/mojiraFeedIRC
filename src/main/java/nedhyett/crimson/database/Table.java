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

package nedhyett.crimson.database;

import nedhyett.crimson.filter.Filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ned Hyett
 */
public class Table implements Serializable {

	public String[] columns;

	public ArrayList<TableEntry> entries = new ArrayList<>();

	public Table(String... columns) {
		this.columns = columns;
	}

	public List<TableEntry> selectData(Filter<TableEntry> filter) {
		return filter.filterList(entries);
	}

	public List<TableEntry> selectDataAND(Filter<TableEntry>... filters) {
		ArrayList<TableEntry> ret = new ArrayList<>(entries); //Create a deep copy
		for(Filter<TableEntry> filter : filters) {
			filter.filterDirect(ret); //Use the filter direct to remove elements from the list instead of risking adding them twice
		}
		return ret;
	}

	public List<TableEntry> selectDataOR(Filter<TableEntry>... filters) {
		ArrayList<TableEntry> ret = new ArrayList<>();
		for(Filter<TableEntry> filter : filters) {
			for(TableEntry entry : filter.filterList(entries)) {
				if(!ret.contains(entry)) {
					ret.add(entry);
				}
			}
		}
		return ret;
	}

	public int countEntries(Filter<TableEntry> matcher) {
		if(matcher != null) {
			return entries.size();
		} else {
			return matcher.filterList(entries).size();
		}
	}

	public boolean entryMatches(Filter<TableEntry> matcher) {
		return countEntries(matcher) > 0;
	}

}
