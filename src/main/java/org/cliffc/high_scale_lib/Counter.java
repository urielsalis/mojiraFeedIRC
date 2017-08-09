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

/*
 * Written by Cliff Click and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package org.cliffc.high_scale_lib;

/**
 * A simple high-performance counter.  Merely renames the extended {@link
 * org.cliffc.high_scale_lib.ConcurrentAutoTable} class to be more obvious.
 * {@link org.cliffc.high_scale_lib.ConcurrentAutoTable} already has a decent
 * counting API.
 *
 * @author Cliff Click
 * @since 1.5
 */

public class Counter extends ConcurrentAutoTable {

	// Add the given value to current counter value.  Concurrent updates will
	// not be lost, but addAndGet or getAndAdd are not implemented because but
	// the total counter value is not atomically updated.
	//public void add( long x );
	//public void decrement();
	//public void increment();

	// Current value of the counter.  Since other threads are updating furiously
	// the value is only approximate, but it includes all counts made by the
	// current thread.  Requires a pass over all the striped counters.
	//public long get();
	//public int  intValue();
	//public long longValue();

	// A cheaper 'get'.  Updated only once/millisecond, but fast as a simple
	// load instruction when not updating.
	//public long estimate_get( );

}

