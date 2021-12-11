package org.simple.spring.beans;

/**
 * bean的属性
 * @author zhangkang
 */
public class PropertyValue {

	/**
	 * 属性名
	 */
	private final String name;

	/**
	 * 属性值，可能是TypedStringValue, RuntimeBeanReference
	 */
	private final Object value;

	public PropertyValue(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
