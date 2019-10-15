package com.sumslack.dataset.api.report.bean;

import cn.hutool.core.bean.BeanUtil;

public class UserBean {
	private String username;
	private String nick;
	private String sex;
	private String token;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getToken() {
		return token;
	}
	public UserBean setToken(String token) {
		this.token = token;
		return this;
	}
	
	public UserBean toJson() {
		UserBean _userBean = new UserBean(); 
		BeanUtil.copyProperties(this, _userBean);
		return _userBean.setToken(null);
	}
	
	
}
