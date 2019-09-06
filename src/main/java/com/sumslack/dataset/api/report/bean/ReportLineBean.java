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

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;

public class ReportLineBean implements Serializable{
	private String label;
	private String align;
	private String fontWeight;
	private String id;
	private String sql;
	private String ds;
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
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
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
	public void init(ReportBean report,Map paramMap) throws Exception{
		if(this.sql!=null) {
			List<Map> dataList = TagJDBCInstance.getInstance().queryList(this.ds,
					ReportUtil.parseParam(this.sql, paramMap),
					null);
			if(report.isJavaAlignData()) {
				if(report!=null) {
					List<ReportColVO> cols = report.getCols(paramMap);
					this.resultList = new ArrayList();
					for(ReportColVO col : cols) {
						Map m = new HashMap();
						m.put("field", col.getField());
						Optional<Map> vv = dataList.stream().filter(s -> {
							return Convert.toStr(s.get("field")).equals(col.getField());
						}).findFirst();
						m.put("v", vv.isPresent()?vv.get().get("v"):null);
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
