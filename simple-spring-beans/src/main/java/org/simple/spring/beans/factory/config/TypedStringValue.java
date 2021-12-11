package org.simple.spring.beans.factory.config;

/**
 * value属性，可能需要转换成Integer,Long等
 *  <property name="id" value="100"/>
 * @author zhangkang
 */
public class TypedStringValue {
	private String value;

	public TypedStringValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
