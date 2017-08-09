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

package nedhyett.crimson.types;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.utility.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Stores arbitrary data.
 *
 * @author Ned Hyett
 */
public class DataStore implements Serializable {

	private HashMap<String, Object> data = new HashMap<>();
	private boolean locked = false;
	private String uuid = null;

	private void checkLocked() {
		if(locked) throw new IllegalStateException("Cannot write to DataStore that is locked!");
	}

	/**
	 * Add data to the store.
	 *
	 * @param key
	 * @param data
	 */
	public void setData(String key, Object data) {
		checkLocked();
		this.data.put(key, data);
	}

	/**
	 * Get data from the store.
	 *
	 * @param key
	 * @param type
	 * @param def
	 * @param <T>
	 *
	 * @return
	 */
	public <T> T getData(String key, Class<T> type, T def) {
		if(data.get(key) == null) return def;
		return !type.isInstance(data.get(key)) ? def : (T) data.get(key);
	}

	/**
	 * Remove data from the store.
	 *
	 * @param key
	 */
	public void deleteData(String key) {
		checkLocked();
		data.remove(key);
	}

	/**
	 * Delete all data from the store.
	 */
	public void deleteAllData() {
		checkLocked();
		data.clear();
	}

	/**
	 * Get the number of pieces of data stored in the store.
	 *
	 * @return
	 */
	public int countData() {
		return data.size();
	}

	/**
	 * Get the list of keys that can be used to get data from the store.
	 *
	 * @return
	 */
	public Collection<String> getKeys() {
		return data.keySet();
	}

	/**
	 * Check if the store is empty.
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Check if the store has this piece of data.
	 *
	 * @param key
	 *
	 * @return
	 */
	public boolean hasData(String key) {
		return data.containsKey(key);
	}

	/**
	 * Check if the store is in read-only mode.
	 *
	 * @return
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Put the store in read-only mode. Keep the returned string safe, it is the only way to unlock the store.
	 *
	 * @param forever
	 *
	 * @return
	 */
	public String lock(boolean forever) {
		checkLocked();
		String returnedUUID = UUID.randomUUID().toString();
		uuid = !forever ? StringUtils.digestString(returnedUUID) : null;
		locked = true;
		return returnedUUID;
	}

	/**
	 * Unlock this store and put it back into read-write mode.
	 *
	 * @param unlockKey
	 */
	public void unlock(String unlockKey) {
		if(locked) {
			if(uuid == null) throw new IllegalStateException("Cannot unlock permanently locked DataStore!");
			if(uuid.equalsIgnoreCase(StringUtils.digestString(unlockKey))) {
				locked = false;
			} else {
				throw new IllegalStateException("Failed to unlock DataStore with key " + unlockKey + " which failed to hash! (" + StringUtils.digestString(unlockKey) + ")");
			}
		} else {
			CrimsonLog.warning("Attempted to unlock DataStore that is not locked!");
		}
	}

	/**
	 * Copies the data from the provided store into this one.
	 *
	 * @param otherStore
	 */
	public void copyIn(DataStore otherStore) {
		checkLocked();
		otherStore.checkLocked();
		data.putAll(otherStore.data);
	}

	public void moveIn(DataStore otherStore) {
		copyIn(otherStore);
		otherStore.deleteAllData();
	}

}
