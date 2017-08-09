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

package nedhyett.crimson.toolbox;

import nedhyett.crimson.logging.CrimsonLog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Wraps an image and allows advanced operations to be applied to it.
 *
 * @author Ned Hyett
 */
public class ImageToolbox {

	private Image image = null;
	private int width = 1;
	private int height = 1;
	private int type = BufferedImage.TYPE_INT_RGB;
	private boolean metaDirty = false;
	private Graphics graphics = null;

	public ImageToolbox() {
		this(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
	}

	public ImageToolbox(Image image) {
		this.image = image;
	}

	public ImageToolbox rebuildImage() {
		if(metaDirty) image = new BufferedImage(width, height, type);
		return this;
	}

	private void updateImageMeta() {
		width = image.getWidth(null);
		height = image.getHeight(null);
		type = image instanceof BufferedImage ? ((BufferedImage) image).getType() : -1;
	}

	public int getWidth() {
		return width;
	}

	public ImageToolbox setWidth(int width) {
		this.width = width;
		metaDirty = true;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public ImageToolbox setHeight(int height) {
		this.height = height;
		metaDirty = true;
		return this;
	}

	public int getType() {
		return type;
	}

	public ImageToolbox setType(int type) {
		this.type = type;
		metaDirty = true;
		return this;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
		updateImageMeta();
	}

	public ImageToolbox fetchFromURL(String url) {
		try {
			return fetchFromURL(new URL(url));
		} catch(MalformedURLException ignored) {

		}
		return this;
	}

	public ImageToolbox fetchFromURL(String url, int w, int h) {
		try {
			return fetchFromURL(new URL(url), w, h);
		} catch(MalformedURLException ignored) {

		}
		updateImageMeta();
		return this;
	}

	public ImageToolbox fetchFromURL(URL url, int w, int h) {
		Image i = new ImageIcon(url).getImage();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(i, 0, 0, w, h, null);
		image = bi;
		updateImageMeta();
		return this;
	}

	public ImageToolbox getImageFromByteArray(byte[] img) {
		try {
			image = ImageIO.read(new ByteArrayInputStream(img));
		} catch(IOException ex) {
			CrimsonLog.warning(ex);
		}
		updateImageMeta();
		return this;
	}

	public byte[] getBytes(String format) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write((RenderedImage) image, format, baos);
		} catch(IOException e) {
			CrimsonLog.warning(e);
		}
		return baos.toByteArray();
	}

	public ImageToolbox fetchFromURL(URL url) {
		Image i = new ImageIcon(url).getImage();
		if(i.getWidth(null) <= 0 || i.getHeight(null) <= 0) {
			CrimsonLog.warning("BAD IMAGE! (%s)", url);
			return null;
		}
		BufferedImage bi = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(i, 0, 0, null);
		image = bi;
		updateImageMeta();
		return this;
	}

	public InputStream putImageInStream(String format) {
		if(!(image instanceof RenderedImage))
			throw new IllegalStateException("Cannot put non-rendered image in stream!");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write((RenderedImage) image, format, baos);
		} catch(IOException e) {
			CrimsonLog.warning(e);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}

	public Graphics getGraphics() {
		if(graphics != null) return graphics;
		graphics = image.getGraphics();
		return graphics;
	}

	public void destroyGraphics() {
		if(graphics != null) graphics.dispose();
	}

	public void drawString(String text, int x, int y) {
		getGraphics().drawString(text, x, y);
	}

	public void clearImage() {
		getGraphics().clearRect(0, 0, getWidth(), getHeight());
	}

}
