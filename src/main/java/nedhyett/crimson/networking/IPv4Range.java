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

package nedhyett.crimson.networking;

import nedhyett.crimson.logging.CrimsonLog;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Defines an IP range that spans between two IP addresses.
 * <p>
 * Current issues:
 * - If start address is greater than or equal to end address this class doesn't work properly.
 * <p>
 * This class can be serialised and used in a Tier4 exchange.
 *
 * @author Ned Hyett
 */
public class IPv4Range implements Serializable {

	/**
	 * The start address.
	 */
	public byte[] start;

	/**
	 * The end address.
	 */
	public byte[] end;

	public IPv4Range(byte[] start, byte[] end) {
		this.start = start;
		if(end == null) {
			this.end = start;
		} else {
			this.end = end;
		}
	}

	/**
	 * Create an IPv4 range.
	 *
	 * @param start the start address.
	 * @param end   the end address.
	 */
	public IPv4Range(InetAddress start, InetAddress end) {
		this(start.getAddress(), end.getAddress());
	}

	/**
	 * Create an IPv4 range.
	 *
	 * @param start the start address.
	 * @param end   the end address.
	 */
	public IPv4Range(String start, String end) throws UnknownHostException {
		this(InetAddress.getByName(start), InetAddress.getByName(end));
	}

	/**
	 * Checks if the provided IP address is in the range.
	 *
	 * @param address
	 *
	 * @return
	 */
	public boolean isInRange(String address) {
		try {
			return isInRange(InetAddress.getByName(address));
		} catch(UnknownHostException ex) {
			CrimsonLog.warning(ex);
		}
		return false;
	}

	/**
	 * Checks if the provided IP address is in the range.
	 *
	 * @param address
	 *
	 * @return
	 */
	public boolean isInRange(InetAddress address) {
		return isInRange(address.getAddress());
	}

	/**
	 * Checks if the provided IP address is in the range.
	 * <p>
	 * Credit goes to <a
	 * href="http://stackoverflow.com/a/2138724">http://stackoverflow.com/a/2138724</a>
	 *
	 * @param address
	 *
	 * @return
	 */
	public boolean isInRange(byte[] address) {
		boolean lower = true;
		boolean upper = true;
		for(int i = 0; i < start.length && (lower || upper); i++) {
			if((lower && address[i] < start[i]) || (upper && address[i] > end[i])) return false;
			lower &= (address[i] == start[i]);
			upper &= (address[i] == end[i]);
		}
		return true;
	}

}
