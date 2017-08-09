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

package nedhyett.crimson.database;

import nedhyett.crimson.logging.CrimsonLog;

import java.io.*;

/**
 * Creates a base for a savable object.
 *
 * @author Ned Hyett
 */
public abstract class ABSavable {

	public static final String IDENTIFIER = "CRM-DB-FILE";

	public ABSavable() {

	}

	/**
	 * Handles saving for the object. You write a save method here that writes data into the provided output stream.
	 *
	 * @param out The stream to write the data to.
	 *
	 * @throws IOException
	 */
	public abstract void save(DataOutputStream out) throws IOException;

	/**
	 * Handles loading of an object. Read the file from the provided input stream. Use (ver < targetversion) to
	 * determine if you should read certain parts of a database file.
	 *
	 * @param in      The stream to read the data from.
	 * @param version The version of the data in the stream.
	 *
	 * @throws IOException
	 */
	public abstract void load(DataInputStream in, int version) throws IOException;

	/**
	 * The name of the file (either absolute or relative to the cwd)
	 *
	 * @return
	 */
	public abstract String getFilename();

	/**
	 * The version of the current database. Increment each time breaking changes are made to the load/save functions.
	 *
	 * @return
	 */
	public abstract int getVersion();

	/**
	 * Save to a file.
	 */
	public void save() {
		try {
			CrimsonLog.debug("Saving file %s to disk!", this.getFilename());
			File db = new File(this.getFilename());
			if(db.exists()) {
				db.delete();
				db.createNewFile();
			}
			DataOutputStream out = new DataOutputStream(new FileOutputStream(db));
			out.writeUTF(IDENTIFIER);
			out.writeInt(getVersion());
			this.save(out);
		} catch(IOException ex) {
			CrimsonLog.warning("Failed to save %s! Can we access it?", this.getFilename());
			CrimsonLog.warning(ex);
		}
	}

	/**
	 * Load from a file.
	 */
	public void load() {
		try {
			CrimsonLog.debug("Loading file %s from disk!", this.getFilename());
			File db = new File(this.getFilename());
			if(!db.exists()) db.createNewFile();
			DataInputStream in = new DataInputStream(new FileInputStream(db));
			int ver = 0;
			try {
				in.mark(in.available());
				if(IDENTIFIER.equalsIgnoreCase(in.readUTF())) {
					ver = in.readInt();
				} else {
					in.reset();
				}
			} catch(IOException e) {
				//Ver doesn't exist yet. Silently ignore and continue as if it was ver 0 to allow legacy loading.
			}

			if(ver > this.getVersion())
				CrimsonLog.critical("File %s exists with version %s. Loader is older version %s. Expect read errors!", this.getFilename(), ver, this.getVersion());
			CrimsonLog.debug("File %s exists with size %s bytes. Loading now...", this.getFilename(), in.available());
			this.load(in, ver); //Use (ver < targetversion) to determine if you should read certain parts of a database file.
			CrimsonLog.debug("File %s loaded successfully with version %s.", this.getFilename(), ver);
		} catch(IOException ex) {
			CrimsonLog.warning("Failed to load %s! This is usually indicative of a corrupt database file! Did you open it in Notepad?", this.getFilename());
			CrimsonLog.warning(ex);
		}
	}

}