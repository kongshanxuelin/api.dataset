package com.sumslack.js;

import cn.hutool.core.convert.Convert;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.dialect.console.ConsoleLogFactory;

@JSFunc(id="logger",description = "登录相关类")
public class JSLogger {
	private static final Log log = LogFactory.get();
	static {
		LogFactory.setCurrentLogFactory(new ConsoleLogFactory());
	}
	public void d(Object msg) {
		log.debug("[js console]" + Convert.toStr(msg,"null"));
		System.out.println("==debug=="+ Convert.toStr(msg,"null"));
	}
	public void e(Object msg) {
		log.error("[js console]" + Convert.toStr(msg,"null"));
		System.out.println("==error=="+ Convert.toStr(msg,"null"));
	}
	public void i(Object msg) {
		log.info("[js console]" + Convert.toStr(msg,"null"));
		System.out.println("==info=="+ Convert.toStr(msg,"null"));
	}
	
}
