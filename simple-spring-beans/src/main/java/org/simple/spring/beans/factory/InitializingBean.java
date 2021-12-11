package org.simple.spring.beans.factory;

/**
 * Bean初始化
 * @author zhangkang
 */
public interface InitializingBean {

	/**
	 * 依赖注入之后，bean初始化时调用
	 */
	void afterPropertiesSet();
}
