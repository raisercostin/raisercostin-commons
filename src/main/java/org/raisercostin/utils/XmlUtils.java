package org.raisercostin.utils;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.base.Splitter;

public class XmlUtils {

	public static String normXml(String content) {
		return normAll(toPrettyString(content));
	}

	public static String normAll(String content) {
		String result = norm(content);
		result = normalizeAttributes(result);
		return result;
	}

	//one attribute per line
	private static String normalizeAttributes(String content) {
		String IS_ATTRIBUTE = "->>>";
		content = content.replaceAll("([^ =]+=\"[^\"]*\")", "\n" + IS_ATTRIBUTE + "$1");
		Splitter splitter = Splitter.on("\n").omitEmptyStrings();
		String lastPrefix = "";
		boolean lastLineIsAttribute = false;
		StringBuilder newContent = new StringBuilder();
		for (String line : splitter.split(content)) {
			String newLine = line;
			if (line.startsWith(IS_ATTRIBUTE)) {
				if (lastLineIsAttribute) {
					newLine = lastPrefix + line.substring(IS_ATTRIBUTE.length());
				} else {
					newLine = lastPrefix + "    " + line.substring(IS_ATTRIBUTE.length());
				}
				lastLineIsAttribute = true;
			} else {
				lastLineIsAttribute = false;
			}
			newContent.append(newLine).append("\n");
			int spacePrefix = findSpacePrefix(newLine);
			lastPrefix = newLine.substring(0, spacePrefix);
		}
		return newContent.toString();
	}

	private static int findSpacePrefix(String line) {
		for (int i = 0; i < line.length(); i++)
			if (line.charAt(i) != ' ')
				return i;
		return 0;
	}

	private static String normEol(String content) {
		String result = content;
		// result = result.replaceAll("(\r\n)|(\n)", "\n");
		// result = result.replaceAll("\r", "\n");
		result = result.replaceAll("(\\r\\n)", "\n");
		result = result.replaceAll("\\r", "\r");
		result = result.replaceAll("\\n", "\n");
		return result;
	}

	private static String normTabs(String content, String oneTab) {
		String result = content;
		result = result.replaceAll("\\t", oneTab);
		return result;
	}

	public static String norm(String content) {
		String result = normEol(content);
		result = normTabs(result, "    ");
		return result;
	}

	public static String toPrettyString(String xml) {
		int indent = 4;
		try {
			// Turn xml string into a document
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

			// Remove whitespaces outside tags
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document,
					XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				node.getParentNode().removeChild(node);
			}

			// Setup pretty print options
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");

			// Return pretty print xml string
			StringWriter stringWriter = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
			return stringWriter.toString();
		} catch (Exception e) {
			System.out.println(xml);
			throw new RuntimeException("Problems prettyfing xml content ["+Splitter.fixedLength(100).split(xml)+"]",e);
		}
	}
}
