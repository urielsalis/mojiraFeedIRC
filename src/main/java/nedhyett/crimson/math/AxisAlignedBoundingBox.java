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
 * Represents a 3D bounding box that only works in parallel with the coordinate axis.
 * <p>
 * This class can be serialised and used in a Tier4 exchange.
 * <p>
 * (Created on 17/03/2015)
 *
 * @author Ned Hyett
 */
public class AxisAlignedBoundingBox implements Serializable {

	public Vector3d min;
	public Vector3d max;

	public AxisAlignedBoundingBox(Vector3d min, Vector3d max) {
		this.min = min;
		this.max = max;
	}

	public AxisAlignedBoundingBox(double mix, double miy, double miz, double max, double may, double maz) {
		this(new Vector3d(mix, miy, miz), new Vector3d(max, may, maz));
	}

	public AxisAlignedBoundingBox expand(double exx, double exy, double exz) {
		return new AxisAlignedBoundingBox(min.add(-exx, -exy, -exz, false), max.add(exx, exy, exz, false));
	}

	public boolean collides(AxisAlignedBoundingBox other) {
		if(other.max.x > this.min.x && other.min.x < this.max.x) {
			if(other.max.y > this.min.y && other.min.y < this.max.y) {
				if(other.max.z > this.min.z && other.min.z < this.max.z) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isInside(Vector3d vec) {
		return min.x < vec.x && vec.x < max.x && min.y < vec.y && vec.y < max.y && min.z < vec.z && vec.z < max.z;
	}

	@SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException", "CloneDoesntCallSuperClone"})
	public AxisAlignedBoundingBox clone() {
		return new AxisAlignedBoundingBox(min, max);
	}

	public boolean equals(AxisAlignedBoundingBox aabb) {
		return aabb != null && min.equals(aabb.min) && max.equals(aabb.max);
	}

}
