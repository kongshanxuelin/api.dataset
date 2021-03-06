package com.sumslack.dataset.api.report.vo;

import java.util.List;

public class ReportVO {
	
	public static enum RET {SUCCESS,NOTFOUND,ERROR,NOTAUTH};
	
	private RET ret;
	private String errMsg;
	private String title;//报表标题
	private String subtitle;//报表父标题
	private List<ReportColVO> cols;
	private List<ReportLineVO> rows;
	//当指定了type时，返回的是这个对象
	private Object result;

	public List<ReportColVO> getCols() {
		return cols;
	}

	public void setCols(List<ReportColVO> cols) {
		this.cols = cols;
	}

	public List<ReportLineVO> getRows() {
		return rows;
	}

	public void setRows(List<ReportLineVO> rows) {
		this.rows = rows;
	}

	public RET getRet() {
		return ret;
	}

	public void setRet(RET ret) {
		this.ret = ret;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	
}
