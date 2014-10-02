package org.raisercostin.util;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class EndpointUtils {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EndpointUtils.class);

	private static final Map<String, RestartableEndpoint<?>> endpoints = Maps.newTreeMap();
	private static final Map<String, RestartableEndpoint<?>> serverIds = Maps.newTreeMap();
	private static final Map<String, RestartableEndpoint<?>> urls = Maps.newTreeMap();

	public static <T> RestartableEndpoint<T> start(String serverId, String url, T server) {
		return start(serverId, url, server, Optional.<RestartableEndpointObserver> absent());
	}
	public static <T> RestartableEndpoint<T> start(String serverId, String url, T server,
			Optional<RestartableEndpointObserver> observer) {
		Preconditions
				.checkArgument(
						!endpoints.containsKey(getKey(serverId, url)),
						"An endpoint with the same id [%s] and url [%s] already exists. There might be collisions. The existing ones on this JVM are: %s",
						serverId, url, endpoints.keySet());
		Preconditions
				.checkArgument(
						!serverIds.containsKey(serverId),
						"An endpoint with the same id [%s] already exists. There might be collisions. The existing ones on this JVM are: %s",
						serverId, serverIds.keySet());
		Preconditions
				.checkArgument(
						!urls.containsKey(url),
						"An endpoint with the same urls [%s] already exists. There might be collisions. The existing ones on this JVM are: %s",
						url, urls.keySet());
		RestartableEndpoint<T> endpoint = new RestartableEndpoint<T>(serverId, url, server, observer);
		endpoints.put(getKey(serverId, url), endpoint);
		serverIds.put(serverId, endpoint);
		urls.put(url, endpoint);
		return endpoint;
	}

	private static String getKey(String serverId, String url) {
		return serverId + "@" + url;
	}

	public static void shutdownAll() {
		for (RestartableEndpoint<?> server : endpoints.values()) {
			LOG.info("> " + server + " shutdownServerAndWait ...");
			server.shutdownServerAndWait();
			LOG.info("> " + server + " shutdownServerAndWait done.");
		}
		endpoints.clear();
		serverIds.clear();
		urls.clear();
	}

	public static void start() {
		for (RestartableEndpoint<?> server : endpoints.values()) {
			LOG.info("> " + server + " start ...");
			server.start();
			LOG.info("> " + server + " start done.");
		}
		endpoints.clear();
	}

	public static void stopById(String serverId) {
		RestartableEndpoint<?> server = serverIds.remove(serverId);
		Preconditions.checkNotNull(server, "A server for id [%s] wasn't found in the list of %s", serverId,
				serverIds.keySet());
		endpoints.remove(getKey(serverId, server.getUrl()));
		urls.remove(server.getUrl());
		server.shutdownServerAndWait();
	}
}
