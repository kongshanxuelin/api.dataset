package com.sumslack.dataset.api.controller;

import com.sumscope.tag.rest.servlet.BaseController;
import com.sumscope.tag.util.StrUtil;

public class AuthAbsController extends BaseController{
	protected boolean isLogin() {
		String token = StrUtil.formatNullStr(request.getParameter("token"));
		if(token.equals("")) {
			token = StrUtil.formatNullStr(request.getHeader("token"));
		}
		if(token.equals("")) {
			token = StrUtil.formatNullStr(request.getHeader("authorize"));
		}
		return AuthController.userCache.containsKey(token);
	}
}
