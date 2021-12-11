package org.simple.spring.beans.factory.config;


import org.simple.spring.beans.PropertyValue;
import org.simple.spring.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * bean定义
 * @author zhangkang
 */
public class BeanDefinition {

	/**
	 * bean的class，存储String或Class
	 */
	private Object beanClass;

	/**
	 * bean的属性
	 */
	private List<PropertyValue> propertyValueList = new ArrayList<>();


	public Class<?> resolveBeanClass()  {
		if (beanClass instanceof Class) {
			return (Class)beanClass;
		}
		return ClassUtils.forName((String)beanClass);
	}

	public Object getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Object beanClass) {
		this.beanClass = beanClass;
	}

	public List<PropertyValue> getPropertyValueList() {
		return propertyValueList;
	}

	public void addProperty(PropertyValue property) {
		propertyValueList.add(property);
	}

}
