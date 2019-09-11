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
import com.sumslack.dataset.api.report.bean.ReportBean;
import com.sumslack.dataset.api.report.util.IReport;
import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.dataset.api.report.vo.ReportVO;
import com.sumslack.dataset.api.report.vo.ReportVO.RET;

import cn.hutool.core.util.ReflectUtil;

@TagRest(value="report/id/*")
public class Home extends AjaxServlet{
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
		String reportId = StrUtil.formatNullStr(request.getAttribute("$1"));
		ReportBean rb = ReportUtil.getReport(reportId);
		try {
			if(rb!=null) {
				//这种情况下，需要SQL或存储过程定义数据集
				if(StrUtil.isEmpty(rb.getJava())) {
					rb.init(HttpUtils.getParamMap(request));
					report = new ReportVO();
					report.setRet(ReportVO.RET.SUCCESS);
					if(!StrUtil.isEmpty(rb.getStartDate()) && !StrUtil.isEmpty(rb.getEndDate())) {
						try {
							report.setCols(rb.getCols(HttpUtils.getParamMap(request)));
						} catch (Exception e) {
							e.printStackTrace();
							report.setRet(ReportVO.RET.ERROR);
							report.setErrMsg("发生错误：" + e.getMessage());
						}
					}
					report.setRows(rb.returnReportJSON(HttpUtils.getParamMap(request)));
					report.setResult(rb.returnJSResult(HttpUtils.getParamMap(request)));
					report.setTitle(rb.getTitle());
				}else { //自己实现完整的报表
					IReport myReport = ReflectUtil.newInstance(rb.getJava());
					printOut(response, request, JSON.toJSONString(myReport.genReport(rb,HttpUtils.getParamMap(request))));
				}
			}else {
				report.setRet(ReportVO.RET.NOTFOUND);
				report.setErrMsg("找不到该报表，请在report.xml中定义report节点id为 【" + reportId + "】 的节点！");
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
