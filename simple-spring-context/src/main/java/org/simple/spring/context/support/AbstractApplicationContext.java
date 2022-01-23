package org.simple.spring.context.support;

import org.simple.spring.beans.factory.config.BeanPostProcessor;
import org.simple.spring.beans.factory.support.DefaultListableBeanFactory;
import org.simple.spring.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractApplicationContext implements ApplicationContext {

	private static final Logger log = LoggerFactory.getLogger(AbstractApplicationContext.class);

	protected DefaultListableBeanFactory beanFactory;


	/**
	 * spring容器刷新核心方法
	 */
	public void refresh() {
		obtainFreshBeanFactory();
		invokeBeanFactoryPostProcessors();
		registerBeanPostProcessors();
		finishBeanFactoryInitialization();
	}

	/**
	 * 创建beanFactory，loadBeanDefinition
	 */
	protected void obtainFreshBeanFactory() {
		beanFactory = new DefaultListableBeanFactory();
	}

	/**
	 * 实例化并调用容器注册后处理器，容器后处理器
	 */
	private void invokeBeanFactoryPostProcessors() {
//		List<String> beanNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class);
//		for (String beanName : beanNames) {
//			BeanFactoryPostProcessor beanFactoryPostProcessor = (BeanFactoryPostProcessor) beanFactory.getBean(beanName);
//			if (beanFactoryPostProcessor instanceof BeanDefinitionRegistryPostProcessor) {
//				log.info("调用容器注册后处理器 {}", beanName);
//				((BeanDefinitionRegistryPostProcessor) beanFactoryPostProcessor).postProcessBeanDefinitionRegistry(beanFactory);
//			}
//			log.info("调用容器后处理器 {}", beanName);
//			beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
//		}
	}

	/**
	 * 注册bean后置处理器，调用在getBean中
	 */
	private void registerBeanPostProcessors() {
		List<String> beanNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class);
		for (String beanName : beanNames) {
			BeanPostProcessor beanPostProcessor = (BeanPostProcessor) beanFactory.getBean(beanName);
			log.info("注册Bean后置处理器 {}", beanName);
			beanFactory.addBeanPostProcessors(beanPostProcessor);
		}
	}

	/**
	 * 实例化单例bean
	 */
	private void finishBeanFactoryInitialization() {
		beanFactory.preInstantiateSingletons();
	}

	@Override
	public Object getBean(String name) {
		return beanFactory.getBean(name);
	}

	@Override
	public <T> T getBean(Class<T> requiredType) {
		return beanFactory.getBean(requiredType);
	}

//	@Override
//	public <T> List<T> getBeanList(Class<T> requiredType) {
//		return beanFactory.getBeanList(requiredType);
//	}
}
