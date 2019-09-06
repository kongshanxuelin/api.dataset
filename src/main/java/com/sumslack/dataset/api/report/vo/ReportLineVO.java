package com.sumslack.dataset.api.report.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sumscope.tag.sql.bean.KV;

public class ReportLineVO {
	private ReportLineLabelVO label;
	private List data;
	public ReportLineLabelVO getLabel() {
		return label;
	}
	public void setLabel(ReportLineLabelVO label) {
		this.label = label;
	}
	public List getData() {
		return data;
	}
	public void setData(List data) {
		this.data = data;
	}
	
	public ReportLineVO() {}
	public ReportLineVO(String title,List data) {
		ReportLineLabelVO vv = new ReportLineLabelVO();
		vv.setTitle(title);
		this.label = vv;
		this.data = data;
	}
	
	public ReportLineVO(String title,KV... kvs) {
		ReportLineLabelVO vv = new ReportLineLabelVO();
		vv.setTitle(title);
		this.label = vv;
		List datas = new ArrayList();
		for(int i=0;i<kvs.length;i++) {
			Map m = new HashMap();
			m.put("field", kvs[i].getName());
			m.put("v", kvs[i].getValue());
			datas.add(m);
		}
		this.data = datas;
	}
	

}
