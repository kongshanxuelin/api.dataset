package com.sumslack.dataset.api.report.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.sumscope.tag.util.IdWorker;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;

public class AuthManager {
	//两小时后token失效
	public static final Cache<String, Map> userCache = new TimedCache<String, Map>(3600*2*1000);
		
	private static AuthManager instance = new AuthManager();
	private AuthManager() {}
	public static AuthManager getInstance() {
		if(instance == null) {
			instance = new AuthManager();
		}
		return instance;
	}
	
	public String getTokenFromWeb(HttpServletRequest request) {
		String token = Convert.toStr(request.getParameter("token"));
		if(StrUtil.isEmpty(token)) {
			token = Convert.toStr(request.getHeader("token"));
		}else if(StrUtil.isEmpty(token)) {
			token = Convert.toStr(request.getHeader("authorize"));
		}
		return token;
	}
	
	public String getToken() {
		return IdWorker.getInstance().uuid();
	}
	
	public void saveToken(String token,Map userMap) {
		userMap.put("token",token);
		userCache.put(token, userMap);
	}
	
	public void removeToken(String token) {
		userCache.remove(token);
	}
	
	public void logout(final String token,final String userid) {
		Map _user = userCache.get(token);
		if(_user!=null && userid!=null) {
			String _userid = Convert.toStr(_user.get("id"));
			if(_userid != null && _userid.equals(userid)) {
				//删除之前登录的该账户tokens
				userCache.forEach( s -> {
					String _thisUserId = Convert.toStr(s.get("id"));
					if(_thisUserId.equals(userid)) {
						userCache.remove(Convert.toStr(s.get("token")));
					}
				});
			}
		}
	}
	
	public boolean checkToken(String token) {
		return userCache.get(token)!=null && !userCache.get(token).isEmpty();
	}
}
