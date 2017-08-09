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

package nedhyett.crimson.swing;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.types.exception.BadOSException;
import nedhyett.crimson.utility.EnumOS;

import javax.swing.*;

/**
 * Basic common utilities for Swing projects.
 *
 * @author Ned Hyett
 */
public class WindowUtils {

	public static final String LAF_MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	public static final String LAF_JAVA = UIManager.getCrossPlatformLookAndFeelClassName();
	public static final String LAF_NIMBUS = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static final String LAF_AQUA = "com.apple.laf.AquaLookAndFeel"; //MAC ONLY

	/**
	 * Set a LAF classname.
	 *
	 * @param className
	 */
	public static void setLAF(String className) {
		if(LAF_AQUA.equalsIgnoreCase(className) && EnumOS.getOS() != EnumOS.MACOSX) throw new BadOSException();
		try {
			UIManager.setLookAndFeel(className);
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			CrimsonLog.warning("Failed to set LAF '%s'", className);
			CrimsonLog.warning(e);
			setNative(); //Fall back to the native LAF
		}
	}

	/**
	 * Sets the default LAF. Does not use setLAF because that would cause recursion if there was an exception!
	 */
	public static void setNative() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			CrimsonLog.warning("Failed to set Native LAF!");
			CrimsonLog.warning(e);
		}
	}

	public static String getInput(String question) {
		return JOptionPane.showInputDialog(null, question, "Please provide input...", JOptionPane.QUESTION_MESSAGE);
	}

	public static void displayError(String message) {
		JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}

}
