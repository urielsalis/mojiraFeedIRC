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

import nedhyett.crimson.math.Vector2d;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Ned Hyett
 */
public class MathsUtil {

	private static final Random rng = new Random();

	/**
	 * Computes a sine wave.
	 *
	 * @param length    How many times longer should the wave be (makes it more spaced apart)
	 * @param amplitude How wibbly it should be
	 *
	 * @return
	 */
	public static double[] computeSineWave(double length, double amplitude, double resolution) {
		double[] ret = new double[64];
		int count = 0;
		for(double i = 0; i < 6.28d; i += resolution) {
			ret[count] = (amplitude * Math.sin(length * i));
			count++;
		}
		return ret;
	}

	/**
	 * Get a random number between min and max.
	 *
	 * @param min
	 * @param max
	 *
	 * @return
	 */
	public static int getRandom(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	/**
	 * Maps a circle into coordinate points.
	 *
	 * @param points
	 * @param radius
	 * @param center
	 *
	 * @return
	 */
	public static Vector2d[] mapCircle(int points, double radius, Vector2d center) {
		Vector2d[] ret = new Vector2d[points];
		double slice = 2 * Math.PI / points;
		for(int i = 0; i < points; i++) {
			double angle = slice * i;
			double newX = (center.x + radius * Math.cos(angle));
			double newY = (center.y + radius * Math.sin(angle));
			ret[i] = new Vector2d(newX, newY);
		}
		return ret;
	}

	/**
	 * Maps a circle into coordinate points.
	 *
	 * @param points
	 * @param radius
	 * @param centerX
	 * @param centerY
	 *
	 * @return
	 */
	public static Vector2d[] mapCircle(int points, double radius, int centerX, int centerY) {
		return mapCircle(points, radius, new Vector2d(centerX, centerY));
	}

	/**
	 * Find the closest multiple of a number.
	 *
	 * @param startingVal
	 * @param targetVal
	 * @param multiplesOf
	 *
	 * @return
	 */
	public static int closestMultipleOf(int startingVal, int targetVal, int multiplesOf) {
		while(startingVal < targetVal) {
			startingVal += multiplesOf;
		}
		return startingVal;
	}

	/**
	 * Round the value up to the closest value of to.
	 *
	 * @param val
	 * @param to
	 *
	 * @return
	 */
	public static int roundUp(int val, int to) {
		return (val + 4) / to * to;
	}

	/**
	 * Round the value down to the closest value of to.
	 *
	 * @param val
	 * @param to
	 *
	 * @return
	 */
	public static int roundDown(int val, int to) {
		int temp = val % to;
		return temp < 3 ? val - temp : val + to - temp;
	}

	/**
	 * Round the value to the specified number of decimal points.
	 *
	 * @param num the number to round
	 * @param dp  the number of decimal points.
	 *
	 * @return
	 */
	public static float roundTo(float num, int dp) {
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		if(dp > 0) sb.append(".");
		for(int i = 0; i < dp; i++) sb.append("#");
		DecimalFormat df = new DecimalFormat(sb.toString());
		df.setRoundingMode(RoundingMode.HALF_UP);
		return Float.parseFloat(df.format(num));
	}

	/**
	 * @param mat1
	 * @param mat2
	 *
	 * @return
	 */
	public static int[][] addMatrices(int[][] mat1, int[][] mat2) {
		int[][] ret = new int[mat1.length][mat1[0].length];
		for(int i = 0; i < mat1.length; i++) {
			for(int q = 0; q < mat1[i].length; q++) {
				ret[i][q] = mat1[i][q] + mat2[i][q];
			}
		}
		return ret;
	}

	/**
	 * @param mat1
	 * @param mat2
	 *
	 * @return
	 */
	public static double[][] addMatrices(double[][] mat1, double[][] mat2) {
		double[][] ret = new double[mat1.length][mat1[0].length];
		for(int i = 0; i < mat1.length; i++) {
			for(int q = 0; q < mat1[i].length; q++) {
				ret[i][q] = mat1[i][q] + mat2[i][q];
			}
		}
		return ret;
	}

	/**
	 * @param mat1
	 * @param mat2
	 *
	 * @return
	 */
	public static float[][] addMatrices(float[][] mat1, float[][] mat2) {
		float[][] ret = new float[mat1.length][mat1[0].length];
		for(int i = 0; i < mat1.length; i++) {
			for(int q = 0; q < mat1[i].length; q++) {
				ret[i][q] = mat1[i][q] + mat2[i][q];
			}
		}
		return ret;
	}

	/**
	 * @param mat1
	 * @param mat2
	 *
	 * @return
	 */
	public static int[][] multiplyMatrices(int[][] mat1, int[][] mat2) {
		int[][] ret = new int[mat1.length][mat1[0].length];
		for(int i = 0; i < mat1.length; i++) {
			for(int q = 0; q < mat1[i].length; q++) {
				ret[i][q] = mat1[i][q] * mat2[i][q];
			}
		}
		return ret;
	}

	/**
	 * @param mat1
	 * @param mat2
	 *
	 * @return
	 */
	public static double[][] multiplyMatrices(double[][] mat1, double[][] mat2) {
		double[][] ret = new double[mat1.length][mat1[0].length];
		for(int i = 0; i < mat1.length; i++) {
			for(int q = 0; q < mat1[i].length; q++) {
				ret[i][q] = mat1[i][q] * mat2[i][q];
			}
		}
		return ret;
	}

	/**
	 * @param mat1
	 * @param mat2
	 *
	 * @return
	 */
	public static float[][] multiplyMatrices(float[][] mat1, float[][] mat2) {
		float[][] ret;
		int x = mat1.length;
		int y = mat2.length;
		ret = new float[x][x];
		for(int i = 0; i < x; i++) {
			for(int j = 0; j < y - 1; j++) {
				for(int k = 0; k < y; k++) {
					ret[i][j] += mat1[i][k] * mat2[k][j];
				}
			}
		}
		return ret;
	}

	/**
	 * Calculates a percentage.
	 *
	 * @param total
	 * @param current
	 *
	 * @return
	 */
	public static float calculatePercentage(float total, float current) {
		return ((current / total) * 100);
	}

	/**
	 * Calculates a percentage as an int.
	 *
	 * @param total
	 * @param current
	 *
	 * @return
	 */
	public static int calculatePercentageRounded(float total, float current) {
		return Math.round(calculatePercentage(total, current));
	}

	/**
	 * Converts a number quickly to radians.
	 *
	 * @param degrees
	 *
	 * @return
	 */
	public static float convertToRads(double degrees) {
		return (float) (degrees * (Math.PI / 180));
	}

	/**
	 * Converts a number quickly to degrees
	 *
	 * @param rads
	 *
	 * @return
	 */
	public static float convertToDegrees(double rads) {
		return (float) (rads * (180 / Math.PI));
	}

	/**
	 * Find the distance between two points in a 2D world.
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 *
	 * @return
	 */
	public static float pythagorasDistance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * Find the distance between two points in a 3D world.
	 *
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 *
	 * @return
	 */
	public static double pythagorasDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
	}

