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

package nedhyett.crimson.utility;

import nedhyett.crimson.toolbox.FileToolbox;
import nedhyett.crimson.types.exception.BadOSException;

import java.io.IOException;

/**
 * (Created on 15/06/2015)
 *
 * @author Ned Hyett
 */
public class SpeechUtils {

	public static byte[] genSpeech(String text) {
		if(EnumOS.getOS() == EnumOS.MACOSX) {
			String key = StringUtils.generateRand(15);
			try {
				Runtime.getRuntime().exec("say -o ./" + key + ".aiff " + text).waitFor();
				FileToolbox aiff = new FileToolbox("./" + key + ".aiff").load();
				byte[] buf = aiff.getData();
				aiff.delete();
				return buf;
			} catch(IOException | InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		}
		throw new BadOSException();
	}

	public static void speak(String text) {
		if(EnumOS.getOS() == EnumOS.MACOSX) {
			try {
				Runtime.getRuntime().exec("say " + text).waitFor();
			} catch(IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
