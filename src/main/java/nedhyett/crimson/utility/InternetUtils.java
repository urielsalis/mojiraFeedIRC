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
import nedhyett.crimson.swing.prefabs.ProgressWindow;
import nedhyett.crimson.types.IGenericCallback;
import nedhyett.crimson.types.exception.CrimsonException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Utility class to perform file downloads.
 *
 * @author Ned Hyett
 */
public class InternetUtils {

	/**
	 * Converts a string into a URL
	 *
	 * @param url the string to convert
	 *
	 * @return the new URL
	 */
	public static URL getURL(String url) {
		try {
			return new URL(url);
		} catch(MalformedURLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Open a connection to a URL.
	 *
	 * @param url the url to open a connection to
	 *
	 * @return the connection to the URL
	 */
	public static HttpURLConnection getConnection(String url) {
		return getConnection(getURL(url));
	}

	/**
	 * Open a connection to a URL.
	 *
	 * @param u the URL to open a connection to
	 *
	 * @return the connection to the URL
	 */
	public static HttpURLConnection getConnection(URL u) {
		try {
			return (HttpURLConnection) u.openConnection();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Fetch a resource from the internet and put it into a byte array.
	 *
	 * @param urlString the URL to fetch
	 *
	 * @return the response from the server
	 */
	public static byte[] fetchResource(String urlString) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		fetchResource(baos, urlString);
		CrimsonLog.debug("Fetched %s bytes!", baos.size());
		return baos.toByteArray();
	}

	/**
	 * Fetch a resource from the internet and save it into a file.
	 *
	 * @param filename  the file to save to
	 * @param urlString the URL to fetch
	 */
	public static boolean fetchResource(String filename, String urlString) {
		try {
			File f = new File(filename);
			if(!f.exists()) f.createNewFile();
			return fetchResource(f, urlString);
		} catch(IOException ex) {
			CrimsonLog.severe(ex);
			return false;
		}
	}

	public static boolean fetchResource(File file, String urlString) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			return fetchResource(out, urlString);
		} catch(IOException ex) {
			CrimsonLog.severe(ex);
			return false;
		} finally {
			try {
				if(out != null) out.close();
			} catch(IOException ex) {
				CrimsonLog.severe(ex);
			}
		}
	}

	/**
	 * Fetch a resource from the internet and put it into a byte array without bringing up a progress display.
	 *
	 * @param urlString the URL to fetch
	 *
	 * @return the response from the server
	 */
	public static byte[] fetchResourceHeadless(String urlString) {
		return fetchResourceHeadless(urlString, objects -> {});
	}

	/**
	 * Fetch a resource from the internet and put it into a byte array without bringing up a progress display.
	 *
	 * @param urlString        the URL to fetch
	 * @param progressCallback
	 *
	 * @return the response from the server
	 */
	public static byte[] fetchResourceHeadless(String urlString, IGenericCallback progressCallback) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		fetchResourceHeadless(baos, urlString);
		CrimsonLog.debug("Fetched %s bytes!", baos.size());
		return baos.toByteArray();
	}

	/**
	 * Fetch a resource from the internet and save it into a file without bringing up a progress display.
	 *
	 * @param filename  the file to save to
	 * @param urlString the URL to fetch
	 */
	public static boolean fetchResourceHeadless(String filename, String urlString) {
		try {
			File f = new File(filename);
			if(!f.exists()) {
				if(!f.createNewFile()) throw new CrimsonException("Cannot create new file!");
			}
			return fetchResourceHeadless(f, urlString);
		} catch(IOException ex) {
			CrimsonLog.severe(ex);
			return false;
		}
	}

