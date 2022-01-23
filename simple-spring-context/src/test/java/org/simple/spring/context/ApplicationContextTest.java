package org.simple.spring.context;

import org.junit.Test;
import org.simple.spring.context.service.UserService;
import org.simple.spring.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * @author zhangkang
 * @create 2022/1/23 14:56
 */
public class ApplicationContextTest {

	@Test
	public void testXmlApplicationContext() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-1.xml");
		UserService userService = (UserService)applicationContext.getBean("userService");
		userService.sayHello("zk");
	}

	@Test
	public void testBeanPostProcessor() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-BeanPostProcessor.xml");
		UserService userService = (UserService)applicationContext.getBean("userService");
		userService.sayHello("zk");
	}

}