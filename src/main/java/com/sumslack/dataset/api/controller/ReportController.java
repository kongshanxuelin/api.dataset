package com.sumslack.dataset.api.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sumscope.tag.TagConst;
import com.sumscope.tag.rest.annotation.Post;
import com.sumscope.tag.rest.annotation.URIAlias;
import com.sumscope.tag.rest.servlet.BaseController;
import com.sumslack.dataset.api.report.bean.ReportLineBean;
import com.sumslack.dataset.api.report.job.ReportJob;
import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.excel.R;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
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
		request.setAttribute("time", FileUtil.lastModifiedTime(filePath).getTime());
		return "/file.jsp";
	}
	
	@URIAlias(value = "clearCache")
	public R clearCache() throws Exception {
		R r = new R();
		ReportLineBean.resultCache.clear();
		r.ok("clear cache ok.");
		return r;
	}
	
	@Post
	@URIAlias(value = "file/save")
	public Map saveFile(String content,String time) {
		Map retMap = new HashMap();
		retMap.put("ret", 0);
		
		long _time = FileUtil.lastModifiedTime(ReportJob.REPORT_FILEPATH).getTime();
		if(Convert.toLong(time)!=_time) {
			retMap.put("ret", 502);
			retMap.put("msg","当前文件已被其他人保存，请拷贝你修改的部分重新刷新页面后保存！");
			return retMap;
		}
		try {
			ReportUtil.initReports();
			File file = FileUtil.writeString(content, new File(ReportJob.REPORT_FILEPATH), Charset.forName("UTF-8"));
			retMap.put("c", FileUtil.readString(file, Charset.forName("UTF-8")));
			retMap.put("time", FileUtil.lastModifiedTime(ReportJob.REPORT_FILEPATH).getTime());
		}catch(Exception ex) {
			ex.printStackTrace();
			retMap.put("msg", "XML解析出错，请核查XML文件！");
			retMap.put("ret", 500);
		}
		return retMap;
	}
}
