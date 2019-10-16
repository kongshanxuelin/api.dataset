package com.sumslack.dataset.api.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.sumscope.tag.rest.TagRest;
import com.sumscope.tag.rest.servlet.AjaxServlet;
import com.sumscope.tag.util.HttpUtils;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.ApiBean;
import com.sumslack.dataset.api.report.util.AuthManager;
import com.sumslack.dataset.api.report.util.IApi;
import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.dataset.api.report.vo.ReportVO;
import com.sumslack.dataset.api.report.vo.ReportVO.RET;

import cn.hutool.core.util.ReflectUtil;

@TagRest(value="api/id/*/*")
public class ApiServlet extends AjaxServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8814286860867527336L;


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		ReportVO report = new ReportVO();
		report.setRet(ReportVO.RET.SUCCESS);
		String fileName = StrUtil.formatNullStr(request.getAttribute("$1"));
		String apiId = StrUtil.formatNullStr(request.getAttribute("$2"));
		ApiBean api = ReportUtil.getApi(fileName,apiId);
		try {
			if(api!=null) {
				//验证接口是否需要登录才能访问
				if(api.isAuth()) {
					if(!AuthManager.getInstance().checkToken(AuthManager.getInstance().getTokenFromWeb(request))) {
						report.setRet(ReportVO.RET.NOTAUTH);
						report.setErrMsg("未传入token或传入的token值不对！");
					}
				}else {
					if(api.getLang().equals("js")) {
						report = new ReportVO();
						report.setRet(ReportVO.RET.SUCCESS);
						report.setResult(api.invokeJavascript(fileName,api,HttpUtils.getParamMap(request)));
						report.setTitle(api.getTitle());
					}else if(api.getLang().equals("java")) {
						IApi myReport = ReflectUtil.newInstance(api.getContent());
						printOut(response, request, JSON.toJSONString(myReport.genApi(fileName,api,HttpUtils.getParamMap(request))));
					}
				}
			}else {
				report.setRet(ReportVO.RET.NOTFOUND);
				report.setErrMsg("找不到该报表，请在配置文件中中定义api节点id为 【" + apiId + "】 的节点！");
			}
			
			printOut(response, request, JSON.toJSONString(report));
		}catch(Exception ex) {
			ex.printStackTrace();
			report.setRet(RET.ERROR);
			report.setErrMsg(ex.getMessage());
			printOut(response, request, JSON.toJSONString(report));
		}
	}
	
	
}
