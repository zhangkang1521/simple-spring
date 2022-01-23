package org.simple.spring.context.support;


import org.simple.spring.beans.factory.xml.XmlBeanDefinitionReader;

public class AbstractRefreshableConfigApplicationContext extends AbstractApplicationContext {

	private String configLocation;

	public AbstractRefreshableConfigApplicationContext(String configLocation) {
		this.configLocation = configLocation;
		this.refresh();
	}

	@Override
	protected void obtainFreshBeanFactory() {
		super.obtainFreshBeanFactory();
		// 读取xml配置，加载beanDefinition
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
		reader.loadBeanDefinition(this.configLocation);
	}
}
