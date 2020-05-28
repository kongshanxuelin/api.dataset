package com.sumslack.dataset.api.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sumscope.tag.rest.TagRest;
import com.sumscope.tag.rest.servlet.AjaxServlet;
import com.sumscope.tag.util.StrUtil;

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
		request.getRequestDispatcher("/ui.jsp").forward(request, response);
	}
	
}
