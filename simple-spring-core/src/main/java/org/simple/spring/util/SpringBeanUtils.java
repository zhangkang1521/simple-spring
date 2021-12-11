package org.simple.spring.util;

/**
 * SpringBean工具类
 * @author zhangkang
 */
public class SpringBeanUtils {

	/**
	 * 使用反射创建bean
	 * @param clz
	 * @return
	 */
	public static Object instantiateClass(Class<?> clz) {
		try {
			return clz.newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException("实例化类异常" + clz, e);
		}
	}
}
