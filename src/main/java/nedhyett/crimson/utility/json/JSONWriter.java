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

import nedhyett.crimson.utility.config.AdvancedConfiguration;
import nedhyett.crimson.utility.config.OptionNumberRange;
import nedhyett.crimson.utility.config.ConfigurationDetail;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Writes JSON files. ALWAYS remember to close objects, arrays and values.
 */
public class JSONWriter {

	protected final OutputStream out;
	private boolean firstIL = true;
	private ArrayList<String> levels = new ArrayList<>();

	public JSONWriter(OutputStream out) {
		this.out = out;
	}

	protected void writeRaw(String data) {
		try {
			out.write(data.getBytes());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	protected String getLevel() {
		return levels.get(levels.size() - 1);
	}

	public JSONWriter name(String name) {
		if(!firstIL) writeRaw(",");
		writeRaw("\"" + name + "\":");
		firstIL = false;
		return this;
	}

	public JSONWriter array() {
		if(!firstIL && getLevel().equalsIgnoreCase("A")) writeRaw(",");
		writeRaw("[");
		firstIL = true;
		levels.add("A");
		return this;
	}

	public JSONWriter endArray() {
		writeRaw("]");
		levels.remove(levels.size() - 1);
		firstIL = false;
		return this;
	}

	public JSONWriter object() {
		if(!firstIL && getLevel().equalsIgnoreCase("A")) writeRaw(",");
		writeRaw("{");
		levels.add("O");
		firstIL = true;
		return this;
	}

	public JSONWriter endObject() {
		writeRaw("}");
		levels.remove(levels.size() - 1);
		firstIL = false;
		return this;
	}

	public JSONWriter writeType(Object o) {
		String type = o.getClass().getTypeName();
		switch(type) {
			case "int":
				value((int) o);
				break;
			case "java.lang.String":
				value((String) o);
				break;
			case "boolean":
				value((boolean) o);
				break;
			case "double":
				value((double) o);
				break;
			case "long":
				value((long) o);
				break;
			default:
				writeClassAsObject(o);
				break;
		}
		return this;
	}

	public <T> JSONWriter writeClass(T o) {
		try {
			return writeClass(o, false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public <T> JSONWriter writeClassAsObject(T o) {
		try {
			return writeClass(o, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	private <T> JSONWriter writeClass(T o, boolean asObject) throws Exception {
		Class<? extends T> inst = (Class<? extends T>) o.getClass();
		boolean advanced = o.getClass().isAnnotationPresent(AdvancedConfiguration.class);
		if(asObject) object();
		for(Field f : inst.getDeclaredFields()) {
			if(Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			name(f.getName());
			String type = f.getGenericType().getTypeName();
			String shortType = null;
			if(type.contains("<")) {
				shortType = type.split("<")[0];
			} else {
				shortType = type;
			}
			switch(shortType) {
				case "int":
					if(advanced) {
						object();
						name("value");
						value(f.getInt(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						if(f.isAnnotationPresent(OptionNumberRange.class)) {
							OptionNumberRange optionNumberRange = f.getAnnotationsByType(OptionNumberRange.class)[0];
							name("max");
							value(optionNumberRange.max());
							name("min");
							value(optionNumberRange.min());
						}
						endObject();
					} else {
						value(f.getInt(o));
					}
					break;
				case "java.lang.String":
					if(advanced) {
						object();
						name("value");
						value((String)f.get(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						value((String)f.get(o));
					}
					break;
				case "boolean":
					if(advanced) {
						object();
						name("value");
						value(f.getBoolean(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						value(f.getBoolean(o));
					}
					break;
				case "double":
					if(advanced) {
						object();
						name("value");
						value(f.getDouble(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						value(f.getDouble(o));
					}
					break;
				case "long":
					if(advanced) {
						object();
						name("value");
						value(f.getLong(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						value(f.getLong(o));
					}
					break;
				case "java.util.HashMap":
					if(!type.split("<")[1].split(", ")[0].equalsIgnoreCase("java.lang.String")) {
						continue;
					}
					Class<?> v = Class.forName(type.split("<")[1].split(", ")[1].replace(">", ""));
					if(advanced) {
						object();
						name("value");
						object();
						for(Map.Entry e : ((HashMap<?, ?>) f.get(o)).entrySet()) {
							name((String) e.getKey());
							writeType(e.getValue());
						}
						endObject();
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						object();
						for(Map.Entry e : ((HashMap<?, ?>) f.get(o)).entrySet()) {
							name((String) e.getKey());
							writeType(e.getValue());
						}
						endObject();
					}
					break;
				default:
					if(f.getType().isEnum()) {
						if(advanced) {
							object();
							name("value");
							value(f.get(o).toString());
							ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
							for(ConfigurationDetail cd : details) {
								name(cd.name());
								value(cd.value());
							}
							endObject();
						} else {
							value(f.get(o).toString());
						}
					} else {
						if(advanced) {
							object();
							name("value");
							writeClassAsObject(f.get(o));
							ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
							for(ConfigurationDetail cd : details) {
								name(cd.name());
								value(cd.value());
							}
							endObject();
						} else {
							writeClassAsObject(f.get(o));
						}
					}
					break;
			}
		}
		if(asObject) endObject();
		return this;
	}

	public <T> JSONWriter writeStaticClass(Class<? extends T> o) {
		try {
			return writeStaticClass(o, false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public <T> JSONWriter writeStaticClassAsObject(Class<? extends T> o) {
		try {
			return writeStaticClass(o, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	private <T> JSONWriter writeStaticClass(Class<? extends T> o, boolean asObject) throws Exception {
		boolean advanced = o.isAnnotationPresent(AdvancedConfiguration.class);
		System.out.println(advanced);
		if(asObject) object();
		for(Field f : o.getDeclaredFields()) {
			if(Modifier.isTransient(f.getModifiers())) {
				continue;
			}
			name(f.getName());
			String type = f.getGenericType().getTypeName();
			String shortType = null;
			if(type.contains("<")) {
				shortType = type.split("<")[0];
			} else {
				shortType = type;
			}
			switch(shortType) {
				case "int":
					if(advanced) {
						object();
						name("value");
						value(f.getInt(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						if(f.isAnnotationPresent(OptionNumberRange.class)) {
							OptionNumberRange optionNumberRange = f.getAnnotationsByType(OptionNumberRange.class)[0];
							name("max");
							value(optionNumberRange.max());
							name("min");
							value(optionNumberRange.min());
						}
						endObject();
					} else {
						value(f.getInt(o));
					}
					break;
				case "java.lang.String":
					if(advanced) {
						object();
						name("value");
						value((String)f.get(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						value((String)f.get(o));
					}
					break;
				case "boolean":
					if(advanced) {
						object();
						name("value");
						value(f.getBoolean(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						value(f.getBoolean(o));
					}
					break;
				case "double":
					if(advanced) {
						object();
						name("value");
						value(f.getDouble(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						value(f.getDouble(o));
					}
					break;
				case "long":
					if(advanced) {
						object();
						name("value");
						value(f.getLong(o));
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						value(f.getLong(o));
					}
					break;
				case "java.util.HashMap":
					if(!type.split("<")[1].split(", ")[0].equalsIgnoreCase("java.lang.String")) {
						continue;
					}
					Class<?> v = Class.forName(type.split("<")[1].split(", ")[1].replace(">", ""));
					if(advanced) {
						object();
						name("value");
						object();
						for(Map.Entry e : ((HashMap<?, ?>) f.get(o)).entrySet()) {
							name((String) e.getKey());
							writeType(e.getValue());
						}
						endObject();
						ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
						for(ConfigurationDetail cd : details) {
							name(cd.name());
							value(cd.value());
						}
						endObject();
					} else {
						object();
						for(Map.Entry e : ((HashMap<?, ?>) f.get(o)).entrySet()) {
							name((String) e.getKey());
							writeType(e.getValue());
						}
						endObject();
					}
					break;
				default:
					if(f.getType().isEnum()) {
						if(advanced) {
							object();
							name("value");
							value(f.get(o).toString());
							ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
							for(ConfigurationDetail cd : details) {
								name(cd.name());
								value(cd.value());
							}
							endObject();
						} else {
							value(f.get(o).toString());
						}
					} else {
						if(advanced) {
							object();
							name("value");
							writeClassAsObject(f.get(o));
							ConfigurationDetail[] details = f.getAnnotationsByType(ConfigurationDetail.class);
							for(ConfigurationDetail cd : details) {
								name(cd.name());
								value(cd.value());
							}
							endObject();
						} else {
							writeClassAsObject(f.get(o));
						}
					}
					break;
			}
		}
		if(asObject) endObject();
		return this;
	}

	public JSONWriter value(Object val) {
		if(!firstIL && getLevel().equalsIgnoreCase("A")) writeRaw(",");
		if(val == null) {
			nullVal();
			return this;
		}
		String type = val.getClass().getTypeName();
		String shortType = null;
		if(type.contains("<")) {
			shortType = type.split("<")[0];
		} else {
			shortType = type;
		}
		switch(shortType) {
			case "int":
				value((int)val);
				break;
			case "java.lang.String":
				value((String)val);
				break;
			case "boolean":
				value((boolean)val);
				break;
			case "double":
				value((double)val);
				break;
			case "long":
				value((long)val);
				break;
			default:
				writeRaw("\"" + val.toString().replace("\"", "\\\"") + "\"");
				firstIL = false;
				break;
		}
		return this;
	}

	public JSONWriter value(String val) {
		if(!firstIL && getLevel().equalsIgnoreCase("A")) writeRaw(",");
		if(val == null) {
			nullVal();
			return this;
		}
		writeRaw("\"" + val.replace("\"", "\\\"").replace("\n", "\\n") + "\"");
		firstIL = false;
		return this;
	}

	public JSONWriter value(boolean val) {
		if(!firstIL && getLevel().equalsIgnoreCase("A")) writeRaw(",");
		writeRaw(val ? "true" : "false");
		firstIL = false;
		return this;
	}

	public JSONWriter value(double val) {
		if(!firstIL && getLevel().equalsIgnoreCase("A")) writeRaw(",");
		writeRaw(Double.toString(val));
		firstIL = false;
		return this;
	}

	public JSONWriter value(long val) {
		if(!firstIL && getLevel().equalsIgnoreCase("A")) writeRaw(",");
		writeRaw(Long.toString(val));
		firstIL = false;
		return this;
	}

	private void nullVal() {
		if(!firstIL && getLevel().equalsIgnoreCase("A")) writeRaw(",");
		writeRaw("null");
		firstIL = false;
	}

	public void flush() {
		try {
			out.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}


}
