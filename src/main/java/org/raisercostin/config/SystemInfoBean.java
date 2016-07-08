package org.raisercostin.config;

import org.raisercostin.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemInfoBean {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SystemInfoBean.class);

	public SystemInfoBean() {
		LOG.info("System Environment:" + ObjectUtils.toString(System.getenv()));
		LOG.info("System Properties:" + ObjectUtils.toString(System.getProperties()));
	}
}
