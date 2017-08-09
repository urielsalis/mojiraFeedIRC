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

import nedhyett.crimson.Constants;
import nedhyett.crimson.logging.CrimsonLog;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Ned Hyett
 */
public class StreamUtils {

	/**
	 * Create a high-performance DataOutputStream from the provided output
	 * stream.
	 *
	 * @param outstream
	 *
	 * @return
	 */
	public static DataOutputStream wrapUncompressedStream(OutputStream outstream) {
		return new DataOutputStream(new BufferedOutputStream(outstream));
	}

	/**
	 * Creates a high-performance DataInputStream from the provided input
	 * stream.
	 *
	 * @param instream
	 *
	 * @return
	 */
	public static DataInputStream wrapUncompressedStream(InputStream instream) {
		return new DataInputStream(new BufferedInputStream(instream));
	}

	public static DataOutputStream wrapStream(OutputStream outstream) throws IOException {
		return wrapUncompressedStream(new GZIPOutputStream(outstream));
	}

	public static DataInputStream wrapStream(InputStream instream) throws IOException {
		return wrapUncompressedStream(new GZIPInputStream(instream));
	}

	/**
	 * Why not?
	 *
	 * @param out
	 * @param times
	 * @param packetSize
	 * @param rng
	 */
	public static void floodStream(OutputStream out, int times, int packetSize, Random rng) throws IOException {
		assert packetSize > 0;
		byte[] buf = new byte[packetSize];
		for(int i = 0; i < times; i++) {
			rng.nextBytes(buf);
			out.write(buf);
			out.flush();
		}
	}

	/**
	 * Why not?
	 *
	 * @param out
	 * @param times
	 */
	public static void floodStream(OutputStream out, int times, int packetSize) throws IOException {
		floodStream(out, times, packetSize, new Random());
	}

	/**
	 * Pushes the provided data through the provided type of output stream.
	 * Useful in cases where the output stream does something to the data
	 * that it is given, like a GZIPOutputStream.
	 *
	 * @param in
	 * @param streamClass
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	public static byte[] pushThroughOutStream(byte[] in, Class<? extends OutputStream> streamClass) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream out1 = streamClass.getConstructor(OutputStream.class).newInstance(baos);
		out1.write(in);
		if(out1 instanceof DeflaterOutputStream) { //Special handling for DeflaterStreams because they require finishing
			((DeflaterOutputStream) out1).finish();
		}
		return baos.toByteArray();
	}

	/**
	 * Pushes the provided data through the provided type of input stream.
	 * Useful in cases where the input stream does something to the data
	 * that it is given, like a GZIPInputStream.
	 *
	 * @param in
	 * @param streamClass
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	public static byte[] pushThroughInStream(byte[] in, Class<? extends InputStream> streamClass) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(in);
		InputStream in1 = streamClass.getConstructor(InputStream.class).newInstance(bais);
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int len;
		while((len = in1.read(buf)) > 0) ret.write(buf, 0, len);
		return ret.toByteArray();
	}

	public static void pushInto(byte[] in, OutputStream out) {
		try {
			out.write(in);
		} catch(Exception e) {
			CrimsonLog.warning(e);
		}
	}

	public static byte[] pullOut(InputStream in) {
		try {
			byte[] buf = new byte[in.available()];
			in.read(buf);
			return buf;
		} catch(Exception e) {
			CrimsonLog.warning(e);
			return new byte[0];
		}
	}

	/**
	 * Pulls data from an InputStream and writes it into an OutputStream.
	 *
	 * @param in
	 * @param out
	 */
	public static void bridge(InputStream in, OutputStream out) {
		try {
			byte[] buf = new byte[Constants.BUFFER_SIZE];
			while(in.available() > 0) {
				int size = in.read(buf);
				out.write(buf, 0, size);
			}
		} catch(IndexOutOfBoundsException e) {

		} catch(Exception e) {
			CrimsonLog.warning("Failure during stream bridge!");
			CrimsonLog.warning(e);
		}
	}

	public static InputStream putInStream(byte[] in) {
		return new ByteArrayInputStream(in);
	}

	public static byte[] getBytes(InputStream in) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bridge(in, baos);
		return baos.toByteArray();
	}

	public static ArrayList<String> getLines(InputStream in) {
		ArrayList<String> ret = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = br.readLine()) != null) ret.add(line);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			return ret;
		}
	}

	public static String readLine(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while(true) {
			if(new String(baos.toByteArray()).endsWith("\r\n")) break;
			baos.write(in.read());
		}
		return new String(baos.toByteArray()).trim();
	}

}
