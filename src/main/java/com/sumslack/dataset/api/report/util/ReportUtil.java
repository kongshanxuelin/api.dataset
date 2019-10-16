package com.sumslack.dataset.api.report.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.compress.utils.Lists;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.ApiBean;
import com.sumslack.dataset.api.report.bean.ReportBean;
import com.sumslack.dataset.api.report.bean.ReportLineBean;
import com.sumslack.dataset.api.report.job.ReportJob;
import com.sumslack.freemarker.functions.DateAdd;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.XmlUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ReportUtil {
	public static Map<String,List<ReportBean>> reportCache = new HashMap();
	public static Map<String,List<ApiBean>> apiCache = new HashMap();
	
	public static Map<String,List<ReportBean>> initReports(String fileName){
		if(fileName.equals("all")) {
			FileUtil.listFileNames(ReportJob.REPORT_FILEPATH).forEach(_name -> {
				if( !StrUtil.isEmpty(_name) && !_name.startsWith("#") && _name.endsWith(".xml")) {
					initReportFileName(_name);
				}
			});
		}else {
			initReportFileName(fileName);
		}
		//ClassPathResource resource = new ClassPathResource("report.xml");
		//Document doc = XmlUtil.readXML(resource.getStream());
		
		return reportCache;
	}
	
	
	private static void initReportFileName(String fileName) {
		Document doc = XmlUtil.readXML(ReportJob.REPORT_FILEPATH + fileName);
		Element rootElement = XmlUtil.getRootElement(doc);
		Element reportsElement = XmlUtil.getElement(rootElement, "reports");
		Element apisElement = XmlUtil.getElement(rootElement, "apis");
		if(apisElement!=null) {
			List<Element> apiList = XmlUtil.getElements(apisElement, "api");
			if(apiList!=null) {
				List<ApiBean> apiBeanList = Lists.newArrayList();
				for(Element api : apiList) {
					String _title = StrUtil.formatNullStr(api.getAttribute("title"));
					if(_title.startsWith("//")) continue;
					ApiBean apiBean = new ApiBean();
					apiBean.setId(StrUtil.formatNullStr(api.getAttribute("id")));
					apiBean.setTitle(_title);
					apiBean.setAuth(StrUtil.formatNullStr(api.getAttribute("auth"),"false").equals("true"));
					apiBean.setLang(StrUtil.formatNullStr(api.getAttribute("lang"),"js"));
					apiBean.setType(StrUtil.formatNullStr(api.getAttribute("type")));
					apiBean.setContent(getCDData(api)); 
					apiBeanList.add(apiBean);
				}
				apiCache.put(fileName, apiBeanList);
			}
		}
		if(reportsElement!=null) {
			String dsDefault = StrUtil.formatNullStr(reportsElement.getAttribute("ds"));
			List<Element> reports = XmlUtil.getElements(reportsElement, "report");
			List<ReportBean> reportList = Lists.newArrayList();
			if(reports!=null) {
				for(Element report : reports) {
					String _title = StrUtil.formatNullStr(report.getAttribute("title"));
					if(_title.startsWith("//")) continue;
					ReportBean rp = new ReportBean();
					rp.setId(StrUtil.formatNullStr(report.getAttribute("id")));
					rp.setTitle(_title);
					rp.setSubtitle(report.getAttribute("subtitle"));
					rp.setCache(StrUtil.formatNullStr(report.getAttribute("cache"),"false").equals("true"));
					rp.setDs(report.hasAttribute("ds")?StrUtil.formatNullStr(report.getAttribute("ds")):dsDefault);
					rp.setStartDate(StrUtil.formatNullStr(report.getAttribute("startDate")));
					rp.setEndDate(StrUtil.formatNullStr(report.getAttribute("endDate")));
					rp.setStep(StrUtil.formatNullStr(report.getAttribute("step"),"month"));
					rp.setJava(StrUtil.formatNullStr(report.getAttribute("java")));
					rp.setType(StrUtil.formatNullStr(report.getAttribute("type")));
					rp.setAuth(StrUtil.formatNullStr(report.getAttribute("auth"),"false").equals("true"));
					rp.setDateFormat(StrUtil.formatNullStr(report.getAttribute("dateFormat"),"yyyy-MM"));
					rp.setJavaAlignData(StrUtil.formatNullStr(report.getAttribute("java-align-data"),"false").equals("true"));
					List<Element> lines = XmlUtil.getElements(report, "row");
					if(lines!=null) {
						List<ReportLineBean> lineList = new ArrayList();
						for(Element line : lines) {
							ReportLineBean rl = new ReportLineBean();
							rl.setId(StrUtil.formatNullStr(line.getAttribute("id")));
							rl.setLabel(StrUtil.formatNullStr(line.getAttribute("label"),"未知"));
							rl.setAlign(line.getAttribute("align"));
							rl.setFontWeight(line.getAttribute("fontWeight"));
							rl.setLang(StrUtil.formatNullStr(line.getAttribute("lang"),"sql"));
							
							rl.setShowTotal(StrUtil.formatNullStr(line.getAttribute("show-total"),"false").equals("true"));
							rl.setTotalFormatter(StrUtil.formatNullStr(line.getAttribute("total-format"),"#"));
							
							rl.setFieldField(StrUtil.formatNullStr(line.getAttribute("field-field"),"field"));
							rl.setFieldV(StrUtil.formatNullStr(line.getAttribute("field-v"),"v"));
							rl.setFieldLabel(StrUtil.formatNullStr(line.getAttribute("field-label")));
							
							rl.setContent(getCDData(line)); 
							rl.setDs(rp.getDs());
							lineList.add(rl);
						}
						rp.setLines(lineList);
					}
					reportList.add(rp);
				}
			}
			reportCache.put(fileName,reportList);
		}
	}
	
	public static String parseParam(String str,Map paramMap) throws Exception{
		Configuration cfg = new Configuration();    
        cfg.setTemplateLoader(new StringTemplateLoader(str));
        cfg.setDefaultEncoding("UTF-8");
        Template template = cfg.getTemplate("");
        StringWriter writer = new StringWriter();    
        if(paramMap == null) {
        	paramMap = new HashMap();
        }
        paramMap.put("dateAdd", new DateAdd());
        
        template.process(paramMap, writer);  
        return writer.toString();
	}
	
	public static ReportBean getReport(String fileName,String id) {
		List<ReportBean> reportList = reportCache.get(fileName);
		if(reportList == null) {
			reportList = reportCache.get(fileName + ".xml");
		}
		if(reportList!=null) {
			Optional<ReportBean> _rb = reportList.stream().filter(s -> s.getId().equalsIgnoreCase(id)).findFirst();
			if(_rb.isPresent())
				return _rb.get();
		}
		return null;
	}
	
	public static ApiBean getApi(String fileName,String id) {
		List<ApiBean> reportList = apiCache.get(fileName);
		if(reportList == null) {
			reportList = apiCache.get(fileName + ".xml");
		}
		if(reportList!=null) {
			Optional<ApiBean> _rb = reportList.stream().filter(s -> s.getId().equalsIgnoreCase(id)).findFirst();
			if(_rb.isPresent())
				return _rb.get();
		}
		return null;
	}
	
	public static String getCDData(Element e) {
	    NodeList list = e.getChildNodes();
	    String data;
	    for(int index = 0; index < list.getLength(); index++){
	        if(list.item(index) instanceof CharacterData){
	            CharacterData child = (CharacterData) list.item(index);
	            data = child.getData();

	            if(data != null && data.trim().length() > 0)
	                return child.getData();
	        }
	    }
	    return "";
	}
	
	
	public static void main(String[] args) throws Exception {
		String str = "hello,${test},${dateAdd('2019-09-08',-1,'month')?string('yyyy-MM-dd')}";
		Map paramMap = new HashMap();
		paramMap.put("test", 12);
		System.out.println(ReportUtil.parseParam(str,paramMap));
	}
}
