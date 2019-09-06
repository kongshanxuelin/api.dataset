package com.sumslack.dataset.api.report.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.CharacterData;

import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.ReportBean;
import com.sumslack.dataset.api.report.bean.ReportLineBean;
import com.sumslack.dataset.api.report.job.ReportJob;

import cn.hutool.core.util.XmlUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ReportUtil {
	public static List<ReportBean> reportList = null;
	public static List<ReportBean> initReports(){
		reportList = new ArrayList();
		//ClassPathResource resource = new ClassPathResource("report.xml");
		//Document doc = XmlUtil.readXML(resource.getStream());
		Document doc = XmlUtil.readXML(ReportJob.REPORT_FILEPATH);
		Element rootElement = XmlUtil.getRootElement(doc);
		String dsDefault = StrUtil.formatNullStr(rootElement.getAttribute("ds"));
		List<Element> reports = XmlUtil.getElements(rootElement, "report");
		if(reports!=null) {
			for(Element report : reports) {
				ReportBean rp = new ReportBean();
				rp.setId(StrUtil.formatNullStr(report.getAttribute("id")));
				rp.setTitle(StrUtil.formatNullStr(report.getAttribute("title")));
				rp.setDs(report.hasAttribute("ds")?StrUtil.formatNullStr(report.getAttribute("ds")):dsDefault);
				rp.setStartDate(StrUtil.formatNullStr(report.getAttribute("startDate")));
				rp.setEndDate(StrUtil.formatNullStr(report.getAttribute("endDate")));
				rp.setStep(StrUtil.formatNullStr(report.getAttribute("step"),"month"));
				rp.setJava(StrUtil.formatNullStr(report.getAttribute("java")));
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
						rl.setSql(getCDData(line)); 
						rl.setDs(rp.getDs());
						lineList.add(rl);
					}
					rp.setLines(lineList);
				}
				reportList.add(rp);
			}
		}
		return reportList;
	}
	
	
	public static String parseParam(String str,Map paramMap) throws Exception{
		Configuration cfg = new Configuration();    
        cfg.setTemplateLoader(new StringTemplateLoader(str));    
        cfg.setDefaultEncoding("UTF-8");
        Template template = cfg.getTemplate("");
        StringWriter writer = new StringWriter();    
        template.process(paramMap, writer);  
        return writer.toString();
	}
	
	public static ReportBean getReport(String id) {
		if(reportList!=null) {
			Optional<ReportBean> _rb = reportList.stream().filter(s -> s.getId().equalsIgnoreCase(id)).findFirst();
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
}
