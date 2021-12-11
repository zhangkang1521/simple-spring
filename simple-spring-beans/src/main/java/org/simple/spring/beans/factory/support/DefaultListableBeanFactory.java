package org.simple.spring.beans.factory.support;

import org.apache.commons.beanutils.BeanUtils;
import org.simple.spring.beans.PropertyValue;
import org.simple.spring.beans.factory.BeanFactory;
import org.simple.spring.beans.factory.config.BeanDefinition;
import org.simple.spring.beans.factory.config.RuntimeBeanReference;
import org.simple.spring.beans.factory.config.TypedStringValue;
import org.simple.spring.util.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean工厂实现
 * 存储BeanDefinition，单例bean
 * @author zhangkang
 */
public class DefaultListableBeanFactory implements BeanFactory {

	private static final Logger log = LoggerFactory.getLogger(DefaultListableBeanFactory.class);

	/** 存储所有beanDefinition beanName -> BeanDefinition */
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

	/** 单例bean */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();


	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
		log.info("注册BeanDefinition {}", beanName);
		this.beanDefinitionMap.put(beanName, beanDefinition);
	}

	@Override
	public Object getBean(String beanName) {
		Object sharedInstance = getSingleton(beanName);
		if (sharedInstance != null) {
			log.info("返回缓存中的实例 {}", sharedInstance);
			return sharedInstance;
		}
		BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
		if (beanDefinition == null) {
			throw new RuntimeException("未找到" + beanName + "的BeanDefinition，请检查配置");
		}
		// 创建bean
		Object bean = createBeanInstance(beanName, beanDefinition);
		// 依赖注入
		populateBean(beanName, bean, beanDefinition);
		// 初始化
		bean = initializeBean(beanName, bean, beanDefinition);
		addSingleton(beanName, bean);
		return bean;
	}




	/**
	 * 从缓存中获取
	 * @param name
	 * @return
	 */
	private Object getSingleton(String name) {
		return this.singletonObjects.get(name);
	}

	/**
	 * 加入缓存
	 * @param name
	 * @param bean
	 */
	private void addSingleton(String name, Object bean) {
		this.singletonObjects.put(name, bean);
	}

	/**
	 * 创建bean的实例
	 * @param beanDefinition
	 * @return
	 */
	private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
		Class clz = beanDefinition.resolveBeanClass();
		return SpringBeanUtils.instantiateClass(clz);
	}

	/**
	 * 依赖注入
	 * @param bean
	 * @param beanDefinition
	 */
	private void populateBean(String beanName, Object bean, BeanDefinition beanDefinition) {
		log.info("populateBean {} start", beanName);
		applyPropertyValues(beanName, bean, beanDefinition.getPropertyValueList());
		log.info("populateBean {} end", beanName);
	}

	/**
	 * 初始化bean
	 * @param name
	 * @param bean
	 * @param beanDefinition
	 */
	private Object initializeBean(String name, Object bean, BeanDefinition beanDefinition) {
		log.info("初始化bean {}", name);
//		invokeAwareMethod(name, bean);
		Object wrappedBean = bean;
		// 前置处理
		//wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, name);
		// 初始化方法
//		invokeInitMethod(wrappedBean, name);
		// 后置处理，可能返回bean的代理
		//wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, name);
		return wrappedBean;
	}



//	private Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
//		Object result = bean;
//		for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
//			result = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
//			if (result == null) {
//				return result;
//			}
//		}
//		return result;
//	}

//	private Object applyBeanPostProcessorsAfterInitialization(Object bean, String name) {
//		Object result = bean;
//		for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
//			result = beanPostProcessor.postProcessAfterInitialization(result, name);
//			if (result == null) {
//				return result;
//			}
//		}
//		return result;
//	}

//	private void invokeInitMethod(Object bean, String name) {
//		if (bean instanceof InitializingBean) {
//			((InitializingBean) bean).afterPropertiesSet();
//		}
//	}
//
//	private void invokeAwareMethod(String name, Object bean) {
//		if (bean instanceof BeanFactoryAware) {
//			((BeanFactoryAware) bean).setBeanFactory(this);
//		}
//	}




	private void applyPropertyValues(String beanName, Object bean, List<PropertyValue> propertyValueList) {
		for (PropertyValue propertyValue : propertyValueList) {
			String propertyName = propertyValue.getName();
			Object sourceValue = propertyValue.getValue();
			Object resolvedValue = resolveValue(sourceValue);
			log.debug("[{}] set property [{}], value [{}]", beanName, propertyName, resolvedValue);
			try {
				// TODO
				BeanUtils.setProperty(bean, propertyName, resolvedValue);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException("bean set property exception", e);
			}
		}
	}

	/**
	 * 解析property的sourceValue，sourceValue可能为TypedStringValue, RuntimeBeanReference等
	 * @param sourceValue
	 * @return
	 */
	private Object resolveValue(Object sourceValue) {
		if (sourceValue instanceof TypedStringValue) {
			return ((TypedStringValue) sourceValue).getValue();
		} else if (sourceValue instanceof RuntimeBeanReference) {
			log.info("获取依赖bean:{}", ((RuntimeBeanReference) sourceValue).getBeanName());
			return getBean(((RuntimeBeanReference) sourceValue).getBeanName());
		} else {
			throw new IllegalArgumentException("暂不支持的property");
		}
	}






}
