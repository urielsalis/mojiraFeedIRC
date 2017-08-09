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

package nedhyett.crimson.logging;

import nedhyett.crimson.utility.GenericUtils;

import java.util.ArrayList;

/**
 * Represents a logger that is spawned from CrimsonLog.
 *
 * @author Ned Hyett
 */
public class MiniLogger {

	private final String id;
	private final ArrayList<ILoggerRedirect> redirects = new ArrayList<>();

	protected MiniLogger(String id) {
		this.id = id;
	}

	public void addRedirect(ILoggerRedirect redirect) {
		redirects.add(redirect);
	}

	public void removeRedirect(ILoggerRedirect redirect) {
		redirects.remove(redirect);
	}

	private void log(LogLevel level, String message) {
		CrimsonLog.log(level, this.id, message);
		String formattedMessage = CrimsonLog.doLineFormatting(level, this.id, message);
		for(ILoggerRedirect rdr : redirects) rdr.onLine(formattedMessage);
	}

	private void log(LogLevel level, ArrayList<String> message) {
		CrimsonLog.log(level, this.id, message);
		for(String s : message) {
			String formattedMessage = CrimsonLog.doLineFormatting(level, this.id, s);
			for(ILoggerRedirect rdr : redirects) rdr.onLine(formattedMessage);
		}
	}

	public void fatal(String message) {
		log(LogLevel.FATAL, message);
	}

	public void fatal(Throwable t) {
		log(LogLevel.FATAL, GenericUtils.pullException(t));
	}

	public void fatal(String message, Object... format) {
		fatal(String.format(message, format));
	}

	public void critical(String message) {
		log(LogLevel.CRITICAL, message);
	}

	public void critical(Throwable t) {
		log(LogLevel.CRITICAL, GenericUtils.pullException(t));
	}

	public void critical(String message, Object... format) {
		critical(String.format(message, format));
	}

	public void severe(String message) {
		log(LogLevel.SEVERE, message);
	}

	public void severe(Throwable t) {
		log(LogLevel.SEVERE, GenericUtils.pullException(t));
	}

	public void severe(String message, Object... format) {
		severe(String.format(message, format));
	}

	public void warning(String message) {
		log(LogLevel.WARNING, message);
	}

	public void warning(Throwable t) {
		log(LogLevel.WARNING, GenericUtils.pullException(t));
	}

	public void warning(String message, Object... format) {
		warning(String.format(message, format));
	}

	public void info(String message) {
		log(LogLevel.INFO, message);
	}

	public void info(Throwable t) {
		log(LogLevel.INFO, GenericUtils.pullException(t));
	}

	public void info(String message, Object... format) {
		info(String.format(message, format));
	}

	public void debug(String message) {
		log(LogLevel.DEBUG, message);
	}

	public void debug(Throwable t) {
		log(LogLevel.DEBUG, GenericUtils.pullException(t));
	}

	public void debug(String message, Object... format) {
		debug(String.format(message, format));
	}

	public void dispose() {
		CrimsonLog.dispose(this);
	}

	public String getId() {
		return id;
	}

}
