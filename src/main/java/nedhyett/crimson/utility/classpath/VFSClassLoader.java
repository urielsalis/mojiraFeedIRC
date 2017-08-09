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

package nedhyett.crimson.utility.classpath;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.logging.MiniLogger;
import nedhyett.crimson.utility.reflect.ReflectionHelper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * (Created on 18/06/2015)
 *
 * @author Ned Hyett
 */
public class VFSClassLoader extends URLClassLoader {

	private static final MiniLogger logger = CrimsonLog.spawnLogger("VFS System");

	private final URLClassLoader oldLoader;

	public VFSClassLoader(URLClassLoader oldLoader) {
		super(oldLoader.getURLs(), oldLoader);
		this.oldLoader = oldLoader;
	}

	/**
	 * Checks for the existence of the VFSClassLoader, and if it does not
	 * exist, injects it into the thread.
	 *
	 * @param delegate
	 */
	public static void init(URLClassLoader delegate) {
		if(Thread.currentThread().getContextClassLoader() instanceof VFSClassLoader) return;
		logger.info("Overriding Context Classloader...");
		URLClassLoader old = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		if(delegate != null) old = delegate;
		Thread.currentThread().setContextClassLoader(new VFSClassLoader(old));
		assert Thread.currentThread().getContextClassLoader() instanceof VFSClassLoader;
	}

	/**
	 * Doing this is quite dangerous, you know. (It also probably only works on the Oracle JVM.)
	 */
	public static void replaceSCL() {
		if(ClassLoader.getSystemClassLoader() instanceof VFSClassLoader) return;
		try {
			ReflectionHelper.getAndUnlockField(ClassLoader.class, "scl").set(null, new VFSClassLoader((URLClassLoader) ClassLoader.getSystemClassLoader()));
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		logger.info("Overridden SCL!");
	}

	public void addURLToVFS(URL u) {
		this.addURL(u);
		System.out.println("Added " + u.getPath());
	}

	public static void addURLToSystem(String str) {
		addURLToSystem(new File(str));
	}

	public static void addURLToSystem(File file) {
		try {
			addURLToSystem(file.toURI().toURL());
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void addURLToSystem(URL u) {
		if(ClassLoader.getSystemClassLoader() instanceof VFSClassLoader) {
			((VFSClassLoader) ClassLoader.getSystemClassLoader()).addURLToVFS(u);
		} else {
			throw new VFSException("System ClassLoader is not VFSClassLoader!");
		}
	}

	public static void addURLToContext(String str) {
		addURLToContext(new File(str));
	}

	public static void addURLToContext(File file) {
		try {
			addURLToContext(file.toURI().toURL());
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void addURLToContext(URL u) {
		if(Thread.currentThread().getContextClassLoader() instanceof VFSClassLoader) {
			((VFSClassLoader) Thread.currentThread().getContextClassLoader()).addURLToVFS(u);
		} else {
			throw new VFSException("Context ClassLoader is not VFSClassLoader!");
		}
	}

}
