package com.sumslack.dataset.api.report.vo;

public class ReportLineLabelVO {
	private String id;
	private String title;
	private String align;
	private String fontWeight;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getFontWeight() {
		return fontWeight;
	}
	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}
	public ReportLineLabelVO(String id,String title,String align,String fontWeight) {
		this.id = id;
		this.title = title;
		this.align= align;
		this.fontWeight=fontWeight;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ReportLineLabelVO() {}
}
