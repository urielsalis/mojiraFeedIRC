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

import nedhyett.crimson.logging.CrimsonLog;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.lang.Runtime.getRuntime;

/**
 * @author Ned Hyett
 */
public class GenericUtils {

	private static Robot robot = null;
	private static final Random r = new Random();

	public static <T> T duplicateClass(Object obj, Class T) {
		try {
			return (T) obj.getClass().asSubclass(T).newInstance();
		} catch(InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Picks a random element from the array.
	 *
	 * @param array
	 *
	 * @return
	 */
	public static <T> T pickRandom(T[] array) {
		return array[r.nextInt(array.length)];
	}

	public static void waitFractional(double seconds) {
		try {
			Thread.sleep((long) seconds * 1000);
		} catch(InterruptedException ignored) {

		}
	}

	/**
	 * Waits for the provided number of seconds. (Hangs the thread)
	 *
	 * @param seconds
	 */
	public static void wait(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch(InterruptedException ignored) {
		}
	}

	/**
	 * Waits for the provided number of minutes. (Hangs the thread)
	 *
	 * @param mins
	 */
	public static void waitMins(int mins) {
		wait(mins * 60);
	}

	/**
	 * Throws a runtime exception in the current thread.
	 */
	public static void crash() {
		crash("No Reason Provided!");
	}

	/**
	 * Throws a runtime exception in the current thread with a reason.
	 *
	 * @param reason
	 */
	public static void crash(String reason) {
		throw new RuntimeException("Crashing: " + reason);
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

	public static int getSecondTime() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	public static String[] getStackTrace(Throwable ex) {
		String[] trace = new String[ex.getStackTrace().length];
		StackTraceElement[] traceElements = ex.getStackTrace();
		for(int i = 0; i < trace.length; i++) {
			trace[i] = "\t" + traceElements[i].toString();
		}
		return trace;
	}

	/**
	 * Writes the contents of a stack trace to an ArrayList.
	 *
	 * @param ex
	 *
	 * @return
	 */
	public static ArrayList<String> pullException(Throwable ex) {
		ArrayList<String> strings = new ArrayList<>();
		strings.add(ex.toString());
		for(StackTraceElement trace : ex.getStackTrace()) strings.add("\t" + trace.toString());
		return strings;
	}

	/**
	 * Digests the bytes using the MD5 algorithm.
	 *
	 * @param input
	 *
	 * @return
	 */
	public static String digest(byte[] input) {
		return digest(input, "MD5");
	}

	/**
	 * Digests the bytes using the provided algorithm.
	 *
	 * @param input
	 * @param algo
	 *
	 * @return
	 */
	public static String digest(byte[] input, String algo) {
		String digested = "";
		try {
			MessageDigest md = MessageDigest.getInstance(algo);
			byte[] res = md.digest(input);
			StringBuilder sb = new StringBuilder();
			for(byte b1 : res) sb.append(Integer.toHexString(0xFF & b1));
			digested = sb.toString();
		} catch(NoSuchAlgorithmException e) {
			CrimsonLog.warning("Failed to digest bytes!");
			CrimsonLog.warning(e);
		}
		return digested;
	}

	/**
	 * Checks if the provided object is a subclass of the provided type. If it
	 * is not, return a new instance of the provided type. If it is, return a
	 * casted version of the object to the provided type.
	 *
	 * @param <T>
	 * @param o    The object to check
	 * @param type The type to check against.
	 *
	 * @return
	 */
	public static <T> T checkAndCast(Object o, Class<T> type) {
		if(!o.getClass().isAssignableFrom(type)) {
			try {
				return type.newInstance();
			} catch(InstantiationException | IllegalAccessException ex) { //Do our best to create a new blank type.
				return null;
			}
		}
		return (T) o;
	}

	/**
	 * Turn a set of bytes into their hex values.
	 *
	 * @param in
	 *
	 * @return
	 */
	public static String hexify(byte[] in) {
		StringBuilder sb = new StringBuilder();
		for(byte b : in) sb.append(String.format("%02X ", b));
		return sb.toString().trim();
	}

	public static byte[] compress(byte[] in) {
		try {
			return StreamUtils.pushThroughOutStream(in, GZIPOutputStream.class);
		} catch(Exception e) {
			e.printStackTrace();
			return in;
		}
	}

	public static byte[] decompress(byte[] in) {
		try {
			return StreamUtils.pushThroughInStream(in, GZIPInputStream.class);
		} catch(Exception e) {
			e.printStackTrace();
			return in;
		}
	}

	public static Robot getRobot() {
		if(robot == null) {
			try {
				robot = new Robot();
			} catch(Exception e) {
				CrimsonLog.warning("Failed to create GenericUtils Robot!");
				e.printStackTrace();
			}
		}
		return robot;
	}

	public static BufferedImage takeScreenshot() {
		Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		return takeScreenshot(screen.x, screen.y, screen.width, screen.height);
	}

	public static BufferedImage takeScreenshot(int x, int y, int width, int height) {
		return getRobot().createScreenCapture(new Rectangle(x, y, width, height));
	}

	public static long getUsedMemory() {
		return (getRuntime().totalMemory() - getRuntime().freeMemory()) / (1024 * 1024);
	}

	public static long getFreeMemory() {
		return getRuntime().freeMemory() / (1024 * 1024);
	}

	public static long getTotalMemory() {
		return getRuntime().totalMemory() / (1024 * 1024);
	}

	public static long getMaxMemory() {
		return getRuntime().maxMemory() / (1024 * 1024);
	}

}
