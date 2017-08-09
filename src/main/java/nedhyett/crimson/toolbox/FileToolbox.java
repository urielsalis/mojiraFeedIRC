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

package nedhyett.crimson.toolbox;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.logging.MiniLogger;
import nedhyett.crimson.utility.GenericUtils;
import nedhyett.crimson.utility.StreamUtils;
import nedhyett.crimson.utility.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.*;

/**
 * Wraps a file instance and allows for common tasks to be performed on it.
 * <p>
 * Don't use the data manipulation methods on large filetypes since all the data
 * is loaded into memory! I'm still working on a way to stream data temporarily
 * into the memory, operate on it and then save it again.
 *
 * @author Ned Hyett
 */
public class FileToolbox {

	private static final MiniLogger logger = CrimsonLog.spawnLogger("FileToolbox");

	/**
	 * The data buffer.
	 */
	private byte[] buf = null;

	/**
	 * True if there *should* be data in the buffer. May become desynced.
	 */
	private boolean loaded = false;
	private File wrappedFile;

	/**
	 * Create a new toolbox from a path. Same as creating a file and passing
	 * that in.
	 *
	 * @param path
	 */
	public FileToolbox(String path) {
		this(new File(path));
	}

	/**
	 * Create a new toolbox from a file instance.
	 *
	 * @param f the file to wrap. <b>CANNOT BE NULL</b>
	 */
	public FileToolbox(File f) {
		this.wrappedFile = f;
	}

	public File getWrappedFile() {
		return wrappedFile;
	}

	/**
	 * Helper method for getChildren()
	 *
	 * @param f
	 *
	 * @return
	 */
	private static ArrayList<File> exploreDirectory(File f, ToolboxDirectoryType type) {
		if(!f.isDirectory()) return new ArrayList<>();
		ArrayList<File> ret = new ArrayList<>();
		for(File f1 : f.listFiles()) {
			ret.addAll(exploreDirectory(f1, type));
			if(type != ToolboxDirectoryType.BOTH) {
				if (type == ToolboxDirectoryType.DIRECTORY && !f1.isDirectory()) continue;
				if(type == ToolboxDirectoryType.FILE && !f1.isFile()) continue;
			}
			ret.add(f1);
		}
		return ret;
	}

	/**
	 * Checks to make sure that we have actually loaded data into the buffer. If
	 * not, it raises an exception.
	 */
	private void checkLoaded() {
		if(!loaded) throw new IllegalStateException("Must load() data into memory before performing this operation!");
	}

	/**
	 * Checks to make sure that we have been passed a directory. If not, it
	 * raises an exception.
	 */
	private void checkIsDirectory() {
		if(!wrappedFile.isDirectory())
			throw new IllegalStateException("Cannot perform this function on a file; must be a directory!");
	}

	/**
	 * Checks to make sure that we have been passed a file. If not, it raises an
	 * exception.
	 */
	private void checkIsFile() {
		if(!wrappedFile.isFile())
			throw new IllegalStateException("Cannot perform this function on a directory; must be a file!");
	}

	/**
	 * Checks to make sure that the file exists. If not, performs some auxiliary
	 * actions or raises an exception.
	 *
	 * @param isDir if this is true, we assume that the target is a directory.
	 * @param safe  if this is true we attempt to create the type specified by
	 *              isDir. If this fails we throw anyway.
	 */
	private void checkExists(boolean isDir, boolean safe) {
		if(!wrappedFile.exists()) {
			if(!safe) {
				throw new IllegalStateException("Cannot perform this function; file does not exist!");
			} else {
				logger.warning("File/Directory %s does not exist! Creating!", wrappedFile.getAbsolutePath());
				if(isDir) {
					wrappedFile.mkdir();
				} else {
					try {
						wrappedFile.createNewFile();
					} catch(IOException ex) {
						logger.severe("Failed to create File/Directory %s! Safe mode disabled; throwing!", wrappedFile.getAbsolutePath());
						throw new IllegalStateException("Cannot perform this function; file does not exist!");
					}
				}
			}
		}
	}

	public String getName() {
		return wrappedFile.getName();
	}

