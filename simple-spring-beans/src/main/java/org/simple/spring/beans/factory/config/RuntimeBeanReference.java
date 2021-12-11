package org.simple.spring.beans.factory.config;

/**
 * bean的ref属性
 *  <property name="order" ref="order"/>
 * @author zhangkang
 */
public class RuntimeBeanReference {
	private String beanName;

	public RuntimeBeanReference(String beanName) {
		this.beanName = beanName;
	}

	public String getBeanName() {
		return beanName;
	}


}
