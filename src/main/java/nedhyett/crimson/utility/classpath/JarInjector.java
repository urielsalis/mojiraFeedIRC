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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * (Created on 18/06/2015)
 *
 * @author Ned Hyett
 */
public class JarInjector {

	public static void injectIntoActiveJar(byte[] file, String filename) throws Exception { //this is a very hacky method, so why not do a cheap "throws"
		String path = JarInjector.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		if(new File(decodedPath).isFile()) {
			//here are the haxx...
			addFilesToExistingZip(new File(decodedPath), new String[]{filename}, new byte[][]{file});
		} else {
			//we are running in dev mode...
			//just fall back and use FS to inject
			File f = new File(decodedPath, filename);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(file);
			fos.close();
		}
	}

	//http://stackoverflow.com/questions/3048669
	private static void addFilesToExistingZip(File zipFile, String[] fnames, byte[][] files) throws IOException {
		File tempFile = File.createTempFile(zipFile.getName(), null);
		tempFile.delete();

		boolean renameOk = zipFile.renameTo(tempFile);
		if(!renameOk) {
			throw new RuntimeException("Could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		ZipEntry entry = zin.getNextEntry();
		while(entry != null) {
			String name = entry.getName();
			out.putNextEntry(new ZipEntry(name));
			int len;
			while((len = zin.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			entry = zin.getNextEntry();
		}
		zin.close();
		for(int i = 0; i < files.length; i++) {
			out.putNextEntry(new ZipEntry(fnames[i]));
			out.write(files[i]);
			out.closeEntry();
		}
		out.close();
		tempFile.delete();
	}

	public static void injectAndExecuteJar(String filename, String[] args) {
		CrimsonLog.warning("Preparing to inject and execute jar file %s...", filename);
		VFSClassLoader.addURLToSystem(filename);
		try {
			CrimsonLog.warning("Extracting main class...");
			JarFile jar = new JarFile(filename);
			Manifest manifest = jar.getManifest();
			String mc = manifest.getMainAttributes().getValue("Main-Class");
			if(mc != null) {
				CrimsonLog.warning("Found main class %s!", mc);
				CrimsonLog.warning("Executing jar file %s...", filename);
				Class c = Class.forName(mc);
				Method m = c.getMethod("main", String[].class);
				if(args == null) args = new String[0];
				m.invoke(null, (Object) args);
			} else {
				CrimsonLog.severe("Failed to find main class file! Aborting!");
			}
		} catch(IOException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public static void injectAndRunJarThreaded(String filename, String[] args, boolean daemon) {
		Thread t = injectAndBuildJarThread(filename, args);
		t.setDaemon(daemon);
		t.start();
	}

	public static Thread injectAndBuildJarThread(String filename, String[] args) {
		CrimsonLog.warning("Preparing to inject and execute jar file %s...", filename);
		VFSClassLoader.addURLToSystem(filename);
		try {
			CrimsonLog.warning("Extracting main class...");
			JarFile jar = new JarFile(filename);
			Manifest manifest = jar.getManifest();
			String mc = manifest.getMainAttributes().getValue("Main-Class");
			if(mc != null) {
				CrimsonLog.warning("Found main class %s!", mc);
				CrimsonLog.warning("Building thread...");
				Class c = Class.forName(mc);

				final Method m = c.getMethod("main", String[].class);
				if(args == null) args = new String[0];
				final String[] args2 = args;
				Thread t = new Thread() {
					@Override
					public void run() {
						try {
							m.invoke(null, (Object) args2);
						} catch(IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				};
				t.setName("[Crimson JarThread] - " + filename);
				return t;
			} else {
				CrimsonLog.severe("Failed to find main class file! Aborting!");
			}
		} catch(IOException | ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

}
