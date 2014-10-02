package org.raisercostin.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.Endpoint;

import org.raisercostin.utils.ObjectUtils;

public class WebServiceFactory {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WebServiceFactory.class);
	public static <T> T createGeneric(String url, Class<T> theInterface, String serviceName, String endpointInterface) {
		LOG.info("starting " + theInterface + " client to url [" + url + "] ...");
		javax.xml.namespace.QName qname = new javax.xml.namespace.QName(endpointInterface, serviceName);
		try {
			javax.xml.ws.Service service = javax.xml.ws.Service.create(new URL(url), qname);
			T client = service.getPort(theInterface);
			client = createClientWithTimeouts(client);
			return client;
			// } catch (InaccessibleWSDLException e) {
			// for (Throwable ex : e.getErrors()) {
			// try {
			// throw ex;
			// } catch (java.io.IOException e2) {
			// if (e2.getMessage().contains("Got Server returned HTTP response code: 407 for URL")) {
			// throw new RuntimeException("The server got response code 407 while trying to access " + url
			// + ". See [https://www.google.com/search?q=HTTP+response+code+" + 407 + "] for a full description.");
			// }
			// } catch (Throwable e2) {
			// throw e;
			// }
			// }
			// throw e;
		} catch (Exception e) {
			String error = ObjectUtils.toStringDump(e);
			if (error.contains("Got Server returned HTTP response code: 407 for URL")) {
				throw new RuntimeException("The server got response code 407 while trying to access " + url
						+ ". See [https://www.google.com/search?q=HTTP+response+code+" + 407
						+ "] for a full description.");
			}
			throw new RuntimeException("Can't create a client for " + theInterface + " client to url [" + url + "]."
					+ error, e);
		}
	}

	// TODO - Costin we should abstract this because now I introuded duplicate code with
	// WebServiceJava6StandardObjectConnector
	private static <T> T createClientWithTimeouts(T client) throws MalformedURLException {
		NetworkUtils.configureWebServiceClientCapabilities(client);
		return client;
	}
	public static <T> Endpoint createGenericServer(Object serverImplementation, String url) {
		LOG.info("starting " + serverImplementation + " server on url [" + url + "] ...");
		// OtherUtils.addAnnotation(serverImplementation.getClass(), WebService.class);
		Endpoint server = Endpoint.publish(url, serverImplementation);
		return server;
	}
}
