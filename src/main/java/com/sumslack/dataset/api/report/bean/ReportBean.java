package com.sumslack.dataset.api.report.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;

import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.dataset.api.report.vo.ReportColVO;
import com.sumslack.dataset.api.report.vo.ReportLineLabelVO;
import com.sumslack.dataset.api.report.vo.ReportLineVO;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.db.Entity;

public class ReportBean implements Serializable{
	private String id;
	private String title;
	private String subtitle;
	private boolean cache = false;//是否缓存记录集
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
	
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public boolean isCache() {
		return cache;
	}
	public void setCache(boolean cache) {
		this.cache = cache;
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
	
	public List<ReportLineVO> returnReportJSON(Map paramMap) throws Exception{
		List<ReportLineVO> rows = new ArrayList();
		for(ReportLineBean line : this.lines) {
			if(cn.hutool.core.util.StrUtil.isBlankIfStr(line.getFieldLabel())) {
				ReportLineVO lineVO = new ReportLineVO();
				ReportLineLabelVO label = new ReportLineLabelVO(line.getId(),line.getLabel(),line.getAlign(),line.getFontWeight());
				lineVO.setLabel(label);
				lineVO.setData(line.getResultList());
				rows.add(lineVO);
			}else { //如果一条曲线，在SQL中被定义了label属性，说明该SQL或存储过程返回的是多条曲线
				//对返回的数据集再处理,这时候返回的是原始数据集，select label,field,v的形式
				List<Map> dataList = line.getResultList();
				List<String> _lables = new ArrayList();
				dataList.stream().map(s -> Convert.toStr(s.get(line.getFieldLabel()))).filter(s -> !StrUtil.isEmpty(s)).distinct()
				.forEach(s -> {
					_lables.add(s);
				});
				
				for(String _label : _lables) {
					ReportLineVO lineVO = new ReportLineVO();
					ReportLineLabelVO label = new ReportLineLabelVO(line.getId(),_label,line.getAlign(),line.getFontWeight());
					lineVO.setLabel(label);
					
					List<Map> _thisLabeDatalList = dataList.stream().filter(s -> Convert.toStr(s.get(line.getFieldLabel())).equals(_label))
							.collect(Collectors.toList());
					List<Map> thisDataList = new ArrayList();
					for(ReportColVO col : this.getCols(paramMap)) {
						Map m = new HashMap();
						m.put("field", col.getField());
						Optional<Map> vv = _thisLabeDatalList.stream().filter(s -> {
							if(s instanceof Entity) {
								if(((Entity) s).getStr(line.getFieldField())!=null)
									return ((Entity) s).getStr(line.getFieldField()).equals(col.getField());
							}else {
								return Convert.toStr(s.get(line.getFieldField())).equals(col.getField());
							}
							return true;
						}).findFirst();
						m.put("v", vv.isPresent()?vv.get().get(line.getFieldV()):null);
						thisDataList.add(m);
					}
					lineVO.setData(thisDataList);
					rows.add(lineVO);
				}
				//是否需要汇总
				renderDataList(rows,line);
			}
		}
		return rows;
	}
	
	private void renderDataList(List<ReportLineVO> lines,ReportLineBean line) {
		if(line.isShowTotal() && lines.size()>0) {
			ReportLineVO totalLine = new ReportLineVO();
			ReportLineLabelVO label = new ReportLineLabelVO();
			label.setTitle("Total");
			totalLine.setLabel(label);
			List data = Lists.newArrayList();
			
			ReportLineVO _first = lines.get(0);
			List<Map> _dataList = _first.getData();
			List<String> cols = _dataList.stream().map(item -> StrUtil.formatNullStr(item.get("field"))).collect(Collectors.toList());
			
			for(String col:cols) {
				Map map = new HashMap();
				//TODO:这里不对
				double dd = lines.stream().map(_line -> {
					List<Map> itemList = (List)_line.getData();
					Optional<Map> _map = itemList.stream().filter(s -> StrUtil.formatNullStr(s.get("field")).equals(col) && NumberUtil.isNumber(StrUtil.formatNullStr(s.get("v")))).findFirst();
					Map _defaultMap = new HashMap();
					_defaultMap.put("field", col);
					_defaultMap.put("v", 0);
					return _map.isPresent()?_map.get():_defaultMap;
				}).mapToDouble(item -> {
					return Convert.toDouble(item.get("v"));
				})
				.sum();
				map.put("field", col);
				map.put(col, NumberUtil.decimalFormat(line.getTotalFormatter(), dd));
				data.add(map);
			}
			totalLine.setData(data);
			lines.add(totalLine);
		}
	}
	
	public Object returnJSResult(Map paramMap) {
		Map retMap = new HashMap();
		for(ReportLineBean line : this.lines) {
			ReportLineVO lineVO = new ReportLineVO();
			if(line.getResult()!=null) {
				retMap.put(line.getId(),line.getResult());
			}
		}
		return retMap;
	}
}
