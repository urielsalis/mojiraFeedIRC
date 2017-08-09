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
package nedhyett.Amelia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Utilities.
 *
 * @author Ned
 */
public class Util {

    /**
     * Hacky way of testing if a string is a number. (If you don't like it, give me a better way.)
     *
     * @param in
     *
     * @return
     */
    public static boolean isNumber(String in) {
	try {
	    Integer.parseInt(in);
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    /**
     * Creates an MD5 hash of the input string.
     *
     * @param input
     *
     * @return
     */
    public static String digestString(String input) {
	String digested = "";
	try {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    byte[] res = md.digest(input.getBytes());
	    StringBuilder sb = new StringBuilder();
	    for (byte b1 : res) {
		sb.append(Integer.toHexString(0xFF & b1));
	    }
	    digested = sb.toString();
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	} finally {
	    return digested;
	}
    }

    /**
     * Get the time between two dates.
     *
     * @param start
     * @param end
     *
     * @return
     */
    public static long[] getElapsed(Date start, Date end) {
	long different = end.getTime() - start.getTime();
	long secInMil = 1000;
	long minInMil = secInMil * 60;
	long hrsInMil = minInMil * 60;
	long dayInMil = hrsInMil * 24;
	long elapsedDays = different / dayInMil;
	different = different % dayInMil;
	long elapsedHours = different / hrsInMil;
	different = different % hrsInMil;
	long elapsedMins = different / minInMil;
	different = different % minInMil;
	long elapsedSeconds = different / secInMil;
	return new long[]{elapsedDays, elapsedHours, elapsedMins, elapsedSeconds};
    }

    /**
     * Get a more accurate microtime.
     *
     * @return
     */
    public static long getMicroTime() {
	return System.nanoTime() / 100000;
    }
    
    public static void saveArrayList(ArrayList<String> array, DataOutputStream out) {
	try {
	    out.writeInt(array.size());
	    array.forEach((String str) -> {
		try {
		    out.writeUTF(str);
		} catch (IOException ex) {
		    
		}
	    });
	} catch (IOException ex) {
	    
	}
    }
    
    public static ArrayList<String> readArrayList(DataInputStream in) {
	ArrayList<String> ret = new ArrayList<>();
	try {
	    int num = in.readInt();
	    for (int i = 0; i < num; i++) {
		ret.add(in.readUTF());
	    }
	} catch (IOException e) {
	    
	} finally {
	    return ret;
	}
    }
    
}