	public String getPath() {
		return wrappedFile.getAbsolutePath();
	}

	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Get an input stream for this file.
	 *
	 * @return
	 */
	public InputStream getInStream() {
		checkExists(false, false);
		checkIsFile();
		try {
			return new BufferedInputStream(new FileInputStream(wrappedFile));
		} catch(FileNotFoundException ex) {
			logger.warning("Failed to open infilestream for %s, file not found!", wrappedFile.getAbsolutePath());
			return null;
		}
	}

	/**
	 * Get an output stream for this file.
	 *
	 * @return
	 */
	public OutputStream getOutStream() {
		checkExists(false, true);
		checkIsFile();
		try {
			return new BufferedOutputStream(new FileOutputStream(wrappedFile));
		} catch(FileNotFoundException ex) {
			logger.warning("Failed to open outfilestream for %s, file not found!", wrappedFile.getAbsolutePath());
			return null;
		}
	}

	public DataInputStream getInDataStream() {
		return StreamUtils.wrapUncompressedStream(getInStream());
	}

	public DataOutputStream getOutDataStream() {
		return StreamUtils.wrapUncompressedStream(getOutStream());
	}

	/**
	 * Load this file into memory.
	 *
	 * @return
	 */
	public FileToolbox load() {
		checkExists(false, false);
		checkIsFile();
		try {
			buf = StreamUtils.getBytes(getInStream());
			loaded = true;
		} catch(Exception e) {
			logger.warning("Failed to load file from disk!");
			logger.warning(e);
		}
		return this;
	}

	/**
	 * Write the data in the memory to the file.
	 *
	 * @return
	 */
	public FileToolbox write() {
		checkLoaded();
		checkExists(false, false);
		checkIsFile();
		try {
			OutputStream out = getOutStream();
			out.write(buf);
			out.flush();
		} catch(Exception e) {
			logger.warning("Failed to write file to disk!");
			logger.warning(e);
		}
		return this;
	}

	/**
	 * Get the loaded data.
	 *
	 * @return
	 */
	public byte[] getData() {
		checkLoaded();
		return buf;
	}

	/**
	 * Set the data in the memory buffer.
	 *
	 * @param in
	 *
	 * @return
	 */
	public FileToolbox setData(byte[] in) {
		if(in == null)
			throw new IllegalArgumentException("setData cannot accept null input! To unload data, use unload()!");
		loaded = true;
		buf = in;
		return this;
	}

	/**
	 * Clears the memory buffer.
	 *
	 * @return
	 */
	public FileToolbox unload() {
		checkLoaded();
		buf = null;
		loaded = false;
		return this;
	}

	/**
	 * Pushes the data in the buffer through a GZIPOutputStream. UNTESTED.
	 *
	 * @return
	 */
	public FileToolbox compress() {
		checkLoaded();
		try {
			buf = StreamUtils.pushThroughOutStream(buf, GZIPOutputStream.class);
		} catch(Exception ex) {
			logger.warning("Failed to compress data!");
			logger.warning(ex);
		}
		return this;
	}

	/**
	 * Pushes the data in the buffer through a GZIPInputStream. UNTESTED.
	 *
	 * @return
	 */
	public FileToolbox decompress() {
		checkLoaded();
		try {
			buf = StreamUtils.pushThroughInStream(buf, GZIPInputStream.class);
		} catch(Exception e) {
			logger.warning("Failed to decompress data!");
			logger.warning(e);
		}
		return this;
	}

	/**
	 * Pushes the data in the buffer into the provided output stream.
	 *
	 * @param out
	 *
	 * @return
	 */
	public FileToolbox upload(OutputStream out) {
		//TODO: check if is directory then zip and upload
		checkLoaded();
		StreamUtils.pushInto(buf, out);
		return this;
	}

	/**
	 * Pushes the data from the file into the provided output stream ignoring
	 * the memory buffer.
	 *
	 * @param out
	 *
	 * @return
	 */
	public FileToolbox uploadDirect(OutputStream out) {
		checkExists(false, false);
		checkIsFile();
		InputStream in = getInStream();
		StreamUtils.bridge(in, out);
		return this;
	}

