package com.sumslack.dataset.api.report.bean;

import java.io.Serializable;

/**
 * WebSocket对象
 * @author user
 *
 */
public class WsMsg implements Serializable{
	private String userName;
	private Object msg;
	public WsMsg() {}
	public WsMsg(String userName,Object msg) {
		this.userName = userName;
		this.msg = msg;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}
	
	
}
