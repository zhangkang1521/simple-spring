package org.simple.spring.beans.factory.config;

public interface BeanPostProcessor {

	/**
	 * bean初始化前调用
	 * @param bean
	 * @param beanName
	 * @return 如果返回空，不继续调用后续的BeanPostProcessor，并返回空对象
	 */
	Object postProcessBeforeInitialization(Object bean, String beanName);

	/**
	 * bean初始化后调用
	 * <p>应用：aop创建代理</p>
	 * @param bean
	 * @param beanName
	 * @return 如果返回空，不继续调用后续的BeanPostProcessor，并返回空对象
	 */
	Object postProcessAfterInitialization(Object bean, String beanName);
}
