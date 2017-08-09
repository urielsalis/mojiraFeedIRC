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

package nedhyett.crimson.threading.scheduler;

import nedhyett.crimson.logging.CrimsonLog;

import java.io.Serializable;

/**
 * Represents a range of values that a time may be between, for example: between 3am and 6am, do something.
 * <p>
 * This class can be serialised and used in a Tier4 exchange.
 * <p>
 * (Created on 17/06/2015)
 *
 * @author Ned Hyett
 */
public class TimeRange implements ITimePeriod, Serializable {

	public int start = 0;
	public int end = 0;

	public TimeRange(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public boolean isInPeriod(int time) {
		if(end < start) throw new IllegalStateException("TimeRange does not support inverted ranges.");
		if(end == start)
			CrimsonLog.debug("Attention: if you are setting TimeRange to have a start and end variable that are the same, consider using StaticTime instead.");
		return start <= time && time <= end;
	}

}
