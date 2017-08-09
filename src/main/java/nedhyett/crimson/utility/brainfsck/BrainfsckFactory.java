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

import nedhyett.crimson.utility.brainfsck.instructions.Instruction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * (Created on 28/11/2016)
 *
 * @author ned
 */
public class BrainfsckFactory {

	/**
	 * Removes all non-instruction characters from the input program.
	 *
	 * @param input
	 *
	 * @return
	 */
	private static String makePure(String input) {
		StringBuilder pure = new StringBuilder();
		for(int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if(Instruction.has(ch)) pure.append(ch);
		}
		return pure.toString();
	}

	/**
	 * Converts all chains to one character.
	 *
	 * @param input
	 *
	 * @return
	 */
	private static String compressString(String input) {
		StringBuilder sb = new StringBuilder();
		char chainType = input.charAt(0);
		sb.append(chainType);
		for(int i = 1; i < input.length(); i++) {
			char ch = input.charAt(i);
			if(!Instruction.canLoop(ch)) {
				sb.append(ch);
				chainType = ch;
				continue;
			}
			if(ch != chainType) {
				chainType = ch;
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Creates a reverse lookup table for all the locations of the loops in the instruction set.
	 *
	 * @param input
	 *
	 * @return
	 */
	private static HashMap<Integer, Integer> analyseLoops(String input) {
		String processedInput = compressString(input);
		HashMap<Integer, Integer> loops = new HashMap<>();
		for(int i = 0; i < processedInput.length(); i++) {
			char ch = processedInput.charAt(i);
			if(ch != '[') continue;
			int offset = 1;
			int count = 0;
			while(true) {
				if(processedInput.charAt(i + offset) == '[') count += 1;
				else if(processedInput.charAt(i + offset) == ']') {
					if(count == 0) {
						loops.put(i, i + offset);
						break;
					} else count -= 1;
				}
				offset += 1;
			}
		}
		return loops;
	}

	/**
	 * Converts the brainfsck code into Java.
	 *
	 * @param input
	 *
	 * @return
	 */
	private static BrainfsckProgram preprocess(String input) {
		ArrayList<Instruction> instructions = new ArrayList<>();
		HashMap<Integer, Integer> loops = analyseLoops(input);
		int chainCount = 1;
		char chainType = input.charAt(0);
		for(int i = 1; i < input.length(); i++) {
			char ch = input.charAt(i);
			if(ch != chainType || !Instruction.canLoop(chainType)) {
				instructions.add(Instruction.get(chainType, chainCount, instructions, loops));
				chainCount = 1;
				chainType = ch;
			} else chainCount += 1;
		}
		instructions.add(Instruction.get(chainType, chainCount, instructions, loops));
		Instruction[] ret = new Instruction[instructions.size()];
		for(int i = 0; i < instructions.size(); i++) ret[i] = instructions.get(i);
		return new BrainfsckProgram(ret);
	}

	public static BrainfsckProgram generateProgram(String program) {
		return preprocess(makePure(program));
	}

}
