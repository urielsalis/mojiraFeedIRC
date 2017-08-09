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
import nedhyett.crimson.utility.reflect.ReflectionHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Allows injection of other resources into the classpath.
 *
 * @author Ned Hyett
 */
@Deprecated //use VFSClassLoader
public class ClassPathManager {

//	static {
//		VFSClassLoader.replaceSCL();
//	}

	/**
	 * Inject a file into the classpath.
	 *
	 * @param s
	 */
	public static void addFile(String s) {
		if(s.equalsIgnoreCase("")) return;
		File f = new File(s);
		addFile(f);
	}

	/**
	 * Inject a file directly into the classpath.
	 *
	 * @param f
	 */
	public static void addFile(File f) {
		try {
			addURL(f.toURI().toURL());
		} catch(IOException e) {
			CrimsonLog.warning(e);
		}
	}

	/**
	 * Inject a file directly into the classpath from a resource URL.
	 *
	 * @param u
	 *
	 * @throws IOException
	 */
	public static void addURL(URL u) throws IOException {
		CrimsonLog.warning("Injecting %s into the JVM...", u.toString());
		//VFSClassLoader.addURLToSystem(u);
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        try {
            ReflectionHelper.getAndUnlockMethod(URLClassLoader.class, "addURL", URL.class).invoke(sysloader, u);
        } catch (IllegalAccessException | InvocationTargetException e) {
            CrimsonLog.severe("Failed to add URL %s to system classloader.", u.toString());
            CrimsonLog.severe(e);
        }
	}

}
