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

package nedhyett.crimson.utility.brainfsck;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Hyper-fast custom brainfsck engine. Instead of an "interpreter", this is a "preprocessor".
 * <p>
 * The engine performs the following optimisations on the code:
 * 1. All non-instruction characters are removed from the code.
 * 2. All chained instructions (i.e. ++++++) are shortened to one symbol and counted.
 * 3. All loop positions are calculated before execution, so the engine can jump back instantly using a lookup table.
 * 4. The brainfsck code is converted into basic Java classes, meaning that the actual execution loop inside this class is only 4 lines long.
 * <p>
 * As well as this, the code is very extensible, as it would only require adding a class to <code>Instruction.instructions</code>
 * to add a new instruction to the engine. (this extensibility is achieved through hacky reflection, however)
 *
 * @author Ned Hyett
 */
public class BrainfsckEngine {

	public byte[] mem;
	public int memSize;
	public int pointer = 0;
	public int instructionPointer = 0;
	private final InputStream tmpIn;
	private InputStreamReader in;
	private final PrintStream out;
	private boolean interrupt = false;
	private boolean executing = false;

	public BrainfsckEngine(int memSize) {
		this(memSize, System.in, System.out);
	}

	public BrainfsckEngine(int memSize, InputStream in, PrintStream out) {
		this.memSize = memSize;
		tmpIn = in;
		mem = new byte[memSize];
		this.out = out;
	}

	public void reset() {
		mem = new byte[mem.length];
		pointer = 0;
		instructionPointer = 0;
		interrupt = false;
	}

	public InputStreamReader getIn() {
		return in;
	}

	public PrintStream getOut() {
		return out;
	}

	public void execute(BrainfsckProgram program) {
		this.in = new InputStreamReader(tmpIn);
		instructionPointer = 0;
		pointer = 0;
		executing = true;
		do {
			if(interrupt) break;
			if(instructionPointer >= program.getNumInstructions()) break;
			program.getInstruction(instructionPointer).execute(this);
		} while(instructionPointer++ < program.getNumInstructions());
		executing = false;
	}

	public void interrupt() {
		interrupt = true;
	}

	public boolean isExecuting() {
		return executing;
	}

}
