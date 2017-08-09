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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ned Hyett
 */
public class StringUtils {

	@Deprecated
	private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

	@Deprecated
	private static final HashMap<String, String> jsReplaces = new HashMap<>();

	private static final String[] randTextSrc = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

	static {
		jsReplaces.put("%3A", ":");
		jsReplaces.put("%3B", ";");
		jsReplaces.put("%40", "@");
		jsReplaces.put("%3C", "<");
		jsReplaces.put("%3E", ">");
		jsReplaces.put("%3D", "=");
		jsReplaces.put("%26", "&");
		jsReplaces.put("%25", "%");
		jsReplaces.put("%24", "$");
		jsReplaces.put("%23", "#");
		jsReplaces.put("%2B", "+");
		jsReplaces.put("%2C", ",");
		jsReplaces.put("%3F", "?");
	}

	/**
	 * Encoding if need escaping %$&+,/:;=?@<>#%
	 *
	 * @param str should be encoded
	 *
	 * @return encoded Result
	 *
	 * @deprecated kinda crap.
	 */
	@Deprecated
	public static String escapeJavascript(String str) {
		try {
			String n = engine.eval(String.format("escape(\"%s\")", str.replaceAll("%20", " "))).toString();
			for(Map.Entry<String, String> e : jsReplaces.entrySet()) {
				n = n.replaceAll(e.getKey(), e.getValue());
			}
			return n;
		} catch(ScriptException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Generate a random alphanumeric string.
	 *
	 * @param len length of the string to generate
	 *
	 * @return the string
	 */
	public static String generateRand(int len) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < len; i++) sb.append(GenericUtils.pickRandom(randTextSrc));
		return sb.toString();
	}

	/**
	 * Split the provided string into sections of the specified size.
	 *
	 * @param string        the string
	 * @param partitionSize the section size
	 *
	 * @return the split string
	 */
	public static List<String> getParts(String string, int partitionSize) {
		List<String> parts = new ArrayList<>();
		String part = "";
		if(!string.contains(" ")) {
			for(int i = 0; i < string.length(); i += partitionSize) {
				parts.add(string.substring(i, Math.min(string.length(), i + partitionSize)));
			}
			return parts;
		}
		for(int i = 0; i < string.length(); i++) {
			if(part.length() >= partitionSize && Character.toString(string.charAt(i)).equals(" ")) {
				parts.add(part);
				part = "";
				continue;
			}
			part += Character.toString(string.charAt(i));
		}
		if(!part.isEmpty()) parts.add(part);
		return parts;
	}

	/**
	 * Get the text in the provided string between the two points
	 *
	 * @param in       the string
	 * @param one      the first string to look for
	 * @param countOne the number of instances of one to skip
	 * @param two      the second string to look for
	 * @param countTwo the number of instances of two to skip
	 *
	 * @return the text between the two points
	 */
	public static String getBetween(String in, String one, int countOne, String two, int countTwo) {
		int inIndex = 0;
		for(int i = 0; i < countOne; i++) inIndex = in.indexOf(one, inIndex + 1);
		inIndex++;
		int endIndex = 0;
		for(int i = 0; i < countTwo; i++) endIndex = in.indexOf(two, endIndex + 1);
		return in.substring(inIndex, endIndex);
	}

