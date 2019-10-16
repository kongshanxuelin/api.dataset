package com.sumslack.js;

import java.util.HashMap;
import java.util.Map;

import com.sumslack.dataset.api.report.util.AuthManager;

import cn.hutool.core.convert.Convert;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

@JSFunc(id="Auth",description = "登录相关类")
public class Auth {
	public String genToken() {
		return AuthManager.getInstance().getToken();
	}
	public boolean storeToken(String token,Object user) {
		Map userMap = new HashMap();
		if(user instanceof ScriptObjectMirror) {
			ScriptObjectMirror _user = (ScriptObjectMirror)user;
			for(String key : _user.keySet()) {
				userMap.put(key, _user.get(key));
			}
		}else if(user instanceof String || user instanceof Integer || user instanceof Long) { //如果传入的字符串或整数，都当做是userid
			userMap.put("userid", Convert.toStr(user));
		}
		AuthManager.getInstance().saveToken(token, userMap);
		return true;
	}
	public void logout(String token,String userid) {
		AuthManager.getInstance().logout(token, userid);
	}
	public boolean checkToken(String token) {
		return AuthManager.getInstance().checkToken(token);
	}
}
