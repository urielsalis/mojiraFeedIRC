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

package nedhyett.crimson.utility.json;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (Created on 21/04/2016)
 *
 * @author Ned Hyett
 */
public class JSONReader2 {

	private static final Pattern objectPattern = Pattern.compile("\\{(.*)\\}");
	private static final Pattern objectContainerPattern = Pattern.compile("\"([0-9a-zA-Z]*)\":(.*)(?:.*,.*|.*})");

	public static void main(String[] args) {
		test();
	}

	public static void test() {
		String json = "{ \"the game\": { \"test\": \"test1\", \"test2\": \"test3\" } }";
		Matcher m = objectPattern.matcher(json);
		while(m.find()) {
			System.out.println("OP: " + m.group(1).trim());
			Matcher m1 = objectContainerPattern.matcher(m.group(1));
			while(m1.find()) {
				System.out.println("OCP: " + m1.group(1) + m1.group(2));
			}
		}
	}

}
