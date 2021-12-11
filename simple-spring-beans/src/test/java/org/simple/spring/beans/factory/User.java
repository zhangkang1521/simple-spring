package org.simple.spring.beans.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangkang
 * @create 2021/12/11 18:35
 */
public class User implements BeanFactoryAware, InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(User.class);

	private Integer id;
	private String username;
	private Order order;

	private BeanFactory beanFactory;

	public User() {
		log.info("调用了User默认的构造方法");
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	@Override
	public void afterPropertiesSet() {
		log.info("afterPropertiesSet invoked !!!");
	}
}
