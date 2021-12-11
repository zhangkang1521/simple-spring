package org.simple.spring.beans.factory.xml;


import org.simple.spring.beans.factory.support.DefaultListableBeanFactory;

/**
 * xml形式的Bean工厂
 * @author zhangkang
 */
public class XmlBeanFactory extends DefaultListableBeanFactory {

	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

	public XmlBeanFactory(String resource) {
		// 读取xml，注册BeanDefinition到容器中
		this.reader.loadBeanDefinition(resource);
	}

}