	public static boolean fetchResourceHeadless(File file, String urlString) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			return fetchResourceHeadless(out, urlString);
		} catch(IOException ex) {
			CrimsonLog.severe(ex);
			return false;
		} finally {
			try {
				if(out != null) out.close();
			} catch(IOException ex) {
				CrimsonLog.severe(ex);
			}
		}
	}

	/**
	 * Fetch a resource from the internet and put it into an output stream.
	 *
	 * @param out       The output stream to write the file to.
	 * @param urlString The URL to request to download the file.
	 */
	public static boolean fetchResource(OutputStream out, String urlString) {
		CrimsonLog.info("Fetching resource %s", urlString);
		BufferedInputStream in = null;
		ProgressWindow pw = new ProgressWindow();
		pw.open();
		pw.updateStatus("Fetching " + urlString);
		pw.setIndeterminate(true);
		try {
			URLConnection conn = getConnection(urlString);
			int maxsize = conn.getContentLength();
			int have = 0;
			in = new BufferedInputStream(conn.getInputStream());

			byte data[] = new byte[Constants.BUFFER_SIZE];
			int count;
			if(maxsize != -1) pw.setIndeterminate(false);
			while((count = in.read(data, 0, Constants.BUFFER_SIZE)) != -1) {
				have += count;
				float progress = ((float) have) / ((float) maxsize);
				pw.updateProgress((int) (progress * 100));
				pw.updateStatus("Fetching " + urlString + " (" + MathsUtil.roundTo(have / (1024f * 1024f), 2) + " MB / " + MathsUtil.roundTo(maxsize / (1024f * 1024f), 2) + "MB)");
				out.write(data, 0, count);
			}
			pw.updateProgress(100);
			pw.updateStatusDirect("Fetch complete!");
			GenericUtils.wait(1);
			pw.dispose();
		} catch(IOException e) {
			CrimsonLog.warning("Failed to fetch resource %s", urlString);
			CrimsonLog.warning(e);
			pw.updateStatusDirect("Failed to fetch resource " + urlString);
			GenericUtils.wait(5);
			pw.dispose();
			if(in != null) {
				try {
					in.close();
				} catch(IOException e1) {
					CrimsonLog.warning(e1);
				}
			}
			return false;
		}
		try {
			in.close();
		} catch(IOException e) {
			CrimsonLog.warning(e);
		}
		return true;
	}

	/**
	 * Fetch a resource from the internet and put it into an OutputStream without bringing up a progress display.
	 *
	 * @param out       the stream to write to
	 * @param urlString the URL to fetch
	 */
	public static boolean fetchResourceHeadless(OutputStream out, String urlString) {
		return fetchResourceHeadless(out, urlString, null);
	}

	/**
	 * Fetch a resource from the internet and put it into an OutputStream without bringing up a progress display.
	 *
	 * @param out              the stream to write to
	 * @param urlString        the URL to fetch
	 * @param progressCallback
	 */
	public static boolean fetchResourceHeadless(OutputStream out, String urlString, IGenericCallback progressCallback) {
		CrimsonLog.info("Fetching resource %s", urlString);
		BufferedInputStream in = null;
		try {
			URLConnection conn = getConnection(urlString);
			((HttpURLConnection)conn).setInstanceFollowRedirects(true);
			in = new BufferedInputStream(conn.getInputStream());
			byte data[] = new byte[Constants.BUFFER_SIZE];
			int count;
			int maxsize = conn.getContentLength();
			int have = 0;
			while((count = in.read(data, 0, Constants.BUFFER_SIZE)) != -1) {
				have += count;
				float progress = ((float) have) / ((float) maxsize);
				if(progressCallback != null) {
					progressCallback.call(have, maxsize, progress * 100);
				}
				out.write(data, 0, count);
			}
			if(progressCallback != null) {
				progressCallback.call(have, maxsize, 100);
			}
			System.out.println("Got " + have + " bytes!");
		} catch(IOException e) {
			CrimsonLog.warning("Failed to fetch resource %s", urlString);
			CrimsonLog.warning(e);
			if(in != null) {
				try {
					in.close();
				} catch(IOException e1) {
					CrimsonLog.warning(e1);
				}
			}
			return false;
		}
		try {
			in.close();
		} catch(IOException e) {
			CrimsonLog.warning(e);
		}
		return true;
	}

	/**
	 * Send a post request to the provided URL.
	 *
	 * @param url  the URL to send a POST request to
	 * @param pars the parameters to use
	 *
	 * @return the response from the server
	 *
	 * @throws IOException
	 */
	public static String postURL(String url, URLData... pars) throws IOException {
		if(!url.endsWith("?")) url += "?";
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		for(URLData d : pars) sb.append(d.key).append("=").append(d.value).append("&");
		String data = sb.toString();
		data = data.substring(0, data.length() - 1);
		HttpURLConnection conn = getConnection(data);
		conn.setRequestMethod("POST");
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		sb = new StringBuilder();
		String line;
		while((line = br.readLine()) != null) sb.append(line);
		return sb.toString();
	}

	/**
	 * Send a GET request to the provided URL.
	 *
	 * @param url  the URL to send a GET request to
	 * @param pars the parameters to use
	 *
	 * @return the response from the server
	 *
	 * @throws IOException
	 */
	public static String getURL(String url, URLData... pars) throws IOException {
		if(!url.endsWith("?")) url += "?";
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		for(URLData d : pars) sb.append(d.key).append("=").append(d.value).append("&");
		String data = sb.toString();
		data = data.substring(0, data.length() - 1);
		HttpURLConnection conn = getConnection(data);
		conn.setRequestMethod("GET");
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		sb = new StringBuilder();
		String line;
		while((line = br.readLine()) != null) sb.append(line);
		return sb.toString();
	}

	/**
	 * Checks if the URL can be reached by sending a HEAD request and checking the response code.
	 *
	 * @param url the URL to check
	 *
	 * @return true if we got a valid response.
	 */
	public static boolean canReachResource(String url) {
		try {
			HttpURLConnection conn = getConnection(url);
			conn.setConnectTimeout(300);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("HEAD");
			return conn.getResponseCode() < 400;
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Follow the URL to the destination and return the actual URL.
	 *
	 * @param start the URL to start from.
	 *
	 * @return the final URL that we arrived at.
	 */
	public static String followURL(String start) {
		try {
			HttpURLConnection conn = getConnection(start);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("HEAD");
			return conn.getHeaderField("Location");
		} catch(IOException ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public static ArrayList<String> parseRawText(String url) {
		try {
			HttpURLConnection conn = getConnection(url);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("GET");
			return StreamUtils.getLines(conn.getInputStream());
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}


}
