package com.sumslack.dataset.api.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sumscope.tag.TagConst;
import com.sumscope.tag.listener.AppInit;
import com.sumscope.tag.sql.TagJDBCInstance;
import com.sumscope.tag.util.FileManager;
import com.sumscope.tag.util.StrUtil;

public class GenDao {
	final static Logger logger = LogManager.getLogger(GenDao.class);
	//数据库名
	private static final String schema = "db_jsptagex";
	//数据源
	private static final String datasource = "default";
	//DAO所在package
	private static final String pack = "com.company.dao";
	//生成源码的路径
	private static final String basePath = "D:/workspace_maven_jsptagEx/JSPTagExWeb/src/main/java/com/company/dao";
	//仅生成某个表的DAO时用
	private static String[] tablesOnly = null;//new String[]{"test3"};
	//是否生成getter和setter方法
	private static final boolean isGenColumn = true;//是否生成getter和setter方法
	
	
	public static void gen()  throws Exception
	{
		String sql = "select table_name from INFORMATION_SCHEMA.TABLES where table_schema=?";
		List tables = TagJDBCInstance.getInstance().queryList(sql, new Object[]{schema});
		if(tables!=null){
			for(int i=0;i<tables.size();i++){
				StringBuffer sb = new StringBuffer();
				Map m = (Map)tables.get(i);
				String _tableName = StrUtil.formatNullStr(m.get("table_name"));
				if(tablesOnly!=null && tablesOnly.length>0){
					if(!ArrayUtils.contains(tablesOnly, _tableName)) continue;
				}
				sb.append("package " + pack).append(";\r\n");
				sb.append("import com.sumscope.tag.util.db.activerecord.Model;").append("\r\n");
				sb.append("import com.sumscope.tag.util.db.activerecord.annotation.PrimaryKey;").append("\r\n");
				sb.append("import com.sumscope.tag.util.db.activerecord.annotation.Table;").append("\r\n");
				if(!datasource.equals(TagConst.DEFAULT.DATASOURCE)){
					sb.append("import com.sumscope.tag.util.db.activerecord.annotation.DataSource;").append("\r\n");
				}
				sql = "select column_name from INFORMATION_SCHEMA.COLUMNS where table_schema=? and table_name=? and column_key='PRI'";
				List priList = TagJDBCInstance.getInstance().queryList(sql, new Object[]{schema,_tableName});
				String _id = "";
				if(priList!=null){
					for(int j=0;j<priList.size();j++){
						_id += "," + StrUtil.formatNullStr(((Map)priList.get(j)).get("column_name"));
					}
					_id = _id.substring(1);
				}else{
					throw new RuntimeException("表 [ "+ _tableName + " ] 必须要有个主键字段!");
				}
				
				sb.append("@PrimaryKey(\""+_id+"\")").append("\r\n");
				sb.append("@Table(\""+_tableName+"\")").append("\r\n");
				if(!datasource.equals(TagConst.DEFAULT.DATASOURCE)){
					sb.append("@DataSource(\""+datasource+"\")").append("\r\n");
				}
				String _clsName = StrUtil.firstCharToUpperCase(_tableName) + "DAO";
				sb.append("public class "+_clsName+" extends Model<"+_clsName+">{").append("\r\n");
				sb.append("\tpublic static "+_clsName+" dao = new "+_clsName+"();").append("\r\n");
				
				if(isGenColumn){
					sql = "select column_name,data_type,numeric_precision,numeric_scale from INFORMATION_SCHEMA.COLUMNS where table_schema=? and table_name=?";
					List colList = TagJDBCInstance.getInstance().queryList(sql, new Object[]{schema,_tableName});
					String getterSetter = genGetterSetter(_tableName,colList);
					sb.append(getterSetter);
				}
				sb.append("}").append("\r\n");
				String _filePath = basePath+"/"+_clsName+".java";
				FileManager.getInstance().writeFile(sb.toString(), _filePath, "UTF-8");
				logger.info(StringUtils.repeat("#", 40) + "gen java file:" + _filePath + "   ok!");
			}
		}
	}
	
	private static String genGetterSetter(String tableName,List colList)
	{
		StringBuffer sb = new StringBuffer();
		if(colList!=null && colList.size()>1){
			for(int i=0;i<colList.size();i++){
				Map m = (Map)colList.get(i);
				String _column_name = StrUtil.formatNullStr(m.get("column_name"));
				String data_type = StrUtil.formatNullStr(m.get("data_type"));
				String numeric_precision = StrUtil.formatNullStr(m.get("numeric_precision"));
				String numeric_scale = StrUtil.formatNullStr(m.get("numeric_scale"));
				if(data_type.equals("tinyint") || data_type.equals("smallint") || data_type.equals("mediumint") || data_type.equals("int") || data_type.equals("integer") || data_type.equals("bigint")){
					sb.append(getSetFunc(tableName,_column_name,"int"));
				}else if(data_type.equals("float")){
					sb.append(getSetFunc(tableName,_column_name,"float"));
				}else if(data_type.equals("double") || data_type.equals("real") || data_type.equals("decimal") || data_type.equals("numeric")){
					sb.append(getSetFunc(tableName,_column_name,"double"));
				}else if(data_type.equals("bit")){
					sb.append(getSetFunc(tableName,_column_name,"byte"));
				}else if(data_type.equals("datetime")){
					sb.append(getSetFunc(tableName,_column_name,"java.util.Date"));
				}else if(data_type.equals("timestamp")){
					sb.append(getSetFunc(tableName,_column_name,"java.sql.Timestamp"));
				}else{
					sb.append(getSetFunc(tableName,_column_name,"String"));
				}
			}
		}
		return sb.toString();
	}
	
	private static String getSetFunc(String tableName,String colName,String type)
	{
		if(colName.length()<1) return "";
		String clsName = StrUtil.firstCharToUpperCase(tableName)+"DAO";
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n").append("\tpublic "+type+" get"+StrUtil.firstCharToUpperCase(colName)+"(){");
		
		sb.append("\r\n").append("\t\treturn ("+type+")get(\"" + colName + "\");");
			
		sb.append("\r\n").append("}");

		sb.append("\r\n").append("\tpublic "+clsName+" set"+StrUtil.firstCharToUpperCase(colName)+"("+type+" v){");
		sb.append("\r\n").append("\t\tset(\""+colName+"\",v);");
		sb.append("\r\n").append("\t\treturn this;");
		sb.append("\r\n").append("}");
		
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		AppInit.instance().run();
		gen();
	}
}
