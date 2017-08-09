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

package nedhyett.crimson.deploader;

import nedhyett.crimson.toolbox.FileToolbox;
import nedhyett.crimson.utility.InternetUtils;
import nedhyett.crimson.utility.json.JSONReader;
import nedhyett.crimson.utility.StreamUtils;
import nedhyett.crimson.utility.classpath.ClassPathManager;
import nedhyett.crimson.utility.json.nodes.JSONNode;
import nedhyett.crimson.utility.json.nodes.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Download dependencies for a project before the JVM crashes due to missing files.
 * <p>
 * JSON file must be formatted as follows:
 * {
 * "BasePath": "base installation path",
 * "Dependencies": [
 * {
 * "Filename": "filename to save the dependency as. DO NOT CHANGE AFTER DISTRIBUTION.",
 * "URL": "the url to obtain the file from",
 * "Version": "version string"
 * }
 * ]
 * }
 *
 * @author Ned Hyett
 */
public class DepLoader {

	private JSONObject base;
	private String basePath;

	public DepLoader(File f) throws FileNotFoundException {
		this(new FileInputStream(f));
	}

	public DepLoader(InputStream in) {
		this(new String(StreamUtils.pullOut(in)));
	}

	public DepLoader(String json) {
		base = JSONReader.parseJSON(json);
		basePath = base.get("BasePath").getAsString();
	}

	public void install() {
		for(JSONNode inode : base.get("Dependencies").getAsArray()) {
			JSONObject item = (JSONObject) inode;
			if(!checkInstalled(item)) {
				installDep(item);
			} else {
				if(!checkUpToDate(item)) {
					deleteDep(item);
					installDep(item);
				}
			}
			injectDep(item);
		}
	}

	private void injectDep(JSONObject item) {
		ClassPathManager.addFile(basePath + "/" + item.get("Filename").getAsString());
	}

	private void deleteDep(JSONObject item) {
		FileToolbox ft = new FileToolbox(basePath + "/" + item.get("Filename").getAsString() + ".meta");
		ft.delete();
		ft = new FileToolbox(basePath + "/" + item.get("Filename").getAsString());
		ft.delete();
	}

	private void installDep(JSONObject item) {
		InternetUtils.fetchResource(basePath + "/" + item.get("Filename").getAsString(), item.get("URL").getAsString());
		FileToolbox ft = new FileToolbox(basePath + "/" + item.get("Filename").getAsString() + ".meta");
		ft.createFile();
		ft.load();
		ft.setData(item.get("Version").getAsString().getBytes());
		ft.write();
	}

	private boolean checkInstalled(JSONObject item) {
		return new File(basePath + "/" + item.get("Filename").getAsString()).exists();
	}

	private boolean checkUpToDate(JSONObject item) {
		FileToolbox ft = new FileToolbox(basePath + "/" + item.get("Filename").getAsString() + ".meta");
		if(ft.exists()) {
			ft.load();
			return item.get("Version").getAsString().equalsIgnoreCase(new String(ft.getData()));
		} else {
			return false;
		}
	}

}
