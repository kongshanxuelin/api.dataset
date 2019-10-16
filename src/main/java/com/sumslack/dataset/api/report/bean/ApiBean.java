package com.sumslack.dataset.api.report.bean;

import java.io.Serializable;
import java.util.Map;

import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.excel.JSUtil;

import cn.hutool.core.util.StrUtil;

public class ApiBean implements Serializable{
	private String id;
	private String title;
	private boolean isAuth =false;//该接口是否需要登录认证才能访问
	private String type="map"; 
	private String lang="js";
	private String content;
	
	//可以直接返回JS对象
	private Object result;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isAuth() {
		return isAuth;
	}
	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public Object invokeJavascript(Map paramMap) throws Exception{
		if(!StrUtil.isEmpty(this.content)) {
			//String _content = ReportUtil.parseParam(this.content, paramMap);
			this.result = JSUtil.execRetApi("(function(){"+content+"})()",paramMap);
		}
		return this.result;
	}
}
