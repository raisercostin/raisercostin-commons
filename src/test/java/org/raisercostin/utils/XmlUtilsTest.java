package org.raisercostin.utils;

import org.junit.Test;

public class XmlUtilsTest {

	@Test
	public void test() {
		String unformattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><QueryMessage\n"
				+ "        xmlns=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message\"\n"
				+ "        xmlns:query=\"http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query\">\n"
				+ "    <Query>\n" + "        <query:CategorySchemeWhere>\n"
				+ "   \t\t\t\t\t         <query:AgencyID>ECB\n\n\n\n</query:AgencyID>\n"
				+ "        </query:CategorySchemeWhere>\n" + "    </Query>\n\n\n\n\n" + "</QueryMessage>";

		System.out.println(XmlUtils.toPrettyString(unformattedXml));
	}
}
