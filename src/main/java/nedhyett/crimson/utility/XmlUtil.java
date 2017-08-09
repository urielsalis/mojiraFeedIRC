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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * (Created on 12/06/2014)
 *
 * @author Ned Hyett
 */
public class XmlUtil {

	public static String documentToString(Document d) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch(TransformerConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		try {
			transformer.transform(new DOMSource(d), new StreamResult(writer));
		} catch(TransformerException e) {
			e.printStackTrace();
			return null;
		}
		return writer.getBuffer().toString().replaceAll("\n|\r", "");
	}

	public static Document getDocument(InputStream in) {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
		} catch(SAXException | ParserConfigurationException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> stripTags(String str) throws IOException {
		final ArrayList<String> list = new ArrayList<>();

		ParserDelegator parserDelegator = new ParserDelegator();
		HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
			public void handleText(final char[] data, final int pos) {
				list.add(new String(data));
			}

			public void handleStartTag(HTML.Tag tag, MutableAttributeSet attribute, int pos) {
			}

			public void handleEndTag(HTML.Tag t, final int pos) {
			}

			public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, final int pos) {
			}

			public void handleComment(final char[] data, final int pos) {
			}

			public void handleError(final String errMsg, final int pos) {
			}
		};
		parserDelegator.parse(new InputStreamReader(StreamUtils.putInStream(str.getBytes())), parserCallback, true);
		return list;
	}

}
