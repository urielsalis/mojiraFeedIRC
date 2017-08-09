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

package nedhyett.crimson.types;

import nedhyett.crimson.toolbox.FileToolbox;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * Creates a basic configuration structure by wrapping a properties file.
 *
 * @author Ned Hyett
 */
public class Configuration implements Serializable {

	private Properties file = new Properties();

	public Configuration(String path) {
		FileToolbox toolbox = new FileToolbox(path);
		try {
			file.load(toolbox.getInStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public String getString(String key, String def) {
		return file.getProperty(key, def);
	}

	public int getInt(String key, int def) {
		return Integer.parseInt(file.getProperty(key, "" + def));
	}

	public boolean getBoolean(String key, boolean def) {
		return file.getProperty(key, ((def) ? "true" : "false")).equalsIgnoreCase("true");
	}

}
