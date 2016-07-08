package org.raisercostin.config;

import java.io.*;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Pretty-prints xml, supplied as a string.
 * <p/>
 * eg. <code>
 * String formattedXml = new XmlFormatter().format("<tag><nested>hello</nested></tag>");
 * </code>
 */
public class XmlFormatter {

	public XmlFormatter() {
	}

	public static String format(String unformattedXml) {
		try {
			final Document document = parseXmlFile(unformattedXml);

			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(2);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);

			return out.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Document parseXmlFile(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		String unformattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><QueryMessage\n"
				+ "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
				+ "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
				+ "    <Query>\n" + "        <query:CategorySchemeWhere>\n"
				+ "   \t\t\t\t\t         <query:AgencyID>ECB\n\n\n\n</query:AgencyID>\n"
				+ "        </query:CategorySchemeWhere>\n" + "    </Query>\n\n\n\n\n" + "</QueryMessage>";

		System.out.println(format(unformattedXml));
	}

}