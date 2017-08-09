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

package nedhyett.crimson.toolbox;


import nedhyett.crimson.logging.CrimsonLog;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * Wraps a class and allows many reflective operations to be performed on it.
 *
 * @author Ned Hyett
 */
public class ClassToolbox {

	private Class clazz;

	public ClassToolbox(Class clazz) {
		this.clazz = clazz;
	}

	/**
	 * Checks if the provided class is a child of the wrapped class.
	 *
	 * @param other
	 *
	 * @return
	 */
	public boolean isCompatible(Class other) {
		return clazz.isAssignableFrom(other);
	}

	/**
	 * Returns an unlocked and unfinalised version of the field specified in the wrapped class.
	 *
	 * @param name
	 *
	 * @return
	 */
	public Field getAndUnlockField(String name) {
		Field f = null;
		try {
			f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
		} catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return f;
	}

	/**
	 * Finalise a field and prevent it from being edited again.
	 *
	 * @param name
	 */
	public void finalizeField(String name) {
		Field f = getAndUnlockField(name);
		try {
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & Modifier.FINAL);
		} catch(NoSuchFieldException | IllegalAccessException ex) {
			CrimsonLog.severe("Failed to finalize field %s in class %s!", name, clazz);
			CrimsonLog.severe(ex);
		}
	}

	/**
	 * Returns an unlocked method object that allows private methods to be called.
	 *
	 * @param name
	 * @param params
	 *
	 * @return
	 */
	public Method getAndUnlockMethod(String name, Class<?>... params) {
		Method m = null;
		try {
			m = clazz.getDeclaredMethod(name, params);
			m.setAccessible(true);
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		}
		return m;
	}

	/**
	 * Returns an unlocked constructor object that allows private constructors to be called.
	 *
	 * @param parameterTypes
	 *
	 * @return
	 */
	public Constructor getAndUnlockConstructor(Class<?>... parameterTypes) {
		try {
			Constructor con = clazz.getDeclaredConstructor(parameterTypes);
			con.setAccessible(true);
			return con;
		} catch(NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if the wrapped class has a field by the provided name.
	 *
	 * @param name
	 *
	 * @return
	 */
	public boolean hasField(String name) {
		try {
			clazz.getDeclaredField(name);
			return true;
		} catch(NoSuchFieldException e) {
			return false;
		}
	}

	/**
	 * Checks if the wrapped class has a method by the provided name.
	 *
	 * @param name
	 * @param parameterTypes
	 *
	 * @return
	 */
	public boolean hasMethod(String name, Class<?>... parameterTypes) {
		try {
			clazz.getDeclaredMethod(name, parameterTypes);
			return true;
		} catch(NoSuchMethodException e) {
			return false;
		}
	}

	public boolean hasAnnotation(Class<? extends Annotation> annot) {
		return clazz.getAnnotation(annot) != null;
	}

	public <T> T instance(Class<T> as) {
		try {
			return (T)clazz.newInstance();
		} catch(InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> T instance(Class<T> as, Class<?>[] parameterTypes, Object... args) {
		try {
			Constructor constructor = clazz.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return (T) constructor.newInstance(args);
		} catch(InstantiationException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}


}
