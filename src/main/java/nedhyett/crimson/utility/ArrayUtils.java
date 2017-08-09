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

import nedhyett.crimson.types.RandomEngine;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.security.SecureRandom;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Ned Hyett
 */
public class ArrayUtils {

	private static final SecureRandom r = new SecureRandom();

	/**
	 * Glues an array of strings together using the provided glue.
	 *
	 * @param array
	 * @param glue
	 *
	 * @return
	 */
	public static String concatArray(String[] array, String glue) {
		StringBuilder sb = new StringBuilder();
		for(String s : array) {
			if(sb.length() > 0) sb.append(glue);
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * Glues an array of strings together using the provided glue, starting at the provided index of the array.
	 *
	 * @param array
	 * @param glue
	 * @param start
	 *
	 * @return
	 */
	public static String concatArray(String[] array, String glue, int start) {
		if(start >= array.length) return "";
		String[] narray = new String[array.length - start];
		System.arraycopy(array, start, narray, 0, narray.length);
		return concatArray(narray, glue);
	}

	public static String concatArray(String[] array, String glue, int start, int end) {
		if(start >= array.length || start >= end) return "";
		String[] narray = new String[Math.min(array.length, end) - start];
		System.arraycopy(array, start, narray, 0, narray.length);
		return concatArray(narray, glue);
	}

	/**
	 * Flips the values of an ArrayList.
	 *
	 * @param in
	 * @param <V>
	 *
	 * @return
	 */
	public static <V> ArrayList<V> flip(ArrayList<V> in) {
		ArrayList<V> ret = new ArrayList<>();
		for(int i = in.size() - 1; i > -1; i--) ret.add(in.get(i));
		return ret;
	}

	/**
	 * Flips the keys and values of a HashMap so that the keys are now values and the values are now keys.
	 * <p>
	 * Useful for reverse lookup.
	 *
	 * @param in
	 * @param <K>
	 * @param <V>
	 *
	 * @return
	 */
	public static <K, V> Map<V, K> flip(Map<K, V> in) {
		HashMap<V, K> ret = new HashMap<>();
		for(Entry<K, V> e : in.entrySet()) ret.put(e.getValue(), e.getKey());
		return ret;
	}

    public static <K, V> HashMap<V, K> flip(HashMap<K, V> in) {
        HashMap<V, K> ret = new HashMap<>();
        for(Entry<K, V> e : in.entrySet()) ret.put(e.getValue(), e.getKey());
        return ret;
    }

	public static <K, V> NonBlockingHashMap<V, K> flip(NonBlockingHashMap<K, V> in) {
		NonBlockingHashMap<V, K> ret = new NonBlockingHashMap<>();
		for(Entry<K, V> e : in.entrySet()) ret.put(e.getValue(), e.getKey());
		return ret;
	}

	public static <K> boolean contains(K[] in, K value) {
		for(K anIn : in) {
			if(Objects.equals(anIn, value)) return true;
		}
		return false;
	}

	public static <K> K pickRandom(K[] in, RandomEngine engine) {
		if(in.length == 0) return null;
		if(in.length <= 1) return in[0];
		return in[engine.getRandom(in.length)];
	}

	public static <K> K pickRandom(List<K> in, RandomEngine engine) {
		if(in.size() == 0) return null;
		if(in.size() <= 1) return in.get(0);
		return in.get(engine.getRandom(in.size()));
	}

	public static <K> K pickRandom(K[] in) {
		if(in.length == 0) return null;
		if(in.length <= 1) return in[0];
		return in[r.nextInt(in.length)];
	}

	public static <K> void shuffleArray(K[] in) {
		Random rnd = new Random();
		for(int i = in.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			K a = in[index];
			in[index] = in[i];
			in[i] = a;
		}
	}

	public static <K> void shuffleArray(List<K> in) {
		Random rnd = new Random();
		for(int i = in.size() - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			K a = in.get(index);
			in.set(index, in.get(i));
			in.set(i, a);
		}
	}

	public static <K, V> List<Entry<K, V>> shuffleHashmap(HashMap<K, V> in) {
		List<Entry<K, V>> list = new ArrayList<>(in.entrySet());
		Collections.shuffle(list);
		return list;
	}

	public static boolean compareLast(byte[] one, byte[] two) {
		int startIDX = one.length - 1 - two.length;
		if(startIDX <= two.length) return false;
		int twoIDX = 0;
		for(int i = startIDX; i < one.length; i++) {
			if(one[i] != two[twoIDX]) return false;
			twoIDX += 1;
		}
		return true;
	}

	public static <K, V> HashMap<K, V> deepClone(HashMap<K, V> source) {
		HashMap<K, V> clone = new HashMap<>();
		for(Entry<K, V> entry : source.entrySet()) {
			clone.put(entry.getKey(), entry.getValue());
		}
		return clone;
	}

	public static <K> List<K> deepClone(List<K> source) {
		List<K> clone = new ArrayList<>();
		for(K entry : source) {
			clone.add(entry);
		}
		return clone;
	}

	public static <T> int indexOf(T[] arr, T obj) {
		for(int i = 0; i < arr.length; i++) {
			T t = arr[i];
			if(Objects.equals(t, obj)) return i;
		}
		return -1;
	}

	public static <T> T[] reverse(T[] arr) {
		T[] t = arr.clone();
		for(int i = 0; i < t.length; i++) t[i] = null;
		for(int i = t.length - 1; i >= 0; i--) {
			t[t.length - 1 - i] = arr[i];
		}
		return t;
	}

}
