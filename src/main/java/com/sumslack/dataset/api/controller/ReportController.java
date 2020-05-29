package com.sumslack.dataset.api.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sumscope.tag.TagConst;
import com.sumscope.tag.rest.annotation.Post;
import com.sumscope.tag.rest.annotation.URIAlias;
import com.sumscope.tag.rest.servlet.BaseController;
import com.sumscope.tag.util.HttpUtils;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.ApiBean;
import com.sumslack.dataset.api.report.bean.ReportLineBean;
import com.sumslack.dataset.api.report.job.ReportJob;
import com.sumslack.dataset.api.report.util.AuthManager;
import com.sumslack.dataset.api.report.util.IApi;
import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.dataset.api.report.vo.ReportVO;
import com.sumslack.excel.JSUtil;
import com.sumslack.excel.R;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
@URIAlias(value = "/")
public class ReportController extends BaseController{
	@URIAlias(value = "run/*")
	public String run() throws Exception {
		String fileName = StrUtil.formatNullStr(request.getAttribute("$1"),"report")  + ".xml";
		request.setAttribute("file",fileName);
		return "/run.jsp";
	}
	
	@URIAlias(value = "ws")
	public String ws(String name,String msg) throws Exception {
		ReportJob.broadcastLog(name, msg);
		return "push test";
	}
	
	@Post
	@URIAlias(value = "runcode")
	public Map runPreview(String content,String lang,String file) {
		Map retMap = new HashMap();
		ApiBean apiBean = new ApiBean();
		apiBean.setId("test");
		apiBean.setLang(StrUtil.formatNullStr(lang,"js"));
		apiBean.setContent(content);
		apiBean.setAuth(false);
		apiBean.setTitle("test");
		Object ret = JSUtil.execRetApi(file,apiBean,"(function(){"+content+"})()",getParamMap());
		if(ret instanceof String) {
			retMap.put("ret", ret.toString());
		}else {
			retMap.put("ret", JSON.toJSONString(ret));
		}
		return retMap;
	}
	
	@URIAlias(value = "ui")
	public Map ui() {
		Map retMap = new HashMap();
		retMap.put("res",false);
		String apiId = StrUtil.formatNullStr(request.getParameter("apiId"));
		String fileName = StrUtil.formatNullStr(request.getParameter("fileName"));
		ApiBean api = ReportUtil.getApi(fileName,apiId);
		Object result = null;
		if(api!=null) {
			//验证接口是否需要登录才能访问
			if(api.isAuth()) {
				if(!AuthManager.getInstance().checkToken(AuthManager.getInstance().getTokenFromWeb(request))) {
					retMap.put("msg","未传入token或传入的token值不对！");
				}
			}else {
				if(api.getLang().equals("js")) {
					try {
						result = api.invokeJavascript(fileName,api,HttpUtils.getParamMap(request));
					} catch (Exception e) {
						e.printStackTrace();
						retMap.put("msg","脚本执行错误！");
					}
				}else if(api.getLang().equals("java")) {
					IApi myReport = ReflectUtil.newInstance(api.getContent());
					result = myReport.genApi(fileName,api,HttpUtils.getParamMap(request));
				}
				retMap.put("res",true);
				retMap.put("result", result);
			}
		}else {
			retMap.put("msg","找不到该报表，请在配置文件中中定义api节点id为 【" + apiId + "】 的节点！");
		}
				
		//获取每个字段的key		
		if(api!=null) {
			List<String> fieldList = cn.hutool.core.util.StrUtil.split(api.getUiField(), ',');
			List<String> titleList = cn.hutool.core.util.StrUtil.split(api.getUiTitle(), ',');
			if(fieldList==null || titleList==null || fieldList.size()<1 || fieldList.size()!=titleList.size()) {
				if(result!=null && result instanceof List) {
					List list = (List)result;
					if(list!=null && list.size()>0) {
						Map<String,Object> fieldMap = (Map)list.get(0);
						fieldList = Lists.newArrayList();
						titleList = Lists.newArrayList();
						for(String ff : fieldMap.keySet()) {
							fieldList.add(ff);
							titleList.add(ff);
						}
					}
				}
			}
			retMap.put("fields", fieldList);
			retMap.put("titles", titleList);
			
		}
		return retMap;
	}
	
	@URIAlias(value = "home/*")
	public String view() throws Exception {
		String fileName = StrUtil.formatNullStr(request.getAttribute("$1"),"report")  + ".xml";
		String filePath = ReportJob.REPORT_FILEPATH + fileName;
		if(!ReUtil.isMatch("\\w+",StrUtil.formatNullStr(request.getAttribute("$1"),"report"))) {
			request.setAttribute("code", 500);
			request.setAttribute("c", "文件名必须是英文，数字或下划线的组合，您的命令不符合规范！");
			return request.getContextPath() + "/file.jsp";
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
