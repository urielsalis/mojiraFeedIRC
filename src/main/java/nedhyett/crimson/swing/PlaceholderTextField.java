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
import javax.swing.text.Document;
import java.awt.*;

/**
 * Based on stack overflow question 16213836. Creates a text field that displays placeholder text if it is empty.
 * <p>
 * Use this as if it were any other text field.
 *
 * @author Ned Hyett
 */
public class PlaceholderTextField extends JTextField {

	private String placeholder = "";

	public PlaceholderTextField() {
		super();
	}

	public PlaceholderTextField(Document doc, String text, int cols) {
		super(doc, text, cols);
	}

	public PlaceholderTextField(int cols) {
		super(cols);
	}

	public PlaceholderTextField(String text) {
		super(text);
	}

	public PlaceholderTextField(String text, int cols) {
		super(text, cols);
	}

	/**
	 * Get the placeholder string that is being displayed.
	 *
	 * @return
	 */
	public String getPlaceholder() {
		return placeholder;
	}

	/**
	 * Set the placeholder string to be displayed.
	 *
	 * @param placeholder
	 */
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		if(placeholder.length() == 0 || getText().length() > 0) return;
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(getDisabledTextColor());
		g2d.drawString(placeholder, getInsets().left, graphics.getFontMetrics().getMaxAscent() + getInsets().top);
	}

}
