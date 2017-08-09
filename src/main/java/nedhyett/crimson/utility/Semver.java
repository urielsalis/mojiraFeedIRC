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

import nedhyett.crimson.networking.tier4.NetworkConstructor;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a Semver
 *
 * @author Ned Hyett
 */
public class Semver implements Serializable {

	private int major;
	private int minor;
	private int patch;

	@Deprecated //Only used for serialization!
	@NetworkConstructor
	public Semver() {

	}

	public Semver(String semver) {
		System.out.println(semver);
		String[] split = StringUtils.robustSplit(semver, ".");
		System.out.println(Arrays.toString(split));
		this.major = Integer.parseInt(split[0]);
		this.minor = Integer.parseInt(split[1]);
		if(split.length > 2) {
			this.patch = Integer.parseInt(split[2]);
		} else {
			this.patch = 0;
		}
	}

	public Semver(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	/**
	 * Returns true if the provided Semver is greater than this Semver
	 *
	 * @param other
	 *
	 * @return
	 */
	public boolean isGreaterThan(Semver other) {
		return other.major > major || other.minor > minor || other.patch > patch;
	}

	/**
	 * Returns true if the provided Semver is greater than this Semver
	 *
	 * @param other
	 *
	 * @return
	 */
	public boolean isLessThan(Semver other) {
		return other.major < major || other.minor < minor || other.patch < patch;
	}

	/**
	 * Returns true if the provided Semver is equal to this Semver.
	 *
	 * @param other
	 *
	 * @return
	 */
	public boolean isEqualTo(Semver other) {
		return other.major == major && other.minor == minor && other.patch == patch;
	}

	public boolean isSafe(Semver other) {
		if(isEqualTo(other)) {
			return true;
		}
		return isGreaterThan(other) && major == other.major;
	}

	public String getVersionString() {
		return String.format("%s.%s.%s", major, minor, patch);
	}

	public String getVersionStringMini() {
		if(patch <= 0) {
			return String.format("%s.%s", major, minor);
		}
		return String.format("%s.%s.%s", major, minor, patch);
	}

}
