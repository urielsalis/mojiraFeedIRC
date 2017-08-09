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

package nedhyett.crimson.utility.reflect;

import nedhyett.crimson.logging.CrimsonLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Helps you become a bad person and be hated by everyone.
 *
 * @author Ned Hyett
 */
@Deprecated //Use ClassToolbox.
public class ReflectionHelper {

	public static Field getAndUnlockField(String clazz, String name) {
		try {
			return getAndUnlockField(Class.forName(clazz), name);
		} catch(ClassNotFoundException e) {

		}
		return null;
	}

	/**
	 * Takes a class and a field name, and returns an unlocked, unfinalised version.
	 *
	 * @param clazz
	 * @param name
	 *
	 * @return
	 */
	public static Field getAndUnlockField(Class clazz, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			return f;
		} catch(NoSuchFieldException ex) {
			CrimsonLog.severe("Failed to get static field (" + name + ") in class (" + clazz + "): field does not exist!");
			return null;
		} catch(SecurityException ex) {
			CrimsonLog.severe("Failed to get static field (" + name + ") in class (" + clazz + "): security manager intervened!");
			return null;
		} catch(IllegalArgumentException ex) {
			CrimsonLog.severe("Failed to get static field (" + name + ") in class (" + clazz + "): illegal arguments!");
			return null;
		} catch(IllegalAccessException ex) {
			CrimsonLog.severe("Failed to get static field (" + name + ") in class (" + clazz + "): illegal access!");
			return null;
		}
	}

	public static void finalizeField(Class clazz, String name) {
		Field f = getAndUnlockField(clazz, name);
		try {
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & Modifier.FINAL);
		} catch(NoSuchFieldException | IllegalAccessException ex) {
			CrimsonLog.severe("Failed to finalize field %s in class %s!", name, clazz);
			CrimsonLog.severe(ex);
		}
	}

	public static Method getAndUnlockMethod(String clazz, String name, Class<?>... params) {
		try {
			return getAndUnlockMethod(Class.forName(clazz), name, params);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getAndUnlockMethod(Class clazz, String name, Class<?>... params) {
		try {
			Method m = clazz.getDeclaredMethod(name, params);
			m.setAccessible(true);
			return m;
		} catch(NoSuchMethodException ex) {
			CrimsonLog.severe("Failed to get method (%s) in class (%s): method does not exist!", name, clazz);
		} catch(SecurityException ex) {
			CrimsonLog.severe("Failed to get method (%s) in class (%s): security manager intervened!", name, clazz);
		} catch(IllegalArgumentException ex) {
			CrimsonLog.severe("Failed to get method (%s) in class (%s): illegal arguments!", name, clazz);
		}
		return null;
	}

	public static Constructor getAndUnlockConstructor(String clazz, String name, Class<?>... parameterTypes) {
		try {
			Class c = Class.forName(clazz);
			Constructor con = c.getDeclaredConstructor(parameterTypes);
			con.setAccessible(true);
			return con;
		} catch(ClassNotFoundException e) {
			CrimsonLog.severe("Failed to get constructor (%s) in class (%s): class does not exist!", name, clazz);
		} catch(NoSuchMethodException e) {
			CrimsonLog.severe("Failed to get constructor (%s) in class (%s): method does not exist!", name, clazz);
		} catch(SecurityException e) {
			CrimsonLog.severe("Failed to get constructor (%s) in class (%s): security manager intervened!", name, clazz);
		}
		return null;
	}

	public static boolean hasField(String clazz, String field) {
		try {
			return hasField(Class.forName(clazz), field);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean hasField(Class c, String field) {
		try {
			c.getDeclaredField(field);
			return true;
		} catch(NoSuchFieldException e) {
			return false;
		}
	}

	public static boolean hasMethod(String clazz, String method) {
		try {
			return hasMethod(Class.forName(clazz), method);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean hasMethod(Class c, String method) {
		try {
			c.getDeclaredMethod(method);
			return true;
		} catch(NoSuchMethodException e) {
			return false;
		}
	}


}
