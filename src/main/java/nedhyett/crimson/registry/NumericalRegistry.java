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

package nedhyett.crimson.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * (Created on 17/06/2014)
 *
 * @author Ned Hyett
 */
public class NumericalRegistry<T> implements Iterable<T>, Serializable {

    protected ArrayList<T> items = new ArrayList<>();

    /**
     * Puts an object in the registry.
     *
     * @param item
     * @param item
     */
    public void registerEntry(T item) {
        items.add(item);
    }

    public void removeEntry(T item) {
        items.remove(item);
    }

    public void removeEntry(int idx) {
        items.remove(idx);
    }

    /**
     * Checks if the registry has the entry. Is not case sensitive.
     *
     * @param item
     *
     * @return
     */
    public boolean hasEntry(T item) {
        return items.contains(item);
    }

    /**
     * Pulls an entry from the registry denoted by the provided key. Is not case sensitive.
     *
     * @param key
     *
     * @return
     */
    public T getEntry(int key) {
        return items.get(key);
    }

    /**
     * Get the number of pieces of data stored in this registry.
     *
     * @return
     */
    public int size() {
        return items.size();
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }
}
