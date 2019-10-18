package com.sumslack.js;

import java.util.Date;

import com.sumslack.dataset.api.report.job.ReportJob;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.dialect.console.ConsoleLogFactory;

@JSFunc(id="console",description = "登录相关类")
public class JSLogger {
	private static final Log log = LogFactory.get();
	//需要将js调用的后台日志信息推送到前端页面，根据文件名推送
	static {
		LogFactory.setCurrentLogFactory(new ConsoleLogFactory());
	}
	private void send(String name,String msg) {
		ReportJob.broadcastLog(name, msg);
	}
	public void d(Object msg) {
		String str = ("["+DateUtil.format(new Date(), "HH:mm:ss")+"][js console DEBUG]" + Convert.toStr(msg,"null"));
		log.debug(str);
		send("",str);
	}
	public void e(Object msg) {
		String str = ("["+DateUtil.format(new Date(), "HH:mm:ss")+"][js console ERROR]" + Convert.toStr(msg,"null"));
		log.error(str);
		send("",str);
	}
	public void i(Object msg) {
		String str = ("["+DateUtil.format(new Date(), "HH:mm:ss")+"][js console INFO]" + Convert.toStr(msg,"null"));
		log.info(str);
		send("",str);
	}
	public void d(String tag,Object msg) {
		String str = ("["+DateUtil.format(new Date(), "HH:mm:ss")+"][js console DEBUG]" + Convert.toStr(msg,"null"));
		log.debug(str);
		send(tag,str);
	}
	public void log(Object... args) {
		if(args!=null && args.length>0) {
			StringBuilder sb = new StringBuilder();
			for(Object arg: args) {
				sb.append(", " + Convert.toStr(arg));
			}
			send("", "[<span class='log-time'>"+DateUtil.format(new Date(), "HH:mm:ss")+"</span>]: " + sb.toString().substring(1,sb.length()));
		}
	}
	
}
