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

import nedhyett.crimson.types.IGenericCallback;

import java.io.*;
import java.util.Arrays;

/**
 * (Created on 25/06/2015)
 *
 * @author Ned Hyett
 */
public class ExecutionUtils {

	public static byte[] executeAndCapture(String command, String... args) throws IOException {
		return executeAndCapture(command, null, args);
	}

	public static byte[] executeAndCapture(String command, File wd, String... args) throws IOException {
		String[] fargs = new String[args.length + 1];
		fargs[0] = command;
		System.arraycopy(args, 0, fargs, 1, fargs.length - 1);
		ProcessBuilder pb = new ProcessBuilder(fargs);
		System.out.println("EXECUTING " + Arrays.toString(fargs));
		if(wd != null) {
			pb.directory(wd);
		}
		Process p = pb.start();
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while((line = input.readLine()) != null) baos.write((line + "\n").getBytes());
		try {
			p.waitFor();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		input.close();
		return baos.toByteArray();
	}

	public static void executeAndCaptureRealtime(String command, File wd, IGenericCallback callback, boolean block, String... args) throws IOException, InterruptedException {
		String[] fargs = new String[args.length + 1];
		fargs[0] = command;
		System.arraycopy(args, 0, fargs, 1, fargs.length - 1);
		ProcessBuilder pb = new ProcessBuilder(fargs);
		if(wd != null) {
			pb.directory(wd);
		}
		Process p = pb.start();
		Thread inputBuffer = new Thread() {
			@Override
			public void run() {
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				try {
					while((line = input.readLine()) != null && !this.isInterrupted()) {
						callback.call(line);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
				try {
					p.waitFor();
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		inputBuffer.setDaemon(true);
		inputBuffer.start();
		if(block) {
			p.waitFor();
		}
	}

}
