package com.example.alarmserver.services;

import java.lang.Class;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CallService}.
 */
public class CallService__BeanDefinitions {
  /**
   * Get the bean definition for 'callService'.
   */
  public static BeanDefinition getCallServiceBeanDefinition() {
    Class<?> beanType = CallService.class;
    RootBeanDefinition beanDefinition = new RootBeanDefinition(beanType);
    beanDefinition.setInstanceSupplier(CallService::new);
    return beanDefinition;
  }
}
