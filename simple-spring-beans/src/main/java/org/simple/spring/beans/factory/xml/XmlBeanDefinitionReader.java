package org.simple.spring.beans.factory.xml;

import org.simple.spring.beans.PropertyValue;
import org.simple.spring.beans.factory.config.BeanDefinition;
import org.simple.spring.beans.factory.config.RuntimeBeanReference;
import org.simple.spring.beans.factory.config.TypedStringValue;
import org.simple.spring.beans.factory.support.DefaultListableBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 读取xml到BeanDefinition
 */
public class XmlBeanDefinitionReader {

	public static final Logger log = LoggerFactory.getLogger(XmlBeanDefinitionReader.class);

	public static final String DEFAULT_NAMESPACE_URI = "http://www.springframework.org/schema/beans";

	private static final String SCHEMA_LANGUAGE_ATTRIBUTE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	private static final String XSD_SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";

	public static final String BEAN_ELEMENT = "bean";
	public static final String ALIAS_ELEMENT = "alias";
	public static final String IMPORT_ELEMENT = "import";
	public static final String BEANS_ELEMENT = "beans";

	private DefaultListableBeanFactory defaultListableBeanFactory;


	public XmlBeanDefinitionReader(DefaultListableBeanFactory defaultListableBeanFactory) {
		this.defaultListableBeanFactory = defaultListableBeanFactory;
	}

	/**
	 * 读取classpath下的xml文件，注册BeanDefinition
	 * @param resource
	 */
	public void loadBeanDefinition(String resource) {
		log.info("loadBeanDefinition from classpath {}", resource);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(resource);

		try {
			InputSource inputSource = new InputSource(inputStream);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			// 简单起见不验证xml格式，spring中使用DTD或XSD进行验证
			factory.setValidating(false);
			factory.setAttribute(SCHEMA_LANGUAGE_ATTRIBUTE, XSD_SCHEMA_LANGUAGE);

			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			// 整个xml文档
			Document doc = dBuilder.parse(inputSource);
			// beans
			Element root = doc.getDocumentElement();
			// beans的子元素
			NodeList noList = root.getChildNodes();
			for (int i = 0; i < noList.getLength(); i++) {
				Node node = noList.item(i);
				if (node instanceof Element) {
					if (DEFAULT_NAMESPACE_URI.equals(node.getNamespaceURI())) {
						parseDefaultElement((Element) node);
					} else {
						parseCustomElement((Element) node);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("xml解析异常", e);
		} catch (SAXException e) {
			throw new RuntimeException("xml解析异常", e);
		} catch (IOException e) {
			throw new RuntimeException("xml解析io异常", e);
		}
	}

	/**
	 * 解析默认标签
	 * @param node
	 */
	private void parseDefaultElement(Element node) {
		String nodeName = node.getNodeName();
		if (Objects.equals(nodeName, BEAN_ELEMENT)) {
			// <bean id="" class="">
			processBeanDefinition(node);
		}
		else if (Objects.equals(nodeName, ALIAS_ELEMENT)) {
			// <alias name="" alias=""/>
			// TODO
		}
		else if (Objects.equals(nodeName, IMPORT_ELEMENT)) {
			// <import>
			// TODO
		}
		else if (Objects.equals(nodeName, BEANS_ELEMENT)) {
			// <beans>...</beans> 递归调用
			// TODO
		}
	}

	/**
	 * 解析<bean></bean>
	 * @param node
	 */
	private void processBeanDefinition(Element node) {
		String beanName = node.getAttribute("id");
		String className = node.getAttribute("class");
		BeanDefinition beanDefinition = new BeanDefinition();
		beanDefinition.setBeanClass(className);
		parseProperty(node, beanDefinition);
		defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
	}

	/**
	 * 处理自定义标签，例如<context:component-scan base-package="xxx"/>
	 * @param node
	 */
	private void parseCustomElement(Element node) {
		// TODO 解析自定义标签
	}



	private void parseProperty(Element element, BeanDefinition beanDefinition) {
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && "property".equals(node.getNodeName())) {
				String name = ((Element) node).getAttribute("name");
				String value = ((Element) node).getAttribute("value");
				if (value != null && value.length() > 0) {
					beanDefinition.getPropertyValueList().add(new PropertyValue(name, new TypedStringValue(value)));
				} else {
					String ref = ((Element) node).getAttribute("ref");
					beanDefinition.getPropertyValueList().add(new PropertyValue(name, new RuntimeBeanReference(ref)));
				}
			}
		}
	}
}
