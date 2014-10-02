package org.raisercostin.config;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ResourceFinder implements InitializingBean {

	private static final Pattern property = Pattern.compile(".*\\$\\{([^}]*)\\}.*");

	protected final Logger LOG = LoggerFactory.getLogger(ResourceFinder.class);

	private String[] profilesLocations;
	private String appPath;
	private String profile;
	private String hostname;
	private String username;

	private String profilesLocationsPropertyName;
	private String profilePropertyName;
	private final String hostNamePropertyName = "HOSTNAME";

	public ResourceFinder() {
	}

	public ResourceFinder(String appPath, String profilePropertyName, String... profilesLocations) throws Exception {
		this.profilesLocations = profilesLocations;
		this.profilePropertyName = profilePropertyName;
		this.appPath = appPath;
		Assert.hasText(appPath);
		afterPropertiesSet();
		Assert.hasText(hostname);
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public void setProfilePropertyName(String profilePropertyName) {
		this.profilePropertyName = profilePropertyName;
	}

	public void setProfilesLocations(String[] profilesLocations) {
		this.profilesLocations = profilesLocations;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public void setProfilesLocationsPropertyName(String profilesLocationsPropertyName) {
		this.profilesLocationsPropertyName = profilesLocationsPropertyName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (profile == null && profilePropertyName != null) {
			this.profile = getProperty(profilePropertyName);
		}
		if (hostname == null) {
			this.hostname = computeHostName(hostNamePropertyName);
		}
		if (username == null) {
			this.username = getProperty("user.name");
		}
		if (profilesLocations == null && profilesLocationsPropertyName != null) {
			this.profilesLocations = getProperty(profilesLocationsPropertyName).split("[,]");
		}
		for (int i = 0; i < profilesLocations.length; i++) {
			profilesLocations[i] = replacePlaceholders(profilesLocations[i]);
		}
	}

	public Resource findResource(String resourceName, boolean ignoreResourceNotFoundProperty) {
		List<String> locations = getLocations(resourceName);
		if (!resourceName.startsWith("classpath:")) {
			if (resourceName.startsWith("optional:")) {
				resourceName = resourceName.substring("optional:".length());
				ignoreResourceNotFoundProperty = true;
			} else if (resourceName.startsWith("mandatory:")) {
				resourceName = resourceName.substring("mandatory:".length());
				ignoreResourceNotFoundProperty = false;
			}
		}

		return getFirstResource(resourceName, locations, ignoreResourceNotFoundProperty);
	}

	private String replacePlaceholders(String expresion) {
		Matcher matcher = property.matcher(expresion);
		String result = expresion;
		while (matcher.find()) {
			String propertyName = matcher.group(1);
			result = result.replace("${" + propertyName + "}", getProperty(propertyName));
		}
		if (result.startsWith(".")) {
			result = "file:///" + new File(".").getAbsolutePath() + result.substring(1);
		}
		result = result.replace("\\", "/");
		return result;
	}

	private List<String> getLocations(String[] profilesLocations, String appPath, String profile, String hostname,
			String fileName) {
		Assert.hasText(fileName);
		List<String> result = new ArrayList<String>();
		for (String profileLocation : profilesLocations) {
			addToLocation(result, profileLocation, appPath, profile, hostname, fileName);
		}
		return result;
	}

	private void addToLocation(List<String> result, String profilesLocation, String appPath, String profile,
			String hostname, String fileName) {
		boolean hasProfile = StringUtils.hasText(profile);
		if (hasProfile) {
			add(result,
					concat(profilesLocation, "/", appPath, "-", profile, "-", hostname, "-", username, "/", fileName));
			add(result, concat(profilesLocation, "/", appPath, "-", profile, "-", hostname, "/", fileName));
			add(result, concat(profilesLocation, "/", appPath, "-", profile, "-", username, "/", fileName));
			add(result, concat(profilesLocation, "/", appPath, "-", profile, "/", fileName));
			add(result, concat(profilesLocation, "/", profile, "-", hostname, "-", username, "/", fileName));
			add(result, concat(profilesLocation, "/", profile, "-", hostname, "/", fileName));
			add(result, concat(profilesLocation, "/", profile, "-", username, "/", fileName));
			add(result, concat(profilesLocation, "/", profile, "/", fileName));
		}
		add(result, concat(profilesLocation, "/", appPath, "-", hostname, "-", username, "/", fileName));
		add(result, concat(profilesLocation, "/", appPath, "-", hostname, "/", fileName));
		add(result, concat(profilesLocation, "/", appPath, "-", username, "/", fileName));
		add(result, concat(profilesLocation, "/", appPath, "/", fileName));
		add(result, concat(profilesLocation, "/", hostname, "-", username, "/", fileName));
		add(result, concat(profilesLocation, "/", hostname, "/", fileName));
		add(result, concat(profilesLocation, "/", username, "/", fileName));
		add(result, concat(profilesLocation, "/", fileName));
	}

	private void add(List<String> result, String location) {
		result.add(location);
	}

	private String concat(String... values) {
		String val1 = values[0];
		for (int i = 1, maxi = values.length; i < maxi - 1; i += 2) {
			String operator = values[i];
			String val2 = values[i + 1];
			val1 = concatenateTwoStrings(val1, operator, val2);
		}
		return val1;
	}

	private String concatenateTwoStrings(String val1, String operator, String val2) {
		if (!StringUtils.hasText(val1) && !StringUtils.hasText(val2)) {
			return "";
		}
		if (!StringUtils.hasText(val1)) {
			return val2;
		}
		if (!StringUtils.hasText(val2)) {
			return val1;
		}
		return val1 + operator + val2;
	}

	private Resource getFirstResource(String resourceDescription, List<String> locations,
			boolean ignoreResourceNotFoundProperty) {
		Resource result = null;
		try {
			try {
				ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
				for (String location : locations) {
					Resource[] resources = resourceResolver.getResources(location);
					if ((resources.length == 1) && resources[0].exists()) {
						result = resources[0];
						return result;
					} else if (resources.length > 1) {
						throw new IllegalStateException(getStatusDescription("Too many resource files found for",
								resourceDescription, locations) + "': " + Arrays.toString(resources));
					}
				}
				if (!ignoreResourceNotFoundProperty) {
					throw new IllegalStateException(
							getStatusDescription("Couldn't find a configuration file for", resourceDescription,
									locations)
									+ ".\n\tYou could pass -D"
									+ profilePropertyName
									+ "=<profile name> as a java parameter or configure system environment properties.\n\tClasspath used was: \n\t\t"
									+ getClasspath("\n\t\t"));
				}
				return null;
			} catch (IOException e) {
				throw new IllegalStateException(getStatusDescription(
						"An error occurred trying to find a configuration files found for", resourceDescription,
						locations), e);
			} finally {
				if (LOG.isInfoEnabled()) {
					LOG.info(getStatusDescription("Found [" + toString(result) + "]\n\t when searching for "
							+ (ignoreResourceNotFoundProperty ? "optional" : "mandatory"), resourceDescription,
							locations));
				}
			}
		} catch (IllegalStateException e) {
			if (ignoreResourceNotFoundProperty) {
				LOG.warn(e.getMessage());
				return null;
			} else {
				throw e;
			}
		}
	}

	private String getClasspath(String delimitator) {
		// Get the System Classloader
		ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
		// Get the URLs
		URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < urls.length; i++) {
			sb.append(urls[i].getFile()).append(delimitator);
		}
		return sb.toString();
	}

	private String toString(Resource result) {
		if (result == null) {
			return null;
		}
		try {
			return result.getURL().toString();
		} catch (IOException e) {
			return result.toString();
		}
	}

	private String getStatusDescription(String message, String resource, List<String> locations) {
		return String
		.format("%s\n\t\tresource=[%s],\n\t\tprofilesLocation=[\n\t\t\t%s],\n\t\tappPath=[%s],\n\t\tprofilePropertyName=[%s],\n\t\tprofile=[%s],\n\t\thostname=[%s],\n\t\tusername=[%s]."
				+ "\n\t The investigated locations were \n\t\t%s according with the format profilesLocations/appPath-hostname-profile/resource.",
				message, resource, StringUtils.arrayToDelimitedString(profilesLocations, "\n\t\t\t"), appPath,
				profilePropertyName, profile, hostname, username,
				StringUtils.collectionToDelimitedString(locations, "\n\t\t"));
	}

	List<String> getLocations(String resourceName) {
		return getLocations(profilesLocations, appPath, profile, hostname, resourceName);
	}

	private String escape(String appPath, String prefix, String sufix) {
		if (appPath == null) {
			return "";
		}
		return prefix + appPath + sufix;
	}

	private String computeHostName(String hostNamePropertyName) {
		String hostName = null;
		if (hostNamePropertyName != null) {
			hostName = getProperty(hostNamePropertyName);
			if (hostName != null) {
				hostName = hostName.trim();
			}
		}
		if (hostName == null) {
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				// this should never happen for localhost, but if it does we end
				// up returning null
				LOG.warn("InetAddress.getLocalHost().getHostName() raised an exception.", e);
			}
		}
		return hostName;
	}

	private String getProperty(String property) {
		String value = System.getProperty(property);
		if (value == null) {
			value = System.getenv(property);
		}
		return value;
	}

	void setHostname(String hostname) {
		this.hostname = hostname;
	}

	void setUsername(String username) {
		this.username = username;
	}
}
