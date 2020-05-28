package com.sumslack.dataset.api.report.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.compress.utils.Lists;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.alibaba.druid.pool.DruidDataSource;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.ApiBean;
import com.sumslack.dataset.api.report.bean.DatasourceBean;
import com.sumslack.dataset.api.report.bean.ReportBean;
import com.sumslack.dataset.api.report.bean.ReportLineBean;
import com.sumslack.dataset.api.report.job.ReportJob;
import com.sumslack.freemarker.functions.DateAdd;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.XmlUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ReportUtil {
	private static final Element _dsEle = null;
	public static Map<String,List<ReportBean>> reportCache = new HashMap();
	public static Map<String,List<ApiBean>> apiCache = new HashMap();
	public static Map<String,List<DatasourceBean>> dsCache = new HashMap();
	
	public static List<DatasourceBean> getDatasourceCache(String name){
		if(dsCache.containsKey(name)) {
			return dsCache.get(name);
		}else if(dsCache.containsKey(name+".xml")) {
			return dsCache.get(name+".xml");
		}
		return null;
	}
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
		System.out.println("##############正在缓存：" + fileName);
		Document doc = XmlUtil.readXML(ReportJob.REPORT_FILEPATH + fileName);
		Element rootElement = XmlUtil.getRootElement(doc);
		Element reportsElement = XmlUtil.getElement(rootElement, "reports");
		Element apisElement = XmlUtil.getElement(rootElement, "apis");
		Element datasourcesElement = XmlUtil.getElement(rootElement, "datasources");
		//数据源定义
		if(datasourcesElement!=null) {
			List<Element> dsList = XmlUtil.getElements(datasourcesElement, "datasource");
			if(dsList!=null) {
				List<DatasourceBean> dsBeanList = Lists.newArrayList();
				for(Element _dsEle : dsList) {
					List<Element> propertyList = XmlUtil.getElements(_dsEle, "property");
					if(propertyList!=null) {
						Optional<Element> driverEle = propertyList.stream().filter(s -> Convert.toStr(s.getAttribute("name"),"").equals("driver")).findFirst();
						Optional<Element> urlEle = propertyList.stream().filter(s -> Convert.toStr(s.getAttribute("name"),"").equals("url")).findFirst();
						Optional<Element> userEle = propertyList.stream().filter(s -> Convert.toStr(s.getAttribute("name"),"").equals("user")).findFirst();
						Optional<Element> passEle = propertyList.stream().filter(s -> Convert.toStr(s.getAttribute("name"),"").equals("password")).findFirst();
						DatasourceBean _ds  = new DatasourceBean();
						_ds.setDriver(driverEle.isPresent()?Convert.toStr(driverEle.get().getAttribute("value")):null);
						_ds.setUrl(urlEle.isPresent()?Convert.toStr(urlEle.get().getAttribute("value")):null);
						_ds.setUser(userEle.isPresent()?Convert.toStr(userEle.get().getAttribute("value")):null);
						_ds.setPassword(passEle.isPresent()?Convert.toStr(passEle.get().getAttribute("value")):null);
						_ds.setName(Convert.toStr(_dsEle.getAttribute("name")));
						dsBeanList.add(_ds);
					}
				}
				dsCache.put(fileName, dsBeanList);
			}
		}
		//API接口定义
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
					apiBean.setUiField(StrUtil.formatNullStr(api.getAttribute("ui-field")));
					apiBean.setUiTitle(StrUtil.formatNullStr(api.getAttribute("ui-title")));
					apiBean.setUiHeader(Convert.toStr(api.getAttribute("ui-header"),""));
					apiBean.setUiFooter(Convert.toStr(api.getAttribute("ui-footer"),""));
					apiBean.setContent(getCDData(api));
//					Map<String,DataSource> datasources = new HashMap();
//					if(datasourcesElement!=null) {
//						List<DatasourceBean> dsBeanList = dsCache.get(fileName);
//						dsBeanList.stream().forEach(dsBean -> {
//							DruidDataSource ds2 = new DruidDataSource();
//							ds2.setUrl(dsBean.getUrl());
//							ds2.setUsername(dsBean.getUser());
//							ds2.setPassword(dsBean.getPassword());
//							datasources.put(dsBean.getName(), ds2);
//						});
//					}
//					apiBean.setDatasources(datasources);
					if(api.hasAttribute("ds")) {
						apiBean.setDs(Convert.toStr(api.getAttribute("ds")));	
					}else {
						apiBean.setDs(Convert.toStr(apisElement.getAttribute("ds"),""));
					}
					apiBeanList.add(apiBean);
				}
				apiCache.put(fileName, apiBeanList);
			}
		}
		//数据集定义
		if(reportsElement!=null) {
			String dsDefault = StrUtil.formatNullStr(reportsElement.getAttribute("ds"));
			List<Element> reports = XmlUtil.getElements(reportsElement, "report");
			List<ReportBean> reportList = Lists.newArrayList();
			if(reports!=null) {
				for(Element report : reports) {
					String _title = StrUtil.formatNullStr(report.getAttribute("title"));
					if(_title.startsWith("//")) continue;
					ReportBean rp = new ReportBean();
					rp.setReports(reportsElement);
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
