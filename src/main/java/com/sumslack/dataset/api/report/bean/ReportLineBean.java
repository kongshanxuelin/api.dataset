package com.sumslack.dataset.api.report.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sumscope.tag.sql.TagJDBCInstance;
import com.sumscope.tag.util.IdWorker;
import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.dataset.api.report.vo.ReportColVO;
import com.sumslack.excel.JSUtil;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;

public class ReportLineBean implements Serializable{
	private String label;
	private String align;
	private String fontWeight;
	private String id;
	private String content;
	private String ds;
	private String lang;
	
	private boolean showTotal = false; //是否显示汇总行
	private String totalFormatter = "#"; 
	
	//针对日期对应的字段名称，默认值：field，针对数据对应的字段名称
	private String fieldField = "field";  //曲线x坐标
	private String fieldV = "v";		//曲线Y坐标
	private String fieldLabel; //曲线名称
	
	public String getTotalFormatter() {
		return totalFormatter;
	}
	public void setTotalFormatter(String totalFormatter) {
		this.totalFormatter = totalFormatter;
	}
	//可以直接返回JS对象
	private Object result;
	
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public String getFieldLabel() {
		return fieldLabel;
	}
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	public String getFieldField() {
		return fieldField;
	}
	public void setFieldField(String fieldField) {
		this.fieldField = fieldField;
	}
	public String getFieldV() {
		return fieldV;
	}
	public void setFieldV(String fieldV) {
		this.fieldV = fieldV;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	private List resultList;
	public String getDs() {
		return ds;
	}
	public void setDs(String ds) {
		this.ds = ds;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
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

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		if(StrUtil.isEmpty(id)) {
			return IdWorker.getInstance().uuid();
		}else {
			return id;
		}
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public static Cache<String, List> resultCache = CacheUtil.newLFUCache(3000);
	
	private String getCacheKey(ReportBean report,String key) {
		return Convert.toStr(report.getId()) + "-" + key.hashCode();
	}
	
	public boolean isShowTotal() {
		return showTotal;
	}
	public void setShowTotal(boolean showTotal) {
		this.showTotal = showTotal;
	}
	public void init(ReportBean report,Map paramMap) throws Exception{
		if(this.content!=null) {
			List<Map> dataList = null;
			if(this.lang.equals("js")) {
				//content可能有网页参数
				String _content = ReportUtil.parseParam(this.content, paramMap);
				Object res = JSUtil.execRetList("(function(){"+_content+"})()",paramMap);
				if(res!=null && res instanceof List)
					dataList = (List)res;
				else
					this.result = res;
			}else {
				/**
				 * SQL执行后的数据集可能有label，field和数据三个字段，需要转换成：{label:{title:"",id:""},data:[{field:"",v:""}]}的形式用于前端展现
				 */
				String _content = ReportUtil.parseParam(this.content, paramMap);
				if(report.isCache()) {
					String _cacheKey = getCacheKey(report,_content);
					if(resultCache.containsKey(_cacheKey)) {
						dataList = resultCache.get(_cacheKey);
					}else {
						dataList = TagJDBCInstance.getInstance().queryList(this.ds,
								_content,
								null);
						resultCache.put(_cacheKey, dataList,3600*1000);
					}
				}else {
					dataList = TagJDBCInstance.getInstance().queryList(this.ds,
							_content,
							null);
				}
			}
			if(report.isJavaAlignData() && StrUtil.isBlankIfStr(this.fieldLabel)) {
				if(report!=null) {
					List<ReportColVO> cols = report.getCols(paramMap);
					this.resultList = new ArrayList();
					for(ReportColVO col : cols) {
						Map m = new HashMap();
						m.put("field", col.getField());
						Optional<Map> vv = dataList.stream().filter(s -> {
							if(s instanceof Entity) {
								if(((Entity) s).getStr(this.fieldField)!=null)
									return ((Entity) s).getStr(this.fieldField).equals(col.getField());
							}else {
								return Convert.toStr(s.get(this.fieldField)).equals(col.getField());
							}
							return true;
						}).findFirst();
						m.put("v", vv.isPresent()?vv.get().get(this.fieldV):null);
						this.resultList.add(m);
					}
				}
			}else {
				this.resultList = dataList;
			}
			
		}
	}
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	
}
