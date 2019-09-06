package com.sumslack.dataset.api.report.vo;

import java.io.Serializable;

public class ReportColVO implements Serializable{
	private String label;
	private String field;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	
	public ReportColVO() {}
	public ReportColVO(String field,String label) {
		this.field = field;
		this.label = label;
	}
	
}
