package org.simple.spring.beans.factory.support;

import org.simple.spring.beans.factory.config.BeanDefinition;

/**
 * @author zhangkang
 * @create 2022/2/19 18:03
 */
public interface BeanDefinitionRegistry {

	/**
	 * 注册BeanDefinition
	 * @param beanName
	 * @param beanDefinition
	 */
	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
