package com.sumslack.dataset.api.report.vo;

import java.util.List;

public class ReportVO {
	
	public static enum RET {SUCCESS,NOTFOUND};
	
	private RET ret;
	private String errMsg;
	private List<ReportColVO> cols;
	private List<ReportLineVO> rows;

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
	
	
}
