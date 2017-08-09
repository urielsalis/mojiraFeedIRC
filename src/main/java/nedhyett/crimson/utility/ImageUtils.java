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

import nedhyett.crimson.logging.CrimsonLog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Ned Hyett
 * @deprecated moving to toolbox.
 */
@Deprecated
public class ImageUtils {

	public static BufferedImage fetchFromURL(String url) {
		try {
			return fetchFromURL(new URL(url));
		} catch(MalformedURLException ex) {
			CrimsonLog.warning("Malformed url: %s", url);
			return null;
		}
	}

	public static BufferedImage fetchFromURL(String url, int w, int h) {
		try {
			return fetchFromURL(new URL(url), w, h);
		} catch(MalformedURLException e) {
			return null;
		}
	}

	public static BufferedImage fetchFromURL(URL url, int w, int h) {
		Image i = new ImageIcon(url).getImage();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(i, 0, 0, w, h, null);
		return bi;
	}

	public static BufferedImage getImageFromByteArray(byte[] img) {
		try {
			return ImageIO.read(new ByteArrayInputStream(img));
		} catch(IOException ex) {
			CrimsonLog.warning(ex);
			return null;
		}
	}

	public static BufferedImage fetchFromURL(URL url) {
		Image i = new ImageIcon(url).getImage();
		if(i.getWidth(null) <= 0 || i.getHeight(null) <= 0) {
			CrimsonLog.warning("BAD IMAGE! (%s)", url);
			return null;
		}
		BufferedImage bi = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(i, 0, 0, null);
		return bi;
	}

	public static InputStream putBufferedImageInStream(BufferedImage bi) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, "PNG", baos);
		} catch(IOException e) {
			CrimsonLog.warning(e);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
