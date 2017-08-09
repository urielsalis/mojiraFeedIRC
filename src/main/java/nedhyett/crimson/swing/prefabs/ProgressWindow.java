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

package nedhyett.crimson.swing.prefabs;

import nedhyett.crimson.swing.Frame;

import javax.swing.*;
import java.awt.Dialog.ModalExclusionType;

/**
 * Displays progress of a running task.
 *
 * @author Ned Hyett
 */
public class ProgressWindow extends Frame {

	private JLabel label;

	private JProgressBar progress;

	/**
	 * Create a new ProgressWindow.
	 */
	public ProgressWindow() {
		this("Operation Progress...");
	}

	/**
	 * Create a new ProgressWindow.
	 *
	 * @param title
	 */
	public ProgressWindow(String title) {
		super(title, 700, 100);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
	}

	@Override
	public void initElements(JPanel panel) {
		this.label = new JLabel();
		label.setLocation(20, 20);
		label.setBounds(20, 10, 660, 25);
		panel.add(label);
		this.progress = new JProgressBar();
		progress.setBounds(20, 40, 660, 25);
		panel.add(progress);
	}

	/**
	 * Update the status text (appending "..." to the string)
	 *
	 * @param status
	 */
	public void updateStatus(String status) {
		this.updateStatusDirect(status + "...");
	}

	/**
	 * Update the status text
	 *
	 * @param status
	 */
	public void updateStatusDirect(String status) {
		this.label.setText(status);
	}

	/**
	 * Get the status text
	 *
	 * @return
	 */
	public String getStatus() {
		return this.label.getText().replace("...", "");
	}

	/**
	 * Update the maximum value of the progress bar in the window.
	 *
	 * @param amt
	 */
	public void updateMax(int amt) {
		this.progress.setMaximum(amt);
	}

	/**
	 * Update the progress bar
	 *
	 * @param amt
	 */
	public void updateProgress(int amt) {
		this.progress.setValue(amt);
	}

	/**
	 * Set the progress bar to display indeterminate values (used when a percentage cannot be
	 * calculated)
	 *
	 * @param state
	 */
	public void setIndeterminate(boolean state) {
		this.progress.setIndeterminate(state);
	}

}
