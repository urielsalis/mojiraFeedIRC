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

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Quite random.
 * <p>
 * Created by ned on 09/02/2016.
 */
public class RandomEngine {

	private final SecureRandom randomPicker = new SecureRandom(UUID.randomUUID().toString().getBytes());
	private final SecureRandom[] randoms;

	public RandomEngine(int numRandoms) {
		CrimsonLog.debug("Initialising RandomEngine %s with %s SecurePRNGs...", this.toString().split("@")[1], numRandoms);
		randoms = new SecureRandom[numRandoms];
		for(int i = 0; i < numRandoms; i++) {
			String seed = UUID.randomUUID().toString();
			CrimsonLog.debug("Seeding SecureRandom #%s with key %s", i + 1, seed);
			randoms[i] = new SecureRandom(seed.getBytes());
		}
	}

	public int getRandom(int max) {
		return getRandomRandom().nextInt(max);
	}

	public int getRandomAverage(int max) {
		int num = 0;
		for(SecureRandom random : randoms) num += random.nextInt(max);
		return Math.round(num / randoms.length);
	}

	public SecureRandom getRandomRandom() {
		return randoms[randomPicker.nextInt(randoms.length)];
	}

}