	/**
	 * Pulls data in from the input stream and puts it in the buffer.
	 *
	 * @param in
	 *
	 * @return
	 */
	public FileToolbox download(InputStream in) {
		buf = StreamUtils.pullOut(in);
		loaded = true;
		return this;
	}

	/**
	 * Pulls data in from the input stream into the file from the provided input
	 * stream ignoring the memory buffer.
	 *
	 * @param in
	 *
	 * @return
	 */
	public FileToolbox downloadDirect(InputStream in) {
		checkExists(false, true);
		checkIsFile();
		OutputStream out = getOutStream();
		StreamUtils.bridge(in, out);
		return this;
	}

	public long getSize() {
		return wrappedFile.length();
	}

	public String getHash() {
		return getHash("MD5");
	}

	public String getHash(String algo) {
		return GenericUtils.digest(buf, algo);
	}

	public String getExtension() {
		return StringUtils.getFileExtension(wrappedFile.getAbsolutePath());
	}

	public FileToolbox createFile() {
		try {
			System.out.println(wrappedFile.getParentFile().getPath());
			if(!wrappedFile.getParentFile().exists()) wrappedFile.getParentFile().mkdirs();
			wrappedFile.createNewFile();
		} catch(IOException e) {
			CrimsonLog.warning(e);
		}
		return this;
	}

	public FileToolbox createDir() {
		if(wrappedFile.exists()) return this;
		wrappedFile.mkdir();
		return this;
	}

	public boolean hasChild(String name) {
		checkExists(true, false);
		FileToolbox child = new FileToolbox(new File(wrappedFile, name));
		return child.exists();
	}

	public FileToolbox getChildDir(String name) {
		checkExists(true, false);
		checkIsDirectory();
		FileToolbox child = new FileToolbox(new File(wrappedFile, name));
		child.checkExists(true, false);
		child.checkIsDirectory();
		return child;
	}

	public FileToolbox getChildFile(String name) {
		checkExists(true, false);
		checkIsDirectory();
		FileToolbox child = new FileToolbox(new File(wrappedFile, name));
		child.checkExists(false, false);
		child.checkIsFile();
		return child;
	}

	public FileToolbox createChildDir(String name) {
		checkExists(true, false);
		checkIsDirectory();
		FileToolbox child = new FileToolbox(new File(wrappedFile, name));
		child.createDir();
		return child;
	}

	public FileToolbox createChildFile(String name) {
		checkExists(false, false);
		checkIsDirectory();
		FileToolbox child = new FileToolbox(new File(wrappedFile, name));
		child.createFile();
		return child;
	}

	public boolean childExists(String name) {
		checkExists(true, false);
		checkIsDirectory();
		File f = new File(wrappedFile, name);
		return f.exists();
	}

	public boolean exists() {
		return wrappedFile.exists();
	}

	public FileToolbox delete() {
		checkExists(false, false);
		if(wrappedFile.isDirectory()) {
			for(FileToolbox ft : getChildrenAsToolboxes(false)) {
				ft.delete();
			}
		}
		wrappedFile.delete();
		return this;
	}

	public FileToolbox queueDelete() {
		checkExists(false, false);
		wrappedFile.deleteOnExit();
		return this;
	}

	public ArrayList<File> getChildren(boolean recursive) {
		return getChildren(recursive, ToolboxDirectoryType.BOTH);
	}

	public ArrayList<File> getChildren(boolean recursive, ToolboxDirectoryType type) {
		checkExists(true, false);
		checkIsDirectory();
		ArrayList<File> ret = new ArrayList<>();
		for(File f : wrappedFile.listFiles()) {
			if(type != ToolboxDirectoryType.BOTH) {
				if (type == ToolboxDirectoryType.DIRECTORY && !f.isDirectory()) continue;
				if(type == ToolboxDirectoryType.FILE && !f.isFile()) {
					if(recursive) {
						ret.addAll(exploreDirectory(f, type));
					}
					continue;
				}
			}
			ret.add(f);
			if(recursive && f.isDirectory()) ret.addAll(exploreDirectory(f, type));
		}
		return ret;
	}

