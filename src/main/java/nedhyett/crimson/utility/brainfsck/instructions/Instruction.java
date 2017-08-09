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
import nedhyett.crimson.utility.reflect.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * (Created on 21/05/2015)
 *
 * @author Ned Hyett
 */
public abstract class Instruction {

	public static final HashMap<Character, Class<? extends Instruction>> instructions = new HashMap<>();

	static {
		instructions.put('<', BackwardInstruction.class);
		instructions.put('>', ForwardInstruction.class);
		instructions.put('+', IncrementInstruction.class);
		instructions.put('-', DecrementInstruction.class);
		instructions.put('[', StartLoopInstruction.class);
		instructions.put(']', EndLoopInstruction.class);
		instructions.put(',', ReadInstruction.class);
		instructions.put('.', WriteInstruction.class);
	}

	public static boolean has(char c) {
		return instructions.containsKey(c);
	}

	public static Instruction get(char c, int chainCount, ArrayList<Instruction> instructions, HashMap<Integer, Integer> loops) {
		try {
			Class<? extends Instruction> cl = Instruction.instructions.get(c);
			Method m = cl.getDeclaredMethod("buildInstruction", int.class, ArrayList.class, HashMap.class);
			return (Instruction) m.invoke(null, chainCount, instructions, loops);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean canLoop(char c) {
		try {
			Class<? extends Instruction> cl = Instruction.instructions.get(c);
			Method m = ReflectionHelper.getAndUnlockMethod(cl, "canLoop");
			return (boolean) m.invoke(null);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public final int repetitions;
	public final char code;

	public Instruction(int repetitions, char code) {
		this.repetitions = repetitions;
		this.code = code;
	}

	public abstract void execute(BrainfsckEngine engine);

}
