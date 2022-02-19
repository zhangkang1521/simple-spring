package org.simple.spring.beans.factory.support;

import org.apache.commons.beanutils.BeanUtils;
import org.simple.spring.beans.PropertyValue;
import org.simple.spring.beans.factory.BeanFactoryAware;
import org.simple.spring.beans.factory.InitializingBean;
import org.simple.spring.beans.factory.config.BeanDefinition;
import org.simple.spring.beans.factory.config.BeanPostProcessor;
import org.simple.spring.beans.factory.config.RuntimeBeanReference;
import org.simple.spring.beans.factory.config.TypedStringValue;
import org.simple.spring.util.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author zhangkang
 * @create 2022/2/19 18:00
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

	private static Logger log = LoggerFactory.getLogger(AbstractAutowireCapableBeanFactory.class);

	@Override
	public Object createBean(String beanName, BeanDefinition beanDefinition) {
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
		invokeAwareMethod(bean);
		Object wrappedBean = bean;
		// 前置处理
		wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, name);
		// 初始化方法
		invokeInitMethod(wrappedBean);
		// 后置处理，可能返回bean的代理
		wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, name);
		return wrappedBean;
	}

	private Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
		Object result = bean;
		for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
			result = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
			if (result == null) {
				return result;
			}
		}
		return result;
	}

	private Object applyBeanPostProcessorsAfterInitialization(Object bean, String name) {
		Object result = bean;
		for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
			result = beanPostProcessor.postProcessAfterInitialization(result, name);
			if (result == null) {
				return result;
			}
		}
		return result;
	}

	private void invokeInitMethod(Object bean) {
		if (bean instanceof InitializingBean) {
			((InitializingBean) bean).afterPropertiesSet();
		}
		// TODO  xml中配置的init-method
	}

	private void invokeAwareMethod(Object bean) {
		if (bean instanceof BeanFactoryAware) {
			((BeanFactoryAware) bean).setBeanFactory(this);
		}
		// TODO 其他aware接口
	}


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
