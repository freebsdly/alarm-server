package com.example.alarmserver;

import java.lang.Class;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ConfigurationClassUtils;

/**
 * Bean definitions for {@link DemoApplication}.
 */
public class DemoApplication__TestContext001_BeanDefinitions {
  /**
   * Get the bean definition for 'demoApplication'.
   */
  public static BeanDefinition getDemoApplicationBeanDefinition() {
    Class<?> beanType = DemoApplication.class;
    RootBeanDefinition beanDefinition = new RootBeanDefinition(beanType);
    ConfigurationClassUtils.initializeConfigurationClass(DemoApplication.class);
    beanDefinition.setInstanceSupplier(DemoApplication$$SpringCGLIB$$0::new);
    return beanDefinition;
  }
}
