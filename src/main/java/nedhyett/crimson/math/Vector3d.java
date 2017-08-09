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

import nedhyett.crimson.utility.MathsUtil;

import java.io.Serializable;

/**
 * Represents a coordinate vector in 3D space.
 * <p>
 * This class can be serialised and used in a Tier4 exchange.
 *
 * @author Ned Hyett
 */
public class Vector3d implements Serializable {

	public double x;
	public double y;
	public double z;

	public Vector3d() {
		this(0, 0, 0);
	}

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d copyFrom(Vector3d from) {
		this.x = from.x;
		this.y = from.y;
		this.z = from.z;
		return this;
	}

	public Vector3d copyTo(Vector3d to) {
		to.x = x;
		to.y = y;
		to.z = z;
		return this;
	}

	public Vector3d setX(double x) {
		this.x = x;
		return this;
	}

	public Vector3d setY(double y) {
		this.y = y;
		return this;
	}

	public Vector3d setZ(double z) {
		this.z = z;
		return this;
	}

	public Vector3d add(Vector3d other, boolean local) {
		if(local) {
			x += other.x;
			y += other.y;
			z += other.z;
			return this;
		} else {
			return new Vector3d(x + other.x, y + other.y, z + other.z);
		}
	}

	public Vector3d add(double x, double y, double z, boolean local) {
		if(local) {
			this.x += x;
			this.y += y;
			this.z += z;
			return this;
		} else {
			return new Vector3d(this.x + x, this.y + y, this.z + z);
		}
	}

	public Vector3d scale(double by, boolean local) {
	    if(local) {
	        x *= by;
	        y *= by;
	        z *= by;
	        return this;
        } else {
	        return new Vector3d(x * by, y * by, z * by);
        }
    }

    public Vector3d mul(Vector3d by, boolean local) {
	    if(local) {
            x *= by.x;
            y *= by.y;
            z *= by.z;
            return this;
        } else {
	        return new Vector3d(x * by.x, y * by.y, z * by.z);
        }
    }

	@SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDoesntDeclareCloneNotSupportedException"})
	public Vector3d clone() {
		return new Vector3d(x, y, z);
	}

	public boolean equals(Vector3d v3d) {
		return v3d != null && v3d.x == x && v3d.y == y && v3d.z == z;
	}

	public double translateTo(Vector3d p, boolean accept) {
		double tx = p.x;
		double ty = p.y;
		double tz = p.z;

		double totalMoves = 0;

		if(tx < x) totalMoves -= (x - tx);
		if(tx > x) totalMoves += (tx - x);

		if(ty < y) totalMoves -= (y - ty);
		if(ty > y) totalMoves += (ty - y);

		if(tz < z) totalMoves -= (z - tz);
		if(tz > z) totalMoves += (tz - z);
		if(accept) copyFrom(p);
		return totalMoves;
	}

	public Vector3d translateBy(double x, double y, double z) {
		return translateBy(x, y, z, false);
	}

	public Vector3d translateBy(double x, double y, double z, boolean accept) {
		return translateBy(new Vector3d(x, y, z), accept);
	}

	public Vector3d translateBy(Vector3d p, boolean accept) {
		Vector3d pn = new Vector3d(x + p.x, y + p.y, z + p.z);
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

	public void increaseZ() {
		z += 1;
	}

	public void decreaseZ() {
		z -= 1;
	}

	public Vector3d floatTowards(double x, double y, double z, double rate) {
		return floatTowards(x, y, z, rate, rate, rate);
	}

	public Vector3d floatTowards(double x, double y, double z, double rateX, double rateY, double rateZ) {
		this.x = MathsUtil.lerp(this.x, x, rateX);
		this.y = MathsUtil.lerp(this.y, y, rateY);
		this.z = MathsUtil.lerp(this.z, z, rateZ);
		return this;
	}

	@Override
	public String toString() {
		return x + ":" + y + ":" + z;
	}
}
