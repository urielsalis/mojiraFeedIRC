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

import java.io.Serializable;

/**
 * Represents a coordinate vector in 2D space.
 * <p>
 * This class can be serialised and used in a Tier4 exchange.
 *
 * @author Ned Hyett
 */
public class Vector2d implements Serializable {

	public double x;
	public double y;

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2d copyFrom(Vector2d from) {
		this.x = from.x;
		this.y = from.y;
		return this;
	}

	public Vector2d copyTo(Vector2d to) {
		to.x = x;
		to.y = y;
		return this;
	}

	public Vector2d set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2d setX(double x) {
		this.x = x;
		return this;
	}

	public Vector2d setY(double y) {
		this.y = y;
		return this;
	}

	public Vector2d add(Vector2d other, boolean local) {
		if(local) {
			x += other.x;
			y += other.y;
			return this;
		} else {
			return new Vector2d(x + other.x, y + other.y);
		}
	}

	public Vector2d add(double x, double y, boolean local) {
		if(local) {
			this.x += x;
			this.y += y;
			return this;
		} else {
			return new Vector2d(this.x + x, this.y + y);
		}
	}

	public double dot(Vector2d other) {
		return x * other.x + y * other.y;
	}

	public double cross(Vector2d other) {
		return (x * other.y) - (y * other.x);
	}

	public double magnitude() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	public double angle(Vector2d other) {
		return dot(other) / (magnitude() * other.magnitude());
	}

	public double translateTo(Vector2d o, boolean accept) {
		double tx = o.x;
		double ty = o.y;

		double totalMoves = 0;

		if(tx < x) totalMoves -= (x - tx);
		if(tx > x) totalMoves += (tx - x);

		if(ty < y) totalMoves -= (y - ty);
		if(ty > y) totalMoves += (ty - y);

		if(accept) copyFrom(o);
		return totalMoves;
	}

	public Vector2d translateBy(Vector2d p, boolean accept) {
		Vector2d pn = new Vector2d(x + p.x, y + p.y);
		if(accept) {
			copyFrom(pn);
			return this;
		}
		return pn;
	}

	public void increaseX() {
		x += 1;
	}

	public void decreaseX() {
		x -= 1;
	}

	public void increaseY() {
		y += 1;
	}

	public void decreaseY() {
		y -= 1;
	}

	@Override
	public String toString() {
		return x + ":" + y;
	}
}
