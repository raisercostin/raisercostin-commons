package org.raisercostin.util;

import javax.xml.ws.Endpoint;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class RestartableEndpoint<T> {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RestartableEndpoint.class);

	private final String serverId;
	private final String url;
	private final T server;
	private Endpoint endpoint;
	private final Optional<RestartableEndpointObserver> observer;

	public RestartableEndpoint(String serverId, String url, T server, Optional<RestartableEndpointObserver> newObserver) {
		Preconditions.checkNotNull(newObserver);
		this.serverId = serverId;
		this.url = url;
		this.server = server;
		this.observer = newObserver;
		start();
	}

	public void start() {
		try {
			endpoint = Endpoint.publish(url, server);
			LOG.info("starting webservice server[" + serverId + "] on url[" + url + "] done ");
		} catch (Exception e) {
			throw new RuntimeException("Can't start a webservice server on [" + url + "].", e);
		}
	}

	public void shutdownServerAndWait() {
		LOG.info(this + ">  shutdownServerAndWait ...");
		endpoint.stop();
		if (observer.isPresent()) {
			observer.get().notifyStop();
		}
		LOG.info(this + ">  shutdownServerAndWait done.");
	}
	public String getServerId() {
		return serverId;
	}
	public String getUrl() {
		return url;
	}
	public T getServer() {
		return server;
	}

	@Override
	public String toString() {
		return "RestartableEndpoint[" + serverId + "@" + url + "]";
	}
}