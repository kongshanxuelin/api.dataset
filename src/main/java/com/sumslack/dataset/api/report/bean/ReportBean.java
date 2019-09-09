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

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

public class ReportBean implements Serializable{
	private String id;
	private String title;
	private String ds; //数据源
	private String startDate;
	private String endDate;
	private String step;
	private String dateFormat;
	private String java;
	//指定返回的数据类型，默认为：{cols:[...],rows:[...]}的形式，如果指定为map，则一个report代码一个key
	private String type; 
	//是否使用java对齐多个数据集中的数据
	private boolean javaAlignData;
	private List<ReportLineBean> lines;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
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
	
	public boolean isJavaAlignData() {
		return javaAlignData;
	}
	public void setJavaAlignData(boolean javaAlignData) {
		this.javaAlignData = javaAlignData;
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
				rlb.init(this,paramMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 根据传入的开始日期和结束日期，计算动态列
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<ReportColVO> getCols(Map paramMap) throws Exception{
		List<ReportColVO> cols = new ArrayList();
		String cStartDate = ReportUtil.parseParam(this.startDate, paramMap);
		String cEndDate = ReportUtil.parseParam(this.endDate, paramMap);
		//未设置开始时间，那么返回空的动态列
		if(StrUtil.isEmpty(cStartDate)) {
			return null;
		}
		if(!StrUtil.isEmpty(cStartDate) && StrUtil.isEmpty(cEndDate)) {
			cEndDate = DateUtil.format(new Date(), "yyyy-MM-dd");
		}
		
		Date _startDate = DateUtil.parse(cStartDate);
		Date _endDate = DateUtil.parse(cEndDate);
		if(this.step.equals("day")) {
			if(_startDate.getTime() < _endDate.getTime()) {
				long mm = DateUtil.between(_startDate, _endDate, DateUnit.DAY);
				for(int i=0;i<=mm;i++) {
					Map map = new HashMap();
					ReportColVO col = new ReportColVO();
					if(this.dateFormat!=null) {
						col.setLabel(DateUtil.format(DateUtil.offsetDay(_startDate, i), this.dateFormat));
					}else {
						col.setLabel(DateUtil.format(DateUtil.offsetDay(_startDate, i), "yyyy-MM-dd"));
					}
					col.setField(DateUtil.format(DateUtil.offsetDay(_startDate, i), this.dateFormat));
					cols.add(col);
				}
			}
		}else if(this.step.equals("year")) {
			if(_startDate.getTime() < _endDate.getTime()) {
				long mm = DateUtil.betweenYear(_startDate, _endDate, false);
				for(int i=0;i<=mm;i++) {
					Map map = new HashMap();
					ReportColVO col = new ReportColVO();
					if(this.dateFormat!=null) {
						col.setLabel(DateUtil.format(DateUtil.offsetMonth(_startDate, 12*i), this.dateFormat));
					}else {
						col.setLabel(DateUtil.format(DateUtil.offsetMonth(_startDate, 12*i), "yyyy-MM-dd"));
					}
					col.setField(DateUtil.format(DateUtil.offsetMonth(_startDate, 12*i), this.dateFormat));
					cols.add(col);
				}
			}
		}else if(this.step.equals("week")) {
			if(_startDate.getTime() < _endDate.getTime()) {
				long mm = DateUtil.between(_startDate, _endDate, DateUnit.WEEK);
				for(int i=0;i<=mm;i++) {
					Map map = new HashMap();
					ReportColVO col = new ReportColVO();
					if(this.dateFormat!=null) {
						col.setLabel(DateUtil.format(DateUtil.offsetWeek(_startDate, i), this.dateFormat));
					}else {
						col.setLabel(DateUtil.format(DateUtil.offsetWeek(_startDate, i), "yyyy-MM-dd"));
					}
					col.setField(DateUtil.format(DateUtil.offsetWeek(_startDate, i), this.dateFormat));
					cols.add(col);
				}
			}
		}else {
			if(_startDate.getTime() < _endDate.getTime()) {
				long mm = DateUtil.betweenMonth(_startDate, _endDate, false);
				for(int i=0;i<=mm;i++) {
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
			lineVO.setData(line.getResultList());
			rows.add(lineVO);
		}
		return rows;
	}
	
	public Object returnReportType(Map paramMap) {
		if(this.getType().equals("map")) {
			Map retMap = new HashMap();
			for(ReportLineBean line : this.lines) {
				ReportLineVO lineVO = new ReportLineVO();
				if(line.getResultList()!=null && line.getResultList().size()==1) {
					retMap.put(line.getId(),line.getResultList().get(0));
				}
			}
			return retMap;
		}
		return null;
	}
}
