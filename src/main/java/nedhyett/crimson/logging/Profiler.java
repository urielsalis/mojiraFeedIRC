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

package nedhyett.crimson.logging;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Allows tasks to be profiled. Calculates how long each task actually took.
 *
 * @author Ned Hyett
 */
public class Profiler {

	/**
	 * List of tasks currently being timed.
	 */
	private final ArrayList<RunningTask> activeTasks = new ArrayList<>();

	/**
	 * List of all tasks that have been timed.
	 */
	private final HashMap<String, Long> sessionTasks = new HashMap<>();

	/**
	 * The ID of this profiler
	 */
	private final String id;

	/**
	 * Should this profiler print to system.out?
	 */
	private boolean verbose;

	/**
	 * Should this profiler aggregate it's findings?
	 */
	private boolean aggregateTimings;

	/**
	 * Create a new profiler with a specific ID.
	 *
	 * @param id
	 */
	public Profiler(String id) {
		this.id = id;
		verbose = false;
		aggregateTimings = false;
	}

	/**
	 * Create a new profiler with a specific ID and verbose setting.
	 *
	 * @param id
	 * @param verbose
	 */
	public Profiler(String id, boolean verbose) {
		this.id = id;
		this.verbose = verbose;
		aggregateTimings = false;
	}

	/**
	 * Create a new profiler with a specific ID, verbose setting and aggregate setting.
	 *
	 * @param id
	 * @param verbose
	 * @param aggregateTimings
	 */
	public Profiler(String id, boolean verbose, boolean aggregateTimings) {
		this.id = id;
		this.verbose = verbose;
		this.aggregateTimings = aggregateTimings;
	}

	/**
	 * Start timing an activity
	 *
	 * @param id a unique ID
	 */
	public void startActivity(String id) {
		activeTasks.add(new RunningTask(id));
	}

	/**
	 * Stop timing an activity
	 *
	 * @param id a unique ID
	 *
	 * @return the time taken
	 */
	public long endActivity(String id) {
		RunningTask task = getTaskForId(id);
		if(task == null) return -1;
		long timeTaken = (System.nanoTime() * 1000000) - task.creationTime;
		if(aggregateTimings) {
			if(sessionTasks.containsKey(id)) {
				sessionTasks.put(id, sessionTasks.get(id) + timeTaken);
			} else {
				sessionTasks.put(id, timeTaken);
			}
		}
		if(verbose) CrimsonLog.info("[%s] Task %s took %s ms to complete.", this.id, id, timeTaken);
		return timeTaken;
	}

	private RunningTask getTaskForId(String id) {
		if(activeTasks.isEmpty()) return null;
		for(int i = activeTasks.size() - 1; i > -1; i--) {
			if(activeTasks.get(i).id.equals(id)) return activeTasks.remove(i);
		}
		return null;
	}

	/**
	 * Determines if this profiler is posting info into the console.
	 *
	 * @return
	 */
	public boolean getVerbosity() {
		return verbose;
	}

	/**
	 * Set if it is ok for this profiler to post info into the console.
	 *
	 * @param verbose
	 */
	public void setVerbosity(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Determines if this profiler is aggregating the times.
	 *
	 * @return
	 */
	public boolean getAggregationMethod() {
		return aggregateTimings;
	}

	/**
	 * Set if this profiler is supposed to aggregate times.
	 *
	 * @param aggregate
	 */
	public void setAggregationMethod(boolean aggregate) {
		this.aggregateTimings = aggregate;
	}

	/**
	 * Get all aggregate timings
	 *
	 * @return
	 */
	public HashMap<String, Long> getSessionTimings() {
		return sessionTasks;
	}


	private static class RunningTask {

		public final long creationTime;
		public final String id;

		RunningTask(String id) {
			this.id = id;
			creationTime = System.currentTimeMillis();
		}

	}

}
