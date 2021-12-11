package org.simple.spring.util;

/**
 * Class工具类
 * @author zhangkang
 */
public class ClassUtils {

	/**
	 * Class.forName的封装
	 * @param className
	 * @return
	 */
	public static Class<?> forName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("没有找到类" + className, e);
		}
	}
}
