package org.simple.spring.beans.factory.support;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.simple.spring.beans.PropertyValue;
import org.simple.spring.beans.factory.BeanFactory;
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
import java.util.ArrayList;
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

	/** bean后置处理器 */
	private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

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

	public void addBeanPostProcessors(BeanPostProcessor beanPostProcessor) {
		this.beanPostProcessors.add(beanPostProcessor);
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

	private List<BeanPostProcessor> getBeanPostProcessors() {
		return beanPostProcessors;
	}

}
