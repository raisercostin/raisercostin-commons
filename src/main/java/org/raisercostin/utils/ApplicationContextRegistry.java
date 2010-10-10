package org.raisercostin.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextRegistry implements ApplicationContextAware {
	private static BeanFactory beanFactory;

	public static void setBeanFactory(BeanFactory beanFactory) {
		ApplicationContextRegistry.beanFactory = beanFactory;
	}

	public static BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		setBeanFactory(applicationContext);
	}
}
