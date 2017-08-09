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

package nedhyett.crimson.networking.http;

import nedhyett.crimson.utility.StringUtils;

import java.util.HashMap;

/**
 * Defines a list of known mime types in an extensible format.
 * <p>
 * (Created on 19/05/2015)
 *
 * @author Ned Hyett
 */
public class MimeTypeLibrary {

	private static final HashMap<String, String> mimes = new HashMap<>();

	static {
		//web
		mimes.put("html", "text/html");
		mimes.put("htm", "text/html");
		mimes.put("htmls", "text/html");
		mimes.put("js", "application/javascript");
		mimes.put("css", "text/css");

		//audio
		mimes.put("aif", "audio/aiff");
		mimes.put("aiff", "audio/aiff");
		mimes.put("m2a", "audio/mpeg");
		mimes.put("midi", "audio/midi");
		mimes.put("mp3", "audio/mpeg");

		//image
		mimes.put("bmp", "image/bmp");
		mimes.put("gif", "image/gif");
		mimes.put("ico", "image/x-icon");
		mimes.put("jpeg", "image/jpeg");
		mimes.put("jpg", "image/jpeg");

		//video
		mimes.put("avi", "video/avi");
		mimes.put("m1v", "video/mpeg");
		mimes.put("m2v", "video/mpeg");
		mimes.put("mov", "video/quicktime");

		//other
		mimes.put("class", "application/java");
		mimes.put("doc", "application/msword");
		mimes.put("docx", "application/msword");
		mimes.put("dot", "application/msword");
		mimes.put("dotx", "application/msword");
		mimes.put("exe", "application/octet-stream");
		mimes.put("gzip", "application/x-gzip");
		mimes.put("ppt", "application/mspowerpoint");
		mimes.put("pptx", "application/mspowerpoint");
		mimes.put("jar", "application/java-archive");
	}

	/**
	 * Resolve a file extension to a mime type.
	 *
	 * @param fileExt the extension of the file.
	 *
	 * @return
	 */
	public static String resolve(String fileExt) {
		String trunatedExt = fileExt;
		if(fileExt.indexOf('.') > -1) {
			trunatedExt = StringUtils.getFileExtension(fileExt);
		}
		if(mimes.get(trunatedExt) == null) return "text/plain";
		return mimes.get(trunatedExt);
	}

}
