package org.simple.spring.beans.factory;

import org.junit.Assert;
import org.junit.Test;
import org.simple.spring.beans.factory.xml.XmlBeanFactory;

import static org.junit.Assert.*;

/**
 * @author zhangkang
 * @create 2021/12/11 17:25
 */
public class BeanFactoryTest {

	@Test
	public void getBean() {
		// 验证getBean
		BeanFactory beanFactory = new XmlBeanFactory("beanFactory-1.xml");
		User user = (User)beanFactory.getBean("user");
		Assert.assertEquals(100, (int)user.getId());
		Assert.assertEquals("zk", user.getUsername());
	}

	@Test
	public void getBeanSingleton() {
		// 验证是单例bean
		BeanFactory beanFactory = new XmlBeanFactory("beanFactory-1.xml");
		User user1 = (User)beanFactory.getBean("user");
		User user2 = (User)beanFactory.getBean("user");
		Assert.assertTrue(user1 == user2);
	}

	@Test
	public void getBeanRef() {
		// 验证依赖另外一个bean
		BeanFactory beanFactory = new XmlBeanFactory("beanFactory-2.xml");
		User user = (User)beanFactory.getBean("user");
		Assert.assertNotNull(user.getOrder());
		Assert.assertEquals(200, (int)user.getOrder().getId());
	}
}