	/**
	 * Hacky way of testing if a string is a number. (If you don't like it, give me a better way.)
	 *
	 * @param in the string
	 *
	 * @return true if it is a number
	 */
	public static boolean isNumber(String in) {
		try {
			Integer.parseInt(in);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Hacky way of testing if a string is a float. (If you don't like it, give me a better way.)
	 *
	 * @param in the string
	 *
	 * @return true if it is a float
	 */
	public static boolean isFloat(String in) {
		try {
			Float.parseFloat(in);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	public static boolean isLong(String in) {
		try {
			Long.parseLong(in);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Creates an MD5 hash of the input string.
	 *
	 * @param input the string
	 *
	 * @return the digested string as a hex value
	 */
	public static String digestString(String input) {
		return digestString(input, "MD5");
	}

	/**
	 * Creates an MD5 hash of the input string.
	 *
	 * @param input the string
	 *
	 * @param algo
	 * @return the digested string as a hex value
	 */
	public static String digestString(String input, String algo) {
		String digested = "";
		try {
			MessageDigest md = MessageDigest.getInstance(algo);
			byte[] res = md.digest(input.getBytes());
			StringBuilder sb = new StringBuilder();
			for(byte b1 : res) sb.append(Integer.toHexString(0xFF & b1));
			digested = sb.toString();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return digested;
	}

	/**
	 * Converts the provided string into a list of strings representing each character.
	 *
	 * @param in the string
	 *
	 * @return the split string
	 */
	public static String[] toStringArray(String in) {
		String[] ret = new String[in.length()];
		for(int i = 0; i < ret.length; i++) ret[i] = Character.toString(in.charAt(i));
		return ret;
	}

	/**
	 * Count the instances of needle in string.
	 *
	 * @param haystack the string
	 * @param needle   the needle to look for
	 *
	 * @return the number of times needle appears in string
	 */
	public static int countInString(String haystack, String needle) {
		int numberOfOccurences = 0;
		int index = haystack.indexOf(needle);
		while(index != -1) {
			numberOfOccurences++;
			haystack = haystack.substring(index + needle.length());
			index = haystack.indexOf(needle);
		}
		return numberOfOccurences;
	}

	/**
	 * Remove all instances of needle from string
	 *
	 * @param string the string
	 * @param needle the needle to look for
	 *
	 * @return string without any instances of needle
	 */
	public static String removeFromString(String string, String needle) {
		return string.replace(needle, "");
	}

	/**
	 * Capitalise the first letter of each word.
	 *
	 * @param in the string
	 *
	 * @return the string with each word capitalised
	 */
	public static String capitaliseEachWord(String in) {
		String lin = in.toLowerCase();
		StringTokenizer st = new StringTokenizer(lin, " ");
		StringBuilder sb = new StringBuilder();
		while(st.hasMoreTokens()) {
			String t = st.nextToken();
			sb.append(Character.toUpperCase(t.charAt(0)));
			sb.append(t.substring(1));
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * Set the case of the text at the provided index of a string.
	 *
	 * @param in    the string
	 * @param index the index to modify
	 * @param _case true to make it uppercase, false to make it lowercase
	 *
	 * @return the new string
	 */
	public static String setCase(String in, int index, boolean _case) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < in.length(); i++) {
			if(i == index) {
				sb.append((_case) ? Character.toUpperCase(in.charAt(i)) : Character.toLowerCase(in.charAt(i)));
			} else {
				sb.append(in.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * Turn an ArrayList of String into one string.
	 *
	 * @param in the arraylist
	 *
	 * @return the concatenated list
	 */
	public static String concatArray(ArrayList<String> in) {
		StringBuilder sb = new StringBuilder();
		for(String s : in) sb.append(s);
		return sb.toString();
	}

	/**
	 * Allows characters like '?' to be the delimiter.
	 *
	 * @param input
	 * @param delim
	 *
	 * @return
	 */
	public static String[] robustSplit(String input, String delim) {
		StringTokenizer tokenizer = new StringTokenizer(input, delim);
		List<String> ret = new ArrayList<>();
		while(tokenizer.hasMoreTokens()) ret.add(tokenizer.nextToken());
		return ret.toArray(new String[0]);
	}


	//http://stackoverflow.com/a/366532
	public static String[] getCommandSplit(String input) {
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		Matcher regexMatcher = regex.matcher(input);
		while (regexMatcher.find()) {
			if (regexMatcher.group(1) != null) {
				// Add double-quoted string without the quotes
				matchList.add(regexMatcher.group(1));
			} else if (regexMatcher.group(2) != null) {
				// Add single-quoted string without the quotes
				matchList.add(regexMatcher.group(2));
			} else {
				// Add unquoted word
				matchList.add(regexMatcher.group());
			}
		}
		return matchList.toArray(new String[0]);
	}

	public static String getFileExtension(String path) {
		return path.substring(path.lastIndexOf(".") + 1).toLowerCase();
	}

	public static String decodeBase64(String base64) {
		return new String(Base64.getDecoder().decode(base64));
	}

	public static ZonedDateTime getDateTimeFromString(String in) {
		return getDateTimeFromString(in, false);
	}

	public static ZonedDateTime getDateTimeFromString(String in, boolean american) {
		ZonedDateTime c = ZonedDateTime.now();
		String[] v = in.split(" ");
		int hour,minute,second,day,month,year;
		hour = minute = second = 0;
		day = month = 1;
		year = c.getYear();
		for(String s : v) {
			if(s.contains(":")) {
				//TIME
				String[] tComponents = s.split(":");
				hour = Integer.parseInt(tComponents[0]);
				minute = Integer.parseInt(tComponents[1]);
				second = Integer.parseInt(tComponents[2]);
			} else if(s.contains("/")) {
				//DATE
				String[] dComponents = s.split("/");
				day = Integer.parseInt(dComponents[american ? 1 : 0]);
				month = Integer.parseInt(dComponents[american ? 0 : 1]);
				year = Integer.parseInt(dComponents[2]);
			} else if(s.length() == 3) {

			}
		}
		return ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneId.systemDefault());
	}

}
