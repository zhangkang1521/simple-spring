package org.simple.spring.beans.factory.support;

import org.simple.spring.beans.factory.BeanFactory;
import org.simple.spring.beans.factory.config.BeanDefinition;
import org.simple.spring.beans.factory.config.BeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangkang
 * @create 2022/2/19 17:56
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

	private static Logger log = LoggerFactory.getLogger(AbstractBeanFactory.class);

	/**
	 * bean后置处理器
	 */
	private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();


	@Override
	public Object getBean(String beanName) {
		Object sharedInstance = getSingleton(beanName);
		if (sharedInstance != null) {
			log.info("返回缓存中的实例 {}", sharedInstance);
			return sharedInstance;
		}
		BeanDefinition beanDefinition = getBeanDefinition(beanName);
		Object bean = createBean(beanName, beanDefinition);
		return bean;
	}

	/**
	 * 获取BeanDefinition，由DefaultListableBeanFactoty实现
	 *
	 * @param beanName
	 * @return
	 */
	protected abstract BeanDefinition getBeanDefinition(String beanName);

	/**
	 * 创建bean，由AbstractAutowireCapableBeanFactory实现
	 *
	 * @param beanName
	 * @param beanDefinition
	 * @return
	 */
	protected abstract Object createBean(String beanName, BeanDefinition beanDefinition);

	public void addBeanPostProcessors(BeanPostProcessor beanPostProcessor) {
		this.beanPostProcessors.add(beanPostProcessor);
	}

	public List<BeanPostProcessor> getBeanPostProcessors() {
		return beanPostProcessors;
	}

}
