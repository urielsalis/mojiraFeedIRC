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

package nedhyett.crimson.database;

import nedhyett.crimson.logging.CrimsonLog;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Background thread for automatically saving ABDatabase instances over time.
 *
 * @author Ned Hyett
 */
public class AutosaveThread {

	private static final Timer timer = new Timer("Crimson Database Autosave Thread", true);

	private static final ArrayList<ABDatabase> loadedDatabases = new ArrayList<>();

	public static void register(ABDatabase db) {
		loadedDatabases.add(db);
	}

	//This static block only starts an autosave thread when we need it.
	static {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					if(!loadedDatabases.isEmpty()) {
						loadedDatabases.forEach(ABDatabase::save);
					}
				} catch(Exception e) {
					CrimsonLog.warning("Error in AutosaveThread!");
					CrimsonLog.warning(e);
					throw e;
				}
			}
		}, 0, 1000 * 60 * 5);

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				loadedDatabases.forEach(ABDatabase::save);
			}

		});
	}

}