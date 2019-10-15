package com.sumslack.dataset.api.controller;

import java.util.HashMap;
import java.util.Map;

import com.sumscope.tag.rest.annotation.URIAlias;
import com.sumscope.tag.rest.servlet.BaseController;
import com.sumscope.tag.util.IdWorker;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.UserBean;
import com.sumslack.excel.R;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
@URIAlias(value = "auth")
public class AuthController extends BaseController{
	//两小时后token失效
	public static final Cache<String, UserBean> userCache = new TimedCache<String, UserBean>(3600*2*1000);
	
	@URIAlias(value = "login")
	public R login() throws Exception {
		String username = StrUtil.formatNullStr(request.getParameter("username"));
		String pass = StrUtil.formatNullStr(request.getParameter("pass"));
		if(username.contains("test")) {
			userCache.forEach(u -> {
				if(u.getUsername().equals(username) && userCache.containsKey(u.getToken())) {
					userCache.remove(u.getToken());
				}
			});
			String token = IdWorker.getInstance().uuid();
			UserBean ub = new UserBean();
			ub.setUsername(username);
			ub.setNick("昵称"+username);
			ub.setToken(token);
			userCache.put(token, ub);
			Map userMap = new HashMap();
			userMap.put("user", ub.toJson());
			userMap.put("token", token);
			return R.ok(userMap);
		}else {
			return R.error(500, "登录失败！");
		}
	}
	
	@URIAlias(value = "logout")
	public R logout(String token) {
		if(userCache.containsKey(token)) {
			userCache.remove(token);
			return R.ok("登出成功！");
		}
		return R.error("无效的token值！");
	}
	
	@URIAlias(value = "checkToken")
	public R checkToken(String token) {
		if(userCache.containsKey(token)) {
			UserBean ub = userCache.get(token);
			Map userMap = new HashMap();
			userMap.put("user", ub.toJson());
			return R.ok(userMap);
		}
		return R.error("无效的token值！");
	}
	
}
