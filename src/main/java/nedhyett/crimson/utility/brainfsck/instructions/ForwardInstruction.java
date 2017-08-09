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

package nedhyett.crimson.utility.brainfsck.instructions;


import nedhyett.crimson.utility.brainfsck.BrainfsckEngine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a forward instruction (>) to move forward in the memory tape.
 * <p>
 * (Created on 21/05/2015)
 *
 * @author Ned Hyett
 */
public class ForwardInstruction extends Instruction {

	//Called through reflection. All Instructions need this.
	public static Instruction buildInstruction(int chainCount, ArrayList<Instruction> instructions, HashMap<Integer, Integer> loops) {
		return new ForwardInstruction(chainCount);
	}

	public static boolean canLoop() {
		return true;
	}

	public ForwardInstruction(int repetitions) {
		super(repetitions, '>');
	}

	@Override
	public void execute(BrainfsckEngine engine) {
		if(engine.pointer + repetitions >= engine.memSize) {
			engine.pointer = (engine.pointer + repetitions) - engine.memSize;
		} else {
			engine.pointer += repetitions;
		}
	}
}
