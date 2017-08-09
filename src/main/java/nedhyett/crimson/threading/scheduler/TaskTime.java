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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents an date/time that a task should be run, for example: on the 2nd sunday of an even numbered month every
 * three years, do something.
 * <p>
 * Any fields that are NULL are not counted. There are some fields that contradict each other and should not
 * be used together (they will be evaluated, but will cause unpredictable results).
 * <p>
 * This class can be serialised and used in a Tier4 exchange.
 * <p>
 * (Created on 17/06/2015)
 *
 * @author Ned Hyett
 */
public class TaskTime implements ITimePeriod, Serializable {

	public ITimePeriod year = null;
	public ITimePeriod month = null;
	public ITimePeriod weekOfYear = null;
	public ITimePeriod weekOfMonth = null;
	public ITimePeriod dayOfYear = null;
	public ITimePeriod dayOfMonth = null;
	public ITimePeriod dayOfWeek = null;
	public ITimePeriod hour = null;
	public ITimePeriod minute = null;
	public ITimePeriod second = null;

	@Override
	public boolean isInPeriod(int time) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(time * 1000));
		if(year != null) if(!year.isInPeriod(c.get(Calendar.YEAR))) return false;
		if(month != null) if(!month.isInPeriod(c.get(Calendar.MONTH))) return false;
		if(weekOfYear != null) if(!weekOfYear.isInPeriod(c.get(Calendar.WEEK_OF_YEAR))) return false;
		if(weekOfMonth != null) if(!weekOfMonth.isInPeriod(c.get(Calendar.WEEK_OF_MONTH))) return false;
		if(dayOfYear != null) if(!dayOfYear.isInPeriod(c.get(Calendar.DAY_OF_YEAR))) return false;
		if(dayOfMonth != null) if(!dayOfMonth.isInPeriod(c.get(Calendar.DAY_OF_MONTH))) return false;
		if(dayOfWeek != null) if(!dayOfWeek.isInPeriod(c.get(Calendar.DAY_OF_WEEK))) return false;
		if(hour != null) if(!hour.isInPeriod(c.get(Calendar.HOUR_OF_DAY))) return false;
		if(minute != null) if(!minute.isInPeriod(c.get(Calendar.MINUTE))) return false;
		if(second != null) if(!second.isInPeriod(c.get(Calendar.SECOND))) return false;
		return true;
	}

}
