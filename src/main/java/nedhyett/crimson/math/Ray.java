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

package nedhyett.crimson.math;

/**
 * @author Ned Hyett
 */
public class Ray {

	public final float xStart;
	public final float yStart;
	public final float zStart;

	public final float xLimit;
	public final float yLimit;
	public final float zLimit;

	public final float xVelocity;
	public final float yVelocity;
	public final float zVelocity;

	public Ray(float xStart, float yStart, float zStart, float xVelocity, float yVelocity, float zVelocity) {
		this(xStart, yStart, zStart, 1000, 1000, 1000, xVelocity, yVelocity, zVelocity);
	}

	public Ray(float start, float xVelocity, float yVelocity, float zVelocity) {
		this(start, start, start, xVelocity, yVelocity, zVelocity);
	}

	public Ray(float start, float velocity) {
		this(start, start, start, 1000, 1000, 1000, velocity, velocity, velocity);
	}

	public Ray(float xStart, float yStart, float zStart, float limit, float xVelocity, float yVelocity, float zVelocity) {
		this(xStart, yStart, zStart, limit, limit, limit, xVelocity, yVelocity, zVelocity);
	}

	public Ray(float xStart, float yStart, float zStart, float xLimit, float yLimit, float zLimit, float xVelocity, float yVelocity, float zVelocity) {
		this.xStart = xStart;
		this.yStart = yStart;
		this.zStart = zStart;
		this.xLimit = xLimit;
		this.yLimit = yLimit;
		this.zLimit = zLimit;
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
		this.zVelocity = zVelocity;
	}

	public Vector3d trace(int iteration) {
	    return new Vector3d(xStart, yStart, zStart).mul(new Vector3d(xVelocity, yVelocity, zVelocity), false);
    }

}
