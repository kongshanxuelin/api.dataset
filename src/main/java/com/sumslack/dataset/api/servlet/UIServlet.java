package com.sumslack.dataset.api.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sumscope.tag.rest.TagRest;
import com.sumscope.tag.rest.servlet.AjaxServlet;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.ApiBean;
import com.sumslack.dataset.api.report.util.ReportUtil;

import cn.hutool.core.convert.Convert;

@TagRest(value="ui/api/*/*")
public class UIServlet extends AjaxServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6825707795705169881L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		String fileName = StrUtil.formatNullStr(request.getAttribute("$1"));
		String apiId = StrUtil.formatNullStr(request.getAttribute("$2"));
		request.setAttribute("fileName", fileName);
		request.setAttribute("apiId", apiId);
		
		//加载自定义UI部分
		ApiBean api = ReportUtil.getApi(fileName,apiId);
		if(api!=null) {
			request.setAttribute("uiHeader", Convert.toStr(api.getUiHeader(),""));
			request.setAttribute("uiFooter", Convert.toStr(api.getUiFooter(),""));
			request.setAttribute("uiJs", Convert.toStr(api.getUiJS(),""));
			request.getRequestDispatcher("/ui.jsp").forward(request, response);
		}else {
			request.setAttribute("msg", "无法在 【 "+fileName+".xml 】 配置文件中找到API节点【"+apiId+"】，请核查您的配置是否正确！");
			request.getRequestDispatcher("/error.jsp").forward(request, response);
		}
		
		
	}
	
}
