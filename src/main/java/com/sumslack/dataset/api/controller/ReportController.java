package com.sumslack.dataset.api.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.utils.Lists;

import com.sumscope.tag.TagConst;
import com.sumscope.tag.rest.annotation.Post;
import com.sumscope.tag.rest.annotation.URIAlias;
import com.sumscope.tag.rest.servlet.BaseController;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.ReportLineBean;
import com.sumslack.dataset.api.report.job.ReportJob;
import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.excel.R;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
@URIAlias(value = "/")
public class ReportController extends BaseController{
	
	@URIAlias(value = "home/*")
	public String view() throws Exception {
		String fileName = StrUtil.formatNullStr(request.getAttribute("$1"),"report")  + ".xml";
		String filePath = ReportJob.REPORT_FILEPATH + fileName;
		if(!ReUtil.isMatch("\\w+",StrUtil.formatNullStr(request.getAttribute("$1"),"report"))) {
			request.setAttribute("code", 500);
			request.setAttribute("c", "文件名必须是英文，数字或下划线的组合，您的命令不符合规范！");
			return "/file.jsp";
		}
		request.setAttribute("code", 0);
		if(!FileUtil.exist(filePath)) {
			FileUtil.touch(filePath);
			String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
			content += "<xml>\r\n";
			content += "\t<reports ds=\"default\">\r\n";
			content += "\t</reports>\r\n";
			content += "\t<apis ds=\"default\">\r\n";
			content += "\t</apis>\r\n";
			content += "</xml>";
			List<String> list = CollectionUtil.newArrayList(content.split("\r\n"));			
			FileUtil.writeLines(list, new File(filePath), Charset.forName("UTF-8"));
			content = FileUtil.readString(filePath, Charset.forName("UTF-8"));
			request.setAttribute("c", content);
			request.setAttribute("isnew",true);
		}else {
			String content = FileUtil.readString(filePath, Charset.forName("UTF-8"));
			request.setAttribute("c", content);
			request.setAttribute("isnew",false);
		}
		Set dss = TagConst.dataSourceMap.keySet();
		request.setAttribute("ds", CollectionUtil.join(dss, ","));
		request.setAttribute("path",filePath);
		request.setAttribute("file",fileName);
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
	public Map saveFile(String content,String time,String file) {
		String filePath = ReportJob.REPORT_FILEPATH + file;
		
		Map retMap = new HashMap();
		retMap.put("ret", 0);
		
		long _time = FileUtil.lastModifiedTime(filePath).getTime();
		if(Convert.toLong(time)!=_time) {
			retMap.put("ret", 502);
			retMap.put("msg","保存失败！！！当前文件已被其他人保存，请刷新页面再保存！");
			return retMap;
		}
		try {
			ReportUtil.initReports(file);
			File _file = FileUtil.writeString(content, filePath, Charset.forName("UTF-8"));
			retMap.put("c", FileUtil.readString(_file, Charset.forName("UTF-8")));
			retMap.put("time", FileUtil.lastModifiedTime(filePath).getTime());
		}catch(Exception ex) {
			ex.printStackTrace();
			retMap.put("msg", "XML解析出错，请核查XML文件！");
			retMap.put("ret", 500);
		}
		return retMap;
	}
}
