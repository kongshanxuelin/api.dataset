package com.sumslack.dataset.api.report.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.dataset.api.report.vo.ReportColVO;
import com.sumslack.dataset.api.report.vo.ReportLineLabelVO;
import com.sumslack.dataset.api.report.vo.ReportLineVO;

import cn.hutool.core.date.DateUtil;

public class ReportBean implements Serializable{
	private String id;
	private String title;
	private String ds; //数据源
	private String startDate;
	private String endDate;
	private String step;
	private String dateFormat;
	private String java;
	private List<ReportLineBean> lines;
	
	public List<ReportLineBean> getLines() {
		return lines;
	}
	public void setLines(List<ReportLineBean> lines) {
		this.lines = lines;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDs() {
		return ds;
	}
	public void setDs(String ds) {
		this.ds = ds;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public String getJava() {
		return java;
	}
	public void setJava(String java) {
		this.java = java;
	}
	public void init(Map paramMap) {
		for(ReportLineBean rlb : this.lines) {
			try {
				rlb.init(paramMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public List<ReportColVO> getCols(Map paramMap) throws Exception{
		List<ReportColVO> cols = new ArrayList();
		String cStartDate = ReportUtil.parseParam(this.startDate, paramMap);
		String cEndDate = ReportUtil.parseParam(this.endDate, paramMap);
		Date _startDate = DateUtil.parse(cStartDate);
		Date _endDate = DateUtil.parse(cEndDate);
		if(this.step.equals("day")) {
			
		}else if(this.step.equals("year")) {
			
		}else {
			if(_startDate.getTime() < _endDate.getTime()) {
				long mm = DateUtil.betweenMonth(_startDate, _endDate, true);
				for(int i=0;i<mm;i++) {
					Map map = new HashMap();
					ReportColVO col = new ReportColVO();
					if(this.dateFormat!=null) {
						col.setLabel(DateUtil.format(DateUtil.offsetMonth(_startDate, i), this.dateFormat));
					}else {
						col.setLabel(DateUtil.format(DateUtil.offsetMonth(_startDate, i), "yyyy-MM-dd"));
					}
					col.setField(DateUtil.format(DateUtil.offsetMonth(_startDate, i), this.dateFormat));
					cols.add(col);
				}
			}
		}
		return cols;
	}
	
	public List<ReportLineVO> returnReportJSON(Map paramMap) {
		List<ReportLineVO> rows = new ArrayList();
		for(ReportLineBean line : this.lines) {
			ReportLineVO lineVO = new ReportLineVO();
			ReportLineLabelVO label = new ReportLineLabelVO(line.getId(),line.getLabel(),line.getAlign(),line.getFontWeight());
			lineVO.setLabel(label);
			try {
				line.init(paramMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			lineVO.setData(line.getResultList());
			rows.add(lineVO);
		}
		return rows;
	}
}
