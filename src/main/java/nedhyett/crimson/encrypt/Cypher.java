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

package nedhyett.crimson.encrypt;

import java.util.Random;

/**
 * Used to encrypt messages between the server and client.
 * <p>
 * Pros:
 * - Breaks common technique of using spaces to crack ceaser cyphers.
 * - Very hard to crack without knowing key or lots of experience/equipment/money (as far as I know, please prove me wrong)
 * - Encrypts symbols
 * - Capitals and lowercase are encrypted differently.
 * <p>
 * Cons:
 * - Old code (needs updating to remove bottlenecks)
 * - Still aimed towards WinChat, needs generalisation.
 * - Not exactly efficient
 * - Needs support for other symbols (i.e. non-romanised Japanese characters)
 *
 * @author Ned Hyett
 * @deprecated needs rewrite
 */
@Deprecated
public class Cypher {

	public String[] alpha = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
			"x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z", "!", "$", "%", "^", "&", "(", ")", "-", "_",
			"+", "=", "[", "]", "{", "}", "@", ";", ":", "#", "|", "?", ">",
			"<", ",", ".", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			" "};

	public String[] beta = new String[alpha.length];

	public String[] simpleCypher = {" ", "9", "8", "7", "6", "5", "4", "3",
			"2", "1", "0", ".", ",", "<", ">", "?", "|", "#", ":", ";", "@",
			"}", "{", "]", "[", "=", "+", "_", "-", ")", "(", "&", "^", "%",
			"$", "!", "Z", "Y", "X", "W", "V", "U", "T", "S", "R", "Q",
			"P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D",
			"C", "B", "A", "z", "y", "x", "w", "v", "u", "t", "s", "r", "q",
			"p", "o", "n", "m", "l", "k", "j", "i", "h", "g", "f", "e", "d",
			"c", "b", "a"};

	public String fin = "";

	public void configureFromString(String str) {
		String[] str1 = new String[alpha.length];
		for(int i = 0; i < alpha.length; i++) {
			str1[i] = Character.toString(str.charAt(i));
		}
		System.arraycopy(str1, 0, beta, 0, str1.length);
	}

	public String doCeaserCypher(String toCypher) {
		String ret = "";
		for(int i = 0; i < toCypher.length(); i++) {
			boolean isAlpha = false;
			int index = 0;
			for(int q = 0; q < alpha.length; q++) {
				if(alpha[q].equals(Character.toString(toCypher.charAt(i)))) {
					isAlpha = true;
					index = q;
				}
			}
			if(isAlpha) {
				ret += beta[index];
			} else {
				ret += Character.toString(toCypher.charAt(i));
			}
		}
		return ret;
	}

	public String doSimpleCypher(String toCypher) {
		String ret = "";
		for(int i = 0; i < toCypher.length(); i++) {
			boolean isAlpha = false;
			int index = 0;
			for(int q = 0; q < alpha.length; q++) {
				if(alpha[q].equals(Character.toString(toCypher.charAt(i)))) {
					isAlpha = true;
					index = q;
				}
			}
			if(isAlpha) {
				ret = simpleCypher[index] + ret;
			} else {
				ret = Character.toString(toCypher.charAt(i)) + ret;
			}
		}
		return ret;
	}

	public void initialize() {
		int lastLetter = 0;
		for(String alpha1 : alpha) {
			Random rand = new Random();
			boolean foundNew = false;

			while(!foundNew) {
				String str = alpha[rand.nextInt(alpha.length)];
				boolean contains = false;
				for(String beta1 : beta) {
					if(beta1 == null ? str == null : beta1.equals(str)) {
						contains = true;
					}
				}
				if(!contains) {
					beta[lastLetter] = str;
					lastLetter += 1;
					foundNew = true;
				}

			}
		}

		String conc = "";
		for(String beta1 : beta) {
			conc += beta1;
		}
		fin = conc;

	}

	public String undoCeaserCypher(String fromCypher) {
		String ret = "";
		for(int i = 0; i < fromCypher.length(); i++) {
			boolean isAlpha = false;
			int index = 0;
			for(int q = 0; q < alpha.length; q++) {
				if(beta[q].equals(Character.toString(fromCypher.charAt(i)))) {
					isAlpha = true;
					index = q;
				}
			}
			if(isAlpha) {
				ret += alpha[index];
			} else {
				ret += Character.toString(fromCypher.charAt(i));
			}
		}
		return ret;
	}

	public String undoSimpleCypher(String fromCypher) {
		String ret = "";
		for(int i = 0; i < fromCypher.length(); i++) {
			boolean isAlpha = false;
			int index = 0;
			for(int q = 0; q < alpha.length; q++) {
				if(simpleCypher[q].equals(Character.toString(fromCypher.charAt(i)))) {
					isAlpha = true;
					index = q;
				}
			}
			if(isAlpha) {
				ret += alpha[index];
			} else {
				ret += Character.toString(fromCypher.charAt(i));
			}
		}
		return ret;
	}

}
