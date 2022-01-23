package org.simple.spring.context.support;

public class ClassPathXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

	public ClassPathXmlApplicationContext(String configLocation) {
		super(configLocation);
	}
}
