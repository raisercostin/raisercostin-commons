package org.raisercostin.util;

import org.raisercostin.utils.ObjectUtils;
import org.springframework.util.SystemPropertyUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class PropertyUtils {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyUtils.class);

	public static String getInterpretedText(String string) {
		return SystemPropertyUtils.resolvePlaceholders(string);
	}	

	@SuppressWarnings("unchecked")
	public static <T> T configure(String environmentProperty, T defaultValue, Function<String, T> builder, T... values) {
		String value = System.getProperty(environmentProperty);
		String resolvedValue = null;
		if (value != null) {
			resolvedValue = getInterpretedText(value);
		}
		T resultValue = defaultValue;
		if (resolvedValue != null) {
			resultValue = builder.apply(resolvedValue);
		}
		LOG.info("Property "
				+ environmentProperty
				+ "="
				+ resultValue
				+ " ("
				+ "defaultValue=["
				+ defaultValue
				+ "], rawValue=["
				+ value
				+ "], resolvedValue=["
				+ resolvedValue
				+ "]).\n"
				+ "Possible values are:"
				+ Lists.newArrayList(values)
				+ ".\n"
				+ "You should use one when:"
				+ Lists.transform(Lists.newArrayList(values), toString2())
				+ ".\n"
				+ "Can be configured by adding -D"
				+ environmentProperty
				+ "=<value> to java arguments or by configuring an environment variable. Can start with [classpath:], [file:///] like in [file:///C:/file.config] (notice three slashes). Placeholders like ${user.home} from both java environment or operating system environment can be used and are resolved. See @org.springframework.util.SystemPropertyUtils.resolvePlaceholders for more details about how placeholders are resolved and @see org.springframework.core.io.support.PathMatchingResourcePatternResolver.getResource about how urls to resource can be defined.\n");
		return resultValue;
	}

	private static <T> Function<T, String> toString2() {
		return new Function<T, String>() {
			@Override
			public String apply(T value) {
				return ObjectUtils.toString(value, true, false, false, "") + "\n";
			}
		};
	}
}
