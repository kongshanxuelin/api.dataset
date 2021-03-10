package com.sumslack.dataset.api.controller;

import java.util.HashMap;
import java.util.Map;

import com.sumscope.tag.TagConst;
import com.sumscope.tag.rest.annotation.URIAlias;
import com.sumscope.tag.rest.servlet.BaseController;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.excel.R;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

/**
 * 极光推送整合
 */
@URIAlias(value = "jpush")
public class JPushController extends BaseController{
	private static JPushClient jpushClient = new JPushClient(
			StrUtil.formatNullStr(TagConst.globalMap.get("jpush_sec")),
			StrUtil.formatNullStr(TagConst.globalMap.get("jpush_key"))
			);
	@URIAlias(value = "msg")
	public R msg(String title,String msg) throws Exception {
		Map<String, String> parm = new HashMap();
		parm.put("id", "test");
		parm.put("msg",msg);
		PushPayload payload = PushPayload.newBuilder()
				.setPlatform(Platform.android())//指定android平台的用户
				.setAudience(Audience.all())//你项目中的所有用户
				//.setAudience(Audience.registrationId(parm.get("id")))//registrationId指定用户
				.setNotification(Notification.android(msg, title, parm))
				//发送内容
				.setOptions(Options.newBuilder().setApnsProduction(false).build())
				//这里是指定开发环境,不用设置也没关系
				.setMessage(Message.content(msg))//自定义信息
				.build();
		try {
			PushResult pu = jpushClient.sendPush(payload);  
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return R.ok();
	}
}
