package org.simple.spring.context.service;

import org.simple.spring.beans.factory.config.BeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zhangkang
 * @create 2022/1/23 15:51
 */
public class DemoBeanPostProcessor implements BeanPostProcessor {

	private static Logger log = LoggerFactory.getLogger(DemoBeanPostProcessor.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		log.info(">>> bean 后置处理，将实际bean换成代理对象");

		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{UserService.class}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				log.info(">>> 代理方法调用 {}", method.getName());
				return method.invoke(bean, args);
			}
		});
	}
}
