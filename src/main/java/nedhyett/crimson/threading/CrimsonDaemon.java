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

package nedhyett.crimson.threading;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.eventreactor.EventReactor;
import nedhyett.crimson.logging.MiniLogger;

import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Background Daemon that can run small snippets of code in the background so the main thread can get on with whatever it needs to do.
 * Example of use: loading terrain in a game
 * <p>
 * Tasks are processed in a first-come-first-serve system so it can also be used for ordering tasks that must not take
 * place at the same time.
 * <p>
 * TODO: remove the static components and make it easy to create custom daemon threads via the use of a factory
 *
 * @author Ned Hyett
 */
public class CrimsonDaemon extends Thread {

	public static final EventReactor daemonReactor = new EventReactor("CrimsonDaemon");
	private static final ConcurrentHashMap<UUID, ITask> taskQueue = new ConcurrentHashMap<>();
	private static final MiniLogger logger = CrimsonLog.spawnLogger("CrimsonDaemon");
	private static CrimsonDaemon instance = null;

	private CrimsonDaemon() {
		this.setDaemon(true);
		this.setName("Crimson Daemon");
	}

	public static UUID queueTask(ITask callable) {
		UUID uuid = UUID.randomUUID();
		taskQueue.put(uuid, callable);
		if(instance == null || !instance.isAlive()) {
			instance = new CrimsonDaemon();
			instance.start();
		}
		return uuid;
	}

	public static void cancelTask(UUID uuid) {
		taskQueue.remove(uuid);
	}

	public static int countJobs() {
		return taskQueue.size();
	}

	@Override
	public void run() {
		try {
			while(!this.isInterrupted()) {
				if(taskQueue.isEmpty()) {
					this.interrupt();
				} else {
					for(final Entry<UUID, ITask> ent : taskQueue.entrySet()) {
						try {
							ent.getValue().call();
							taskQueue.remove(ent.getKey());
							daemonReactor.publish(new BackgroundTaskCompleteEvent(ent.getKey()));
						} catch(Exception e) {
							logger.severe("Fatal exception in a task!");
							logger.severe(e);
							taskQueue.remove(ent.getKey());
						}
					}
				}
			}
		} catch(Exception e) {
			logger.fatal("Exception in Crimson Daemon! Expect things to break!");
			logger.fatal("There will be %s unserviced requests!", taskQueue.size());
			flushAll();
			logger.fatal(e);
		}
	}


	private void flushAll() {
		logger.debug("Flushing task queue...");
		taskQueue.clear();
		logger.debug("Flushing response reactor...");
		daemonReactor.flushAll();
	}

}
