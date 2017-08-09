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

package org.cliffc.high_scale_lib;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Simple class to obtain access to the {@link Unsafe} object.  {@link Unsafe}
 * is required to allow efficient CAS operations on arrays.  Note that the
 * versions in {@link java.util.concurrent.atomic}, such as {@link
 * java.util.concurrent.atomic.AtomicLongArray}, require extra memory ordering
 * guarantees which are generally not needed in these algorithms and are also
 * expensive on most processors.
 */
class UtilUnsafe {

	private UtilUnsafe() {
	} // dummy private constructor

	/**
	 * Fetch the Unsafe.  Use With Caution.
	 */
	public static Unsafe getUnsafe() {
		// Not on bootclasspath
		if(UtilUnsafe.class.getClassLoader() == null) {
			return Unsafe.getUnsafe();
		}
		try {
			final Field fld = Unsafe.class.getDeclaredField("theUnsafe");
			fld.setAccessible(true);
			return (Unsafe) fld.get(UtilUnsafe.class);
		} catch(Exception e) {
			throw new RuntimeException("Could not obtain access to sun.misc.Unsafe", e);
		}
	}
}