	public ArrayList<FileToolbox> getChildrenAsToolboxes(boolean recursive) {
		return getChildrenAsToolboxes(recursive, ToolboxDirectoryType.BOTH);
	}

	public ArrayList<FileToolbox> getChildrenAsToolboxes(boolean recursive, ToolboxDirectoryType type) {
		ArrayList<File> files = getChildren(recursive, type);
		ArrayList<FileToolbox> toolboxes = new ArrayList<>();
		for(File f : files) {
			toolboxes.add(new FileToolbox(f));
		}
		return toolboxes;
	}

	public String digestFile() {
		checkLoaded();
		String digested = "";
		byte[] b;
		try {
			InputStream fis = new ByteArrayInputStream(buf);
			b = new byte[fis.available()];
			fis.read(b);
		} catch(IOException e) {
			return "";
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] res = md.digest(b);
			StringBuilder sb = new StringBuilder();
			for(byte b1 : res) sb.append(Integer.toHexString(0xFF & b1));
			digested = sb.toString();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			return digested;
		}
	}

	public String digestFileDirect() {
		checkExists(false, false);
		checkIsFile();
		String digested = "";
		byte[] b;
		try {
			InputStream fis = new FileInputStream(wrappedFile);
			b = new byte[fis.available()];
			fis.read(b);
		} catch(IOException e) {
			return "";
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] res = md.digest(b);
			StringBuilder sb = new StringBuilder();
			for(byte b1 : res) sb.append(Integer.toHexString(0xFF & b1));
			digested = sb.toString();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			return digested;
		}
	}

	public FileToolbox wipeFile() {
		checkExists(false, false);
		checkIsFile();
		wrappedFile.delete();
		try {
			wrappedFile.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public FileToolbox rename(String newName) {
	    wrappedFile.renameTo(new File(wrappedFile.getParent(), newName));
	    return this;
    }

	public FileToolbox zipDirectory(FileToolbox output, boolean recursive) {
		try {
			if (!output.exists()) output.createFile();
			ZipOutputStream out = new ZipOutputStream(output.getOutStream());
			ArrayList<FileToolbox> children = getChildrenAsToolboxes(recursive, ToolboxDirectoryType.FILE);
			for (FileToolbox ft : children) {
				System.out.println("Zipping " + ft.getPath().replace(getPath(), ""));
				ZipEntry e = new ZipEntry(ft.getPath().replace(getPath(), ""));
				out.putNextEntry(e);
				ft.load();
				out.write(ft.getData(), 0, ft.getData().length);
				ft.unload();
				out.closeEntry();
			}
			out.close();
		} catch(IOException e) {
			logger.critical("Failed to zip files!");
			logger.critical(e);
		}
		return this;
	}

	public FileToolbox unzipInto(byte[] zipData) {
		try {
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData));
			ZipEntry entry;
			while((entry = zis.getNextEntry()) != null) {
				FileToolbox ft = new FileToolbox(new File(wrappedFile, entry.getName()));
				System.out.println("Extracting: " + ft.getPath() + "...");
				if(entry.isDirectory()) {
					ft.createDir();
					continue;
				}
				ft.createFile();
				byte[] b = StreamUtils.getBytes(zis);
				System.out.println("Writing " + b.length + " bytes...");
				ft.setData(b);
				ft.write();
				ft.unload();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public FileToolbox cloneInto(FileToolbox output) {
        if (!output.exists()) output.createDir();
        ArrayList<FileToolbox> children = getChildrenAsToolboxes(true, ToolboxDirectoryType.FILE);
        for (FileToolbox ft : children) {
            System.out.println("Cloning " + ft.getPath().replace(getPath() + "/", ""));
            ft.load();
            FileToolbox ft1 = output.createChildFile(ft.getPath().replace(getPath() + "/", ""));
            System.out.println("Writing " + ft.getData().length + " bytes...");
            ft1.setData(ft.getData());
            ft1.write();
            ft.unload();
            ft1.unload();
        }
        return this;
    }

	public enum ToolboxDirectoryType {
		DIRECTORY,
		FILE,
		BOTH;
	}

}
