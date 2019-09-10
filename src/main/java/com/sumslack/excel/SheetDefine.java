package com.sumslack.excel;

import java.util.List;

public class SheetDefine {
	 private String sheetName;
     private String datasource;
     private String tableName;
     private int beginRow;
     private String primaryKey;
     private String filter;
     private Boolean insert;
     
	private List<FieldMapping> fields;
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public int getBeginRow() {
		return beginRow;
	}
	public void setBeginRow(int beginRow) {
		this.beginRow = beginRow;
	}
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public Boolean getInsert() {
		return insert;
	}
	public void setInsert(Boolean insert) {
		this.insert = insert;
	}
	public List<FieldMapping> getFields() {
		return fields;
	}
	public void setFields(List<FieldMapping> fields) {
		this.fields = fields;
	}
     
}
