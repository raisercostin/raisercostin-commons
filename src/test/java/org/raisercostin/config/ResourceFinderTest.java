package org.raisercostin.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.util.StringUtils;

public class ResourceFinderTest {

	@Test
	public void testFindResource() throws Exception {
		System.setProperty("TEST_PROFILE", "prof");
		ResourceFinder resourceFinder = new ResourceFinder("app", "TEST_PROFILE", "c:\\home", "classpath:");
		resourceFinder.setHostname("host");
		resourceFinder.setUsername("user");
		List<String> locations = resourceFinder.getLocations("config/res1.txt");
		String[] expected = { "c:/home/app-prof-host-user/config/res1.txt", "c:/home/app-prof-host/config/res1.txt",
				"c:/home/app-prof-user/config/res1.txt",
				"c:/home/app-prof/config/res1.txt",
				"c:/home/prof-host-user/config/res1.txt",
				"c:/home/prof-host/config/res1.txt",
				"c:/home/prof-user/config/res1.txt",
				"c:/home/prof/config/res1.txt",
				//
				"c:/home/app-host-user/config/res1.txt", "c:/home/app-host/config/res1.txt",
				"c:/home/app-user/config/res1.txt", "c:/home/app/config/res1.txt", "c:/home/host-user/config/res1.txt",
				"c:/home/host/config/res1.txt", "c:/home/user/config/res1.txt",
				"c:/home/config/res1.txt",
				//
				"classpath:/app-prof-host-user/config/res1.txt", "classpath:/app-prof-host/config/res1.txt",
				"classpath:/app-prof-user/config/res1.txt", "classpath:/app-prof/config/res1.txt",
				"classpath:/prof-host-user/config/res1.txt", "classpath:/prof-host/config/res1.txt",
				"classpath:/prof-user/config/res1.txt", "classpath:/prof/config/res1.txt",
				//
				"classpath:/app-host-user/config/res1.txt", "classpath:/app-host/config/res1.txt",
				"classpath:/app-user/config/res1.txt", "classpath:/app/config/res1.txt",
				"classpath:/host-user/config/res1.txt", "classpath:/host/config/res1.txt",
				"classpath:/user/config/res1.txt", "classpath:/config/res1.txt" };
		assertEquals(StringUtils.arrayToDelimitedString(expected, "\n"),
				StringUtils.collectionToDelimitedString(locations, "\n"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindResource2() throws Exception {
		ResourceFinder resourceFinder = new ResourceFinder(null, "EESSI_PROFILE", "c:\\home");
		assertNotNull(resourceFinder);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindResource3() throws Exception {
		ResourceFinder resourceFinder = new ResourceFinder("", "EESSI_PROFILE", "c:\\home");
		assertNotNull(resourceFinder);
	}

}
