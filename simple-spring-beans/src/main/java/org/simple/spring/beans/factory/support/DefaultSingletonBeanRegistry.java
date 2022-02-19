package org.simple.spring.beans.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangkang
 * @create 2022/2/19 17:58
 */
public class DefaultSingletonBeanRegistry {

	/** 单例bean */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

	/**
	 * 从缓存中获取
	 * @param name
	 * @return
	 */
	public Object getSingleton(String name) {
		return this.singletonObjects.get(name);
	}

	/**
	 * 加入缓存
	 * @param name
	 * @param bean
	 */
	public void addSingleton(String name, Object bean) {
		this.singletonObjects.put(name, bean);
	}
}
