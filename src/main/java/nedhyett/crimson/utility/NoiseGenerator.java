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

import java.util.Random;

/**
 * (Created on 19/03/2015)
 *
 * @author Ned Hyett
 */
public class NoiseGenerator {

	private Random random;

	public NoiseGenerator(String seed) {
		this(seed.hashCode());
	}

	public NoiseGenerator(long seed) {
		random = new Random();
		random.setSeed(seed);
	}

	public float[][] generateBaseNoise(int w, int d) {
		float[][] b = new float[w][d];
		for(int x = 0; x < w; x++) {
			for(int z = 0; z < d; z++) {
				b[x][z] = random.nextFloat();
			}
		}
		return b;
	}

	private static float interpolate(float x0, float x1, float a) {
		return x0 * (1 - a) + a * x1;
	}

	public float[][] generateSmoothNoise(float[][] b, int o) {
		int w = b.length;
		int h = b[0].length;
		float[][] s = new float[w][h];
		int sp = 1 << o;
		float sf = (float) (1.0 / sp);
		for(int i = 0; i < w; i++) {
			int si0 = (i / sp) * sp;
			int si1 = (si0 + sp) % w;
			float hb = (i - si0) * sf;
			for(int k = 0; k < h; k++) {
				int sk0 = (k / sp) * sp;
				int sk1 = (sk0 + sp) % h;
				float vb = (k - sk0) * sf;
				float tp = interpolate(b[si0][sk0], b[si1][sk0], hb);
				float bt = interpolate(b[si0][sk1], b[si1][sk1], vb);
				s[i][k] = interpolate(tp, bt, vb);
			}
		}
		return s;
	}

	public float[][] generateSmoothNoise(int w, int d, int o) {
		return generateSmoothNoise(generateBaseNoise(w, d), o);
	}

	public float[][] generatePerlinNoise(float[][] b, int o, float p) {
		int w = b.length;
		int h = b[0].length;
		float[][][] s = new float[o][][];
		for(int i = 0; i < o; i++) s[i] = generateSmoothNoise(b, i);
		float[][] pn = new float[w][h];
		float a = 1.0f;
		float ta = 0.0f;
		for(int oc = o - 1; oc >= 0; oc--) {
			a *= p;
			ta += a;
			for(int i = 0; i < w; i++) for(int k = 0; k < h; k++) pn[i][k] += s[oc][i][k] * a;
		}
		for(int i = 0; i < w; i++) for(int k = 0; k < h; k++) pn[i][k] /= ta;
		return pn;
	}

	public float[][] generatePerlinNoise(int w, int d, int o, float p) {
		return generatePerlinNoise(generateBaseNoise(w, d), o, p);
	}

}
