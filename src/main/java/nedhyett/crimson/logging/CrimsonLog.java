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
import nedhyett.crimson.utility.JVMArgsHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * The main Crimson Logging Utility.
 *
 * @author Ned Hyett
 */
public class CrimsonLog {

	/**
	 * A list of all loggers spawned by the main logger.
	 */
	private static final HashMap<String, MiniLogger> childLoggers = new HashMap<>();

	/**
	 * An instance of System.out to be kept when we overwrite the standard one.
	 */
	protected static PrintStream stdout = System.out;

	/**
	 * An instance of System.err to be kept when we overwrite the standard one.
	 */
	protected static PrintStream stderr = System.err;

	/**
	 * The file to write the log output to.
	 */
	private static OutputStream file = null;

	private static final String disableFsFlag = "-Dneptune.crimson.log.disablefs";

	/**
	 * Determines if we should use file IO. Useful on Solid-State systems to prevent excessive writes to disk (such as SD cards in the RPi).
	 */
	private static boolean shouldWriteLogToFile = !JVMArgsHandler.propExists(disableFsFlag) || JVMArgsHandler.checkBool(disableFsFlag);

	/**
	 * The name of this logger.
	 */
	private static String name = "Crimson";

	public static LogLevel minimumLogLevel = LogLevel.DEBUG;

	/**
	 * Create a MiniLogger that has a "zone name".
	 *
	 * @param name zone name.
	 *
	 * @return the new logger.
	 */
	public static MiniLogger spawnLogger(String name) {
		if(childLoggers.containsKey(name)) return childLoggers.get(name);
		MiniLogger child = new MiniLogger(name);
		childLoggers.put(name, child);
		return child;
	}

	/**
	 * Initialise without overwriting the System.out/err. This is used when
	 * Crimson is used in conjunction with another library that already
	 * overrides them (i.e. Forge Mod Loader) to prevent conflicts.
	 *
	 * @param name the name of the base logger.
	 */
	public static void semiInitialise(String name) {
		CrimsonLog.name = name;
		try {
			if(shouldWriteLogToFile) {
				cycleLogFiles();
				file = new FileOutputStream(new File(name.toLowerCase() + ".0.log"));
			}
		} catch(IOException ignored) {
		}
	}

	/**
	 * Fully initialise, set the application name and overwrite the
	 * System.out/err. This may cause issues if another library does the same.
	 *
	 * @param name the name of the base logger.
	 */
	public static void initialise(String name) {
		System.setOut(new PrintStream(new StreamCapture(spawnLogger("STDOUT")), true));
		System.setErr(new PrintStream(new StreamCapture(spawnLogger("STDERR"), true), true));
		semiInitialise(name);
	}

	private static void cycleLogFiles() throws IOException {
		if(!shouldWriteLogToFile) return;
		File f = new File(name.toLowerCase() + ".0.log");
		if(f.exists()) {
			int maxLogs = 3;
			if(new File(name.toLowerCase() + "." + maxLogs + ".log").exists()) {
				new File(name.toLowerCase() + "." + maxLogs + ".log").delete();
			}
			for(int i = maxLogs - 1; i >= 0; i--) {
				if(new File(name.toLowerCase() + "." + i + ".log").exists()) {
					new File(name.toLowerCase() + "." + i + ".log").renameTo(new File(name.toLowerCase() + "." + (i + 1) + ".log"));
				}
			}

		}
		f.createNewFile();
		file = new FileOutputStream(f);
	}

	static void dispose(MiniLogger logger) {
		childLoggers.remove(logger.getId());
	}

	protected static String doLineFormatting(LogLevel level, String id, String message) {
		//Fix the id
		if(id == null) {
			id = " ";
		} else if(!id.startsWith("[") || !id.endsWith("]")) {
			id = " [" + id.replace("[", "").replace("]", "") + "] ";
		}
		return String.format("[%s] [%s]%s[%s]: %s", new Date().toString(), name, id, level.toString().toUpperCase(), message);
	}

	public static void log(LogLevel level, String message) {
		log(level, null, message);
	}

	public static void log(LogLevel level, String id, String message) {
		if(level.ordinal() < minimumLogLevel.ordinal()) return;
		String formattedMessage = doLineFormatting(level, id, message);
		if(level.isError) {
			try {
				if(file != null) file.write((formattedMessage + "\n").getBytes());
			} catch(IOException ignored) {}
			stderr.println(formattedMessage);
		} else {
			try {
				if(file != null) file.write((formattedMessage + "\n").getBytes());
			} catch(IOException ignored) {}
			stdout.println(formattedMessage);
		}
	}

	protected static void log(LogLevel level, ArrayList<String> list) {
		for(String s : list) {
			if(s == null || s.isEmpty()) continue;
			log(level, s);
		}
	}

	protected static void log(LogLevel level, String id, ArrayList<String> list) {
		for(String s : list) {
			if(s == null || s.isEmpty()) continue;
			log(level, id, s);
		}
	}

	public static void fatal(String message) {
		log(LogLevel.FATAL, message);
	}

	public static void fatal(Throwable t) {
		log(LogLevel.FATAL, GenericUtils.pullException(t));
	}

	public static void fatal(String message, Object... format) {
		fatal(String.format(message, format));
	}

	public static void critical(String message) {
		log(LogLevel.CRITICAL, message);
	}

	public static void critical(Throwable t) {
		log(LogLevel.CRITICAL, GenericUtils.pullException(t));
	}

	public static void critical(String message, Object... format) {
		critical(String.format(message, format));
	}

	public static void severe(String message) {
		log(LogLevel.SEVERE, message);
	}

	public static void severe(Throwable t) {
		log(LogLevel.SEVERE, GenericUtils.pullException(t));
	}

	public static void severe(String message, Object... format) {
		severe(String.format(message, format));
	}

	public static void warning(String message) {
		log(LogLevel.WARNING, message);
	}

	public static void warning(Throwable t) {
		log(LogLevel.WARNING, GenericUtils.pullException(t));
	}

	public static void warning(String message, Object... format) {
		warning(String.format(message, format));
	}

	public static void info(String message) {
		log(LogLevel.INFO, message);
	}

	public static void info(Throwable t) {
		log(LogLevel.INFO, GenericUtils.pullException(t));
	}

	public static void info(String message, Object... format) {
		info(String.format(message, format));
	}

	public static void debug(String message) {
		log(LogLevel.DEBUG, message);
	}

	public static void debug(Throwable t) {
		log(LogLevel.DEBUG, GenericUtils.pullException(t));
	}

	public static void debug(String message, Object... format) {
		debug(String.format(message, format));
	}

}
