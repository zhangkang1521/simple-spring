package org.simple.spring.beans.factory;

/**
 * @author zhangkang
 * @create 2021/12/11 18:35
 */
public class User {

	private Integer id;
	private String username;
	private Order order;

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
}
