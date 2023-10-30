package org.raisercostin.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.raisercostin.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringValueResolver;

/**
 * Extends PropertyPlaceholderConfigurer to expose the properties the configurer will use.
 */
public class AdapterPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean,
    BeanFactoryAware {
  private static final String SEARCHED_RESOURCE_PREFIX = "searched-resource:";

  /**
   * Logger for this class
   */
  private static final Logger LOG = LoggerFactory.getLogger(AdapterPropertyPlaceholderConfigurer.class);

  private Properties properties;

  private Resource[] myLocations;

  private String buildDatePropertyName;

  private boolean myIgnoreResourceNotFoundProperty;

  private BeanFactory myBeanFactory;

  private Map<String, String> propertiesMap;
  // Default as in PropertyPlaceholderConfigurer
  private int mySystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

  private String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

  private String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

  private String valueSeparator = DEFAULT_VALUE_SEPARATOR;

  private boolean ignoreUnresolvablePlaceholders = false;
  private String nullValue;

  @Override
  public void setSystemPropertiesMode(int systemPropertiesMode) {
    super.setSystemPropertiesMode(systemPropertiesMode);
    mySystemPropertiesMode = systemPropertiesMode;
  }

  @Override
  @Autowired(required = true)
  public void setLocations(Resource[] locations) {
    super.setLocations(locations);
    this.myLocations = locations;
  }

  @Override
  public void afterPropertiesSet() {
  }

  private void beforeAfterPropertiesSet() {
    List<Resource> findResources = new ArrayList<>();
    for (Resource resource : myLocations) {
      Resource resolvedResource = resource;
      String name = resource.getFilename();
      if (name.startsWith(SEARCHED_RESOURCE_PREFIX)) {
        String withoutProtocol = name.substring(SEARCHED_RESOURCE_PREFIX.length());
        int index = withoutProtocol.indexOf(":");
        if (index == 0) {
          throw new RuntimeException(
            "The resource should have the format: [" + SEARCHED_RESOURCE_PREFIX
                + "<resourceFinderBeanId>:<fileName>] and is defined as ["
                + name + "].");
        }
        if (index < 0) {
          throw new RuntimeException(
            "The resource should have the format: [" + SEARCHED_RESOURCE_PREFIX
                + "<resourceFinderBeanId>:<fileName>] and is defined as ["
                + name + "].");
        }
        String resourceFinderBeanId = withoutProtocol.substring(0, index);
        String resourceName = withoutProtocol.substring(index + 1);
        ResourceFinder rf = (ResourceFinder) myBeanFactory.getBean(resourceFinderBeanId);
        resolvedResource = rf.findResource(resourceName, myIgnoreResourceNotFoundProperty);
      }
      findResources.add(resolvedResource);
    }
    setLocations(findResources.toArray(new Resource[0]));
  }

  @Override
  protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
      throws BeansException {
    super.processProperties(beanFactory, props);
    StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
    propertiesMap = new HashMap<>();
    for (Map.Entry<Object, Object> entry : props.entrySet()) {
      String keyStr = entry.getKey().toString();
      String oldValue = entry.getValue().toString();
      String valueStr = valueResolver.resolveStringValue(oldValue);
      propertiesMap.put(keyStr, valueStr);
    }
    LOG.info("Properties:" + ObjectUtils.toString(propertiesMap));
  }

  @Override
  public void setPlaceholderPrefix(String placeholderPrefix) {
    super.setPlaceholderPrefix(placeholderPrefix);
    this.placeholderPrefix = placeholderPrefix;
  }

  @Override
  public void setPlaceholderSuffix(String placeholderSuffix) {
    super.setPlaceholderSuffix(placeholderSuffix);
    this.placeholderSuffix = placeholderSuffix;
  }

  @Override
  public void setValueSeparator(String valueSeparator) {
    super.setValueSeparator(valueSeparator);
    this.valueSeparator = valueSeparator;
  }

  @Override
  public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
    super.setIgnoreUnresolvablePlaceholders(ignoreUnresolvablePlaceholders);
    this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
  }

  @Override
  public void setNullValue(String nullValue) {
    super.setNullValue(nullValue);
    this.nullValue = nullValue;
  }

  private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

    private final PropertyPlaceholderHelper helper;

    private final PlaceholderResolver resolver;

    public PlaceholderResolvingStringValueResolver(Properties props) {
      this.helper = new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, valueSeparator,
        ignoreUnresolvablePlaceholders);
      this.resolver = new PropertyPlaceholderConfigurerResolver(props);
    }

    @Override
    public String resolveStringValue(String strVal) throws BeansException {
      String value = this.helper.replacePlaceholders(strVal, this.resolver);
      return (value.equals(nullValue) ? null : value);
    }
  }

  private class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

    private final Properties props;

    private PropertyPlaceholderConfigurerResolver(Properties props) {
      this.props = props;
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
      return AdapterPropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, props,
        mySystemPropertiesMode);
    }
  }

  public String getProperty(String name) {
    return propertiesMap.get(name).toString();
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    super.setBeanFactory(beanFactory);
    this.myBeanFactory = beanFactory;
  }

  @Override
  public void setLocation(Resource location) {
    super.setLocation(location);
  }

  @Override
  public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
    super.setIgnoreResourceNotFound(ignoreResourceNotFound);
    this.myIgnoreResourceNotFoundProperty = ignoreResourceNotFound;
  }

  @Autowired(required = true)
  public void setBuildDatePropertyName(String buildDatePropertyName) {
    this.buildDatePropertyName = buildDatePropertyName;
  }

  @Override
  public void postProcessBeanFactory(
      ConfigurableListableBeanFactory beanFactory) throws BeansException {
    beforeAfterPropertiesSet();
    super.postProcessBeanFactory(beanFactory);
  }
}
