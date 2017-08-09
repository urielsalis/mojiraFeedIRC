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

import nedhyett.crimson.utility.ArrayUtils;
import nedhyett.crimson.utility.brainfsck.BrainfsckEngine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the end loop instruction (]).
 * <p>
 * (Created on 21/05/2015)
 *
 * @author Ned Hyett
 */
public class EndLoopInstruction extends Instruction {

	//Called through reflection. All Instructions need this.
	public static Instruction buildInstruction(int chainCount, ArrayList<Instruction> instructions, HashMap<Integer, Integer> loops) {
		return new EndLoopInstruction(ArrayUtils.flip(loops).get(instructions.size()));
	}

	public static boolean canLoop() {
		return false;
	}

	public final int start;

	public EndLoopInstruction(int start) {
		super(1, ']');
		this.start = start;
	}

	@Override
	public void execute(BrainfsckEngine engine) {
		if(engine.mem[engine.pointer] != 0) {
			engine.instructionPointer = start - 1;
		}
	}

}
