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

/**
 * (Created on 01/05/2015)
 *
 * @author Ned Hyett
 */
public enum EnumOS {

	/**
	 * Represents FREEBSD (not implemented).
	 */
	FREEBSD,

	/**
	 * Represents any Linux distribution.
	 */
	LINUX,

	/**
	 * Represents any version of Mac OS X.
	 */
	MACOSX,

	/**
	 * Represents Solaris.
	 */
	SOLARIS,

	/**
	 * Represents any version of Windows.
	 */
	WINDOWS,

	/**
	 * The operating system is not known to Crimson.
	 */
	UNKNOWN;

	/**
	 * Guess the current Operating System
	 *
	 * @return
	 */
	public static EnumOS getOS() {
		String s = System.getProperty("os.name").toLowerCase();
		if(s.contains("win")) return WINDOWS;
		if(s.contains("mac")) return MACOSX;
		if(s.contains("solaris")) return SOLARIS;
		if(s.contains("sunos")) return SOLARIS;
		if(s.contains("linux")) return LINUX;
		if(s.contains("unix")) return LINUX;
		return UNKNOWN;
	}

}
