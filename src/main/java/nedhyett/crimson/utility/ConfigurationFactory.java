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
import nedhyett.crimson.utility.reflect.ReflectionHelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * Created by ned on 01/06/2015.
 * <p>
 * Hacky way, using reflection, to import a configuration file to a class.
 */
@Deprecated
public class ConfigurationFactory<T> {

	private final Properties p = new Properties();

	public ConfigurationFactory(String fileLocation) {
		try {
			p.load(new FileToolbox(fileLocation).getInStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public T parse() {
		Type type = getClass().getGenericSuperclass();
		ParameterizedType paramType = (ParameterizedType) type;
		Class<T> ct = (Class<T>) paramType.getActualTypeArguments()[0];
		try {
			T t = ct.newInstance();
			for(Object o : p.keySet()) {
				String s = (String) o;
				if(ReflectionHelper.hasField(ct, s)) {
					Field f = ReflectionHelper.getAndUnlockField(ct, s);
					String val = p.getProperty(s);
					if(StringUtils.isNumber(val)) {
						f.setInt(t, Integer.parseInt(val));
					} else if(StringUtils.isFloat(val)) {
						f.setFloat(t, Float.parseFloat(val));
					} else {
						f.set(t, val);
					}
				}
			}
			return t;
		} catch(InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
