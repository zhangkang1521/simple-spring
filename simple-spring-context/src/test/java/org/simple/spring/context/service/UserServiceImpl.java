package org.simple.spring.context.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangkang
 * @create 2022/1/23 15:56
 */
public class UserServiceImpl implements UserService {

	private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public String sayHello(String msg) {
		log.info("调用UserServiceImpl sayHello方法");
		return "hello, " + msg;
	}
}
