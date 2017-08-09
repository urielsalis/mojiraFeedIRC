/*
 * Copyright (c) 2014, Ned Hyett
 *  All rights reserved.
 * 
 *  By using this program/package/library you agree to be completely and unconditionally
 *  bound by the agreement displayed below. Any deviation from this agreement will not
 *  be tolerated.
 * 
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 * 
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this
 *  list of conditions and the following disclaimer in the documentation and/or other
 *  materials provided with the distribution.
 *  3. The redistribution is not sold, unless permission is granted from the copyright holder.
 *  4. The redistribution must contain reference to the original author, and this page.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nedhyett.Amelia.core.connection;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Defines an IP range that spans between two IP addresses.
 *
 * @author Ned
 */
public class IPRange {

    /**
     * The start address.
     */
    public byte[] start;

    /**
     * The end address.
     */
    public byte[] end;

    /**
     * Create this IP range.
     * <br><br>
     * Current issues:
     * <br> - If start address is greater than or equal to end address this class doesn't work
     * properly.
     *
     * @param start
     * @param end
     */
    public IPRange(String start, String end) {
	try {
	    this.start = InetAddress.getByName(start).getAddress();
	    this.end = InetAddress.getByName(end).getAddress();
	} catch (UnknownHostException ex) {
	    
	}
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
	} catch (UnknownHostException ex) {
	    
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
     *
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
	for (int i = 0; i < start.length && (lower || upper); i++) {
	    if ((lower && address[i] < start[i]) || (upper && address[i] > end[i])) {
		return false;
	    }
	    lower &= (address[i] == start[i]);
	    upper &= (address[i] == end[i]);
	}
	return true;
    }

}
