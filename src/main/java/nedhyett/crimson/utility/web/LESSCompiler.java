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

package nedhyett.crimson.utility.web;

import nedhyett.crimson.utility.ExecutionUtils;
import nedhyett.crimson.utility.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * (Created on 25/06/2015)
 *
 * Requires lessc and less-plugin-autoprefix to be installed from NPM.
 *
 * @author Ned Hyett
 */
public class LESSCompiler {

	public static void installNPM() {
		if(!new File("/usr/local/bin/npm").exists()) {
			try {
				Runtime.getRuntime().exec("curl https://www.npmjs.org/install.sh | sh").waitFor();
			} catch(IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void installLESS() {
		if(!new File("/usr/local/bin/lessc").exists()) {
			try {
				Runtime.getRuntime().exec("npm install -g less").waitFor();
			} catch(InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

//    public static final File less_base = new File("crimson-less");
//
//    public static final String lessCompilerURL = "https://github.com/less/less.js/archive/master.zip";
//
//    static { //If LESSCompiler is loaded, assume that we need LESS
//        if(!less_base.exists()) {
//            less_base.mkdir();
//            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(InternetUtils.fetchResource(lessCompilerURL)));
//            ZipEntry entry;
//            try {
//                byte[] buf = new byte[Constants.BUFFER_SIZE];
//                while((entry = zis.getNextEntry()) != null){
//                    if(entry.getName().startsWith("less.js-master/bin") || entry.getName().startsWith("less.js-master/lib") || true){
//                        CrimsonLog.info("Extracting " + entry.getName().replace("less.js-master/", ""));
//                        if(entry.isDirectory()){
//                            new File(less_base, entry.getName().replace("less.js-master/", "")).mkdir();
//                            continue;
//                        }
//                        String name = entry.getName().replace("less.js-master/", "");
//                        File newFile = new File(less_base, name);
//                        FileOutputStream out = new FileOutputStream(newFile);
//                        int len;
//                        while((len = zis.read(buf)) > 0) {
//                            out.write(buf, 0, len);
//                        }
//                        out.close();
//                    }
//                }
//                zis.close();
//                switch(EnumOS.getOS()){
//                    case LINUX:
//                    case MACOSX:
//                        new File(less_base, "bin/lessc").setExecutable(true);
//                        break;
//                    default:
//                        break;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

	public static byte[] compileLess(String less) {
		return compileLess(less.getBytes());
	}

	public static byte[] compileLess(byte[] less) {
		try {
			File f = File.createTempFile(StringUtils.generateRand(15), null);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(less, 0, less.length);
			fos.close();
			byte[] css = ExecutionUtils.executeAndCapture("lessc", new String[]{f.getAbsolutePath(), "--autoprefix=last 10 versions", "--compress"});
			f.delete();
			return css;
		} catch(Exception e) {
			e.printStackTrace();
			return less;
		}
	}

}