	/**
	 * Get a boolean with a fractional chance of actually getting true.
	 *
	 * @param chance
	 *
	 * @return
	 */
	public static boolean getChance(int chance) {
		return rng.nextInt(100) < 100 - chance;
		//return rng.nextInt() > (Integer.MAX_VALUE - (Integer.MAX_VALUE / chance));
	}

	/**
	 * Get the magnitude of a number (discard sign)
	 *
	 * @param number
	 *
	 * @return
	 */
	@Deprecated
	public static double getMagnitude(double number) {
		return Math.abs(number);
	}

	public static boolean isWholeNumber(float number) {
		return ((int) number) == number;
	}

	public static double lerp(double from, double to, double rate) {
		return from + rate * (to - from);
	}

	public static String secondsToHRTime(long seconds) {
		return secondsToHRTime(seconds, false);
	}

	public static String secondsToHRTime(long seconds, boolean backticks) {
		return String.format(backticks ? "`%02d` days, `%02d` hours, `%02d` minutes and `%02d` seconds." : "%02d days, %02d hours, %02d minutes and %02d seconds.",
				TimeUnit.SECONDS.toDays(seconds),
				TimeUnit.SECONDS.toHours(seconds) - TimeUnit.DAYS.toHours(TimeUnit.SECONDS.toDays(seconds)),
				TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds)),
				seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
		);
	}

}
