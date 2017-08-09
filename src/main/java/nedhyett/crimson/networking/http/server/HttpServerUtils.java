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

package nedhyett.crimson.networking.http.server;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.utility.StreamUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * Utilities for the HTTP server implementation.
 *
 * @author Ned Hyett
 */
public class HttpServerUtils {

	/**
	 * Parses postdata from a request.
	 *
	 * @param postData
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	public static HashMap<String, Object> parsePostData(byte[] postData) throws Exception {
		HashMap<String, Object> returnValues = new HashMap<>();
		ByteArrayInputStream in = new ByteArrayInputStream(postData);
		while(in.available() > 0) {
			System.out.println(in.available());
			HashMap<String, String> sectionHeaders = new HashMap<>();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while(true) {
				if(new String(baos.toByteArray()).endsWith("\r\n\r\n")) break;
				baos.write(in.read());
			}
			Scanner headerScanner = new Scanner(new ByteArrayInputStream(new String(baos.toByteArray()).trim().getBytes()));
			String line;
			while(headerScanner.hasNextLine() && (line = headerScanner.nextLine()) != null) {
				sectionHeaders.put(line.substring(0, line.indexOf(": ")), line.substring(line.indexOf(": ") + 2));
			}
			if(!sectionHeaders.containsKey("Content-Disposition")) {
				CrimsonLog.warning("Failed to parse section of multipart, missing Content-Disposition!");
				continue;
			}
			String[] dispositionFields = sectionHeaders.get("Content-Disposition").split("; ");
			HashMap<String, String> dispositions = new HashMap<>();
			for(String dispositionField : dispositionFields) {
				if(dispositionField.contains("=")) {
					dispositions.put(dispositionField.split("=")[0], dispositionField.split("=")[1].replace("\"", ""));
				} else {
					dispositions.put(dispositionField, null);
				}
			}
			if(dispositions.containsKey("filename")) {
				//Save the file to temp
				File f = File.createTempFile("crimson", null);
				FileOutputStream fos = new FileOutputStream(f);
				StreamUtils.bridge(in, fos);
				fos.close();
				returnValues.put(dispositions.get("name"), new FileUploadContainer(dispositions.get("filename"), sectionHeaders.get("Content-Type"), new FileInputStream(f)));
				StreamUtils.readLine(in); //Skip the breaker.
				continue;
			}
			baos = new ByteArrayOutputStream();
			StreamUtils.bridge(in, baos);
			returnValues.put(dispositions.get("name"), new String(baos.toByteArray()));
			StreamUtils.readLine(in); //Skip the breaker.
		}
		return returnValues;
	}

	/**
	 * Parses the query parameters from a url and encodes them into a map.
	 *
	 * @param url
	 *
	 * @return
	 */
	public static Map<String, List<String>> getQueryParams(String url) {
		if(url == null) return new HashMap<>();
		try {
			Map<String, List<String>> params = new HashMap<>();
			String[] urlParts = url.split("\\?");
			if(urlParts.length > 1) {
				String query = urlParts[1];
				for(String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if(pair.length > 1) value = URLDecoder.decode(pair[1], "UTF-8");

					List<String> values = params.get(key);
					if(values == null) {
						values = new ArrayList<>();
						params.put(key, values);
					}
					values.add(value);
				}
			}

			return params;
		} catch(UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}

}
