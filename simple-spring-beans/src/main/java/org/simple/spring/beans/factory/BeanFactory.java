package org.simple.spring.beans.factory;

/**
 * Bean工厂
 */
public interface BeanFactory {

	/**
	 * 按名称获取bean
	 * @param name
	 * @return
	 */
	Object getBean(String name);

}
