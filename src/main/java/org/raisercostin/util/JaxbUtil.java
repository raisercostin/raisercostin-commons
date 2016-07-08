package org.raisercostin.util;

import java.io.*;

import javax.xml.bind.*;

public class JaxbUtil {
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Class<T> theClass) {
		try {
			JAXBContext jc2 = JAXBContext.newInstance(theClass);
			Unmarshaller unmarshaller = jc2.createUnmarshaller();
			T actual = (T) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
			return actual;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> String toXml(T data) {
		try {
			JAXBContext jc = JAXBContext.newInstance(data.getClass());
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.marshal(data, out);
			return out.toString("UTF-8");
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
