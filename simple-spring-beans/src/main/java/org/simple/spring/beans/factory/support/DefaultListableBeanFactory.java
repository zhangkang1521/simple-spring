package org.simple.spring.beans.factory.support;

import org.apache.commons.collections.CollectionUtils;
import org.simple.spring.beans.factory.config.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean工厂实现
 * 存储BeanDefinition
 * @author zhangkang
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {

	private static final Logger log = LoggerFactory.getLogger(DefaultListableBeanFactory.class);

	/** 存储所有beanDefinition beanName -> BeanDefinition */
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();


	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
		log.info("注册BeanDefinition {}", beanName);
		this.beanDefinitionMap.put(beanName, beanDefinition);
	}

	@Override
	protected BeanDefinition getBeanDefinition(String beanName) {
		BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
		if (beanDefinition == null) {
			throw new RuntimeException("未找到" + beanName + "的BeanDefinition，请检查配置");
		}
		return beanDefinition;
	}

	@Override
	public <T> T getBean(Class<T> requiredType) {
		List<String> beanNames = getBeanNamesForType(requiredType);
		if (CollectionUtils.isEmpty(beanNames)) {
			throw new RuntimeException("没有找到bean，requiredType:" + requiredType);
		}
		if (beanNames.size() > 1) {
			throw new RuntimeException("找到多个bean错误，requiredType: " + requiredType);
		}
		return (T)getBean(beanNames.get(0));
	}

	public List<String> getBeanNamesForType(Class<?> type) {
		List<String> beanNames = new ArrayList<>();
		this.beanDefinitionMap.forEach((beanName, beanDefinition) -> {
			Class<?> clz = beanDefinition.resolveBeanClass();
//			if (FactoryBean.class.isAssignableFrom(clz)) {
//				// 存在死循环 getBean("userDao") -> getBean("sqlSessionFactory") -> getBeanNamesForType(DataSource) -> getBean("&userDao") -> getBean("sqlSessionFactory")
//				FactoryBean factoryBean = (FactoryBean) getBean(BeanFactory.FACTORY_BEAN_PREFIX + beanName);
//				clz = factoryBean.getObjectType();
//			}
			if (type.isAssignableFrom(clz)) {
				beanNames.add(beanName);
			}
		});
		return beanNames;
	}

	/**
	 * 初始化bean
	 */
	public void preInstantiateSingletons() {
		log.info("初始化所有单例bean");
		for (String beanName : beanDefinitionMap.keySet()) {
			getBean(beanName);
		}
	}
}
