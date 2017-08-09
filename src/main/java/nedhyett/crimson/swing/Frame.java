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

import javax.swing.*;

/**
 * Base class for any Crimson-based Swing GUI. Makes creating new Swing windows easy.
 *
 * @author Ned Hyett
 */
@SuppressWarnings("serial")
public abstract class Frame extends JFrame {

	private JPanel panel = null;

	/**
	 * Create a new basic Frame.
	 *
	 * @param title  the window title.
	 * @param width  the width of the window.
	 * @param height the height of the window.
	 */
	public Frame(String title, int width, int height) {
		init();
		setTitle(title);
		setSize(width, height);
		setLocationRelativeTo(null);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
	}

	/**
	 * Create a new basic Frame that can kill the application when it is closed.
	 *
	 * @param title      the window title.
	 * @param width      the width of the window.
	 * @param height     the height of the window.
	 * @param killOnExit true to terminate all running threads when this window is closed.
	 */
	public Frame(String title, int width, int height, boolean killOnExit) {
		this(title, width, height);
		if(killOnExit) setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void init() {
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(null);
		initElements(panel);
		this.panel = panel;
	}

	/**
	 * Open the frame using the launcher. Call this instead of doing any extra work.
	 */
	public final void open() {
		FrameLauncher.launch(this);
	}

	/**
	 * This is overridden to place elements on the Frame during construction.
	 *
	 * @param panel
	 */
	public abstract void initElements(JPanel panel);

	/**
	 * Get the panel that is being used to place elements on the screen.
	 *
	 * @return
	 */
	public final JPanel getPanel() {
		return panel;
	}

}
