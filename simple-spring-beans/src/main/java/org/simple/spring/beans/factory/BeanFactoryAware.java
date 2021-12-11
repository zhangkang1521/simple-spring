package org.simple.spring.beans.factory;

/**
 * Bean工厂感知
 * @author zhangkang
 */
public interface BeanFactoryAware {

	/**
	 * 初始化时调用
	 * @param beanFactory
	 */
	void setBeanFactory(BeanFactory beanFactory);
}
