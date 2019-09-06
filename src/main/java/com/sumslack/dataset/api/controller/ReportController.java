package com.sumslack.dataset.api.controller;

import java.nio.charset.Charset;
import java.util.Set;

import com.sumscope.tag.TagConst;
import com.sumscope.tag.rest.annotation.URIAlias;
import com.sumscope.tag.rest.servlet.BaseController;
import com.sumslack.dataset.api.report.job.ReportJob;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
@URIAlias(value = "/")
public class ReportController extends BaseController{
	@URIAlias(value = "home")
	public String view() throws Exception {
		String content = FileUtil.readString(ReportJob.REPORT_FILEPATH, Charset.forName("UTF-8"));
		String filePath = ReportJob.REPORT_FILEPATH;
		Set dss = TagConst.dataSourceMap.keySet();
		request.setAttribute("path",filePath);
		request.setAttribute("c", content);
		request.setAttribute("ds", CollectionUtil.join(dss, ","));
		return "/file.jsp";
	}
}
