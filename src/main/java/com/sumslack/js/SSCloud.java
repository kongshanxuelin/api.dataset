package com.sumslack.js;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sumslack.common.api.ApiManager;

@JSFunc(id="cloud",description = "新增调用sumslack云平台接口服务")
public class SSCloud {
	public JSONObject post(String appId,String appSec,String apiUrl,Map<String,String> params) {
		JSONObject ret = new JSONObject();
		String msg = ApiManager.post(appId, appSec, apiUrl, params);
		ret.put("result", msg);
		return ret;
	}
	public JSONObject get(String appId,String appSec,String apiUrl,Map<String,String> params) {
		JSONObject ret = new JSONObject();
		String msg = ApiManager.get(appId, appSec, apiUrl, params);
		ret.put("result", msg);
		return ret;
	}
	public JSONObject put(String appId,String appSec,String apiUrl,Map<String,String> params) {
		JSONObject ret = new JSONObject();
		String msg = ApiManager.put(appId, appSec, apiUrl, params);
		ret.put("result", msg);
		return ret;
	}
	public JSONObject delete(String appId,String appSec,String apiUrl,Map<String,String> params) {
		JSONObject ret = new JSONObject();
		String msg = ApiManager.delete(appId, appSec, apiUrl, params);
		ret.put("result", msg);
		return ret;
	}
}
