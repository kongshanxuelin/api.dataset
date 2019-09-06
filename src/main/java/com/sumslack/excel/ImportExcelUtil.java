package com.sumslack.excel;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sumscope.tag.TagConst;
import com.sumscope.tag.sql.bean.AbsDataSource;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.sql.SqlUtil;
import cn.hutool.db.transaction.TxFunc;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

public class ImportExcelUtil {
	private static Log log = LogFactory.get();

	public R importExcel(InputStream filedDta) {
		try {
			// 获取模板定义
			List<SheetDefine> sheetDefineList = getExcelDefine(filedDta);
			if (sheetDefineList == null || sheetDefineList.isEmpty()) {
				return R.error("导入失败，请检查模板定义是否正确。");
			}
			// 根据模板定义，解析Excel并且入库
			R ret = saveExcelBySheetDefine(sheetDefineList, filedDta);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return R.error("导入失败").put("error", ExceptionUtil.stacktraceToString(e));
		}
	}

	private R saveExcelBySheetDefine(List<SheetDefine> sheetDefineList, InputStream fileData) {
		for (SheetDefine sheetDefine : sheetDefineList) {
			// 解析sheet，并且入库
			R error = handleSheet(sheetDefine, fileData);
			if (ObjectUtil.isNotNull(error)) {
				return error;
			}
		}
		return R.ok();
	}

	 private AbsDataSource getDruidDataSource(SheetDefine sheetDefine) {
		 return TagConst.dataSourceMap.get(sheetDefine.getDbType());
	    }
	 
	private R handleSheet(SheetDefine sheetDefine, InputStream fileData) {
		AbsDataSource ds = null;
		try {
			ds = getDruidDataSource(sheetDefine);

			// 如果excel定义模板字段多于表字段，则往表里添加字段
			alterTable(sheetDefine, ds);

			// excel解析入库
			saveExcel(sheetDefine, fileData, ds);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return R.error("导入失败").put("error", ExceptionUtil.stacktraceToString(e));
		}

	}

	/**
	 * 获取模板定义信息
	 *
	 * @param inputStream
	 * @return
	 */
	public List<SheetDefine> getExcelDefine(InputStream inputStream) {
		ExcelReader reader = ExcelUtil.getReader(inputStream, "模板定义");

		List<List<Object>> defineList = reader.read();

		List<SheetDefine> retList = new ArrayList<>();
		SheetDefine sheetDefine = new SheetDefine();
		for (List<Object> row : defineList) {
			if (ObjectUtil.isNotNull(row.get(0))) {
				sheetDefine = new SheetDefine();
				sheetDefine.setSheetName(Convert.toStr(row.get(0)));
				List<FieldMapping> fields = new ArrayList<>();
				sheetDefine.setFields(fields);

				retList.add(sheetDefine);
			} else {
				if (row.size() == 3) {
					// 属性设置
					String col = Convert.toStr(row.get(1));
					String col2 = Convert.toStr(row.get(2));
					if (!col.equalsIgnoreCase("fields")) {
						if (col.equalsIgnoreCase("dbType")) {
							sheetDefine.setDbType(col2);
						} else if (col.equalsIgnoreCase("tableName")) {
							sheetDefine.setTableName(col2);
						} else if (col.equalsIgnoreCase("beginRow")) {
							sheetDefine.setBeginRow(Convert.toInt(col2, 1));
						} else if (col.equalsIgnoreCase("primaryKey")) {
							sheetDefine.setPrimaryKey(col2);
						} else if (col.equalsIgnoreCase("filter")) {
							sheetDefine.setFilter(col2);
						} else if (col.equalsIgnoreCase("insert")) {
							sheetDefine.setInsert(Convert.toBool(col2));
						}
					} else {
						// ignore
					}
				} else if (row.size() >= 4) {
					// 设置列映射
					List<FieldMapping> fields = sheetDefine.getFields();
					FieldMapping field = new FieldMapping();
					field.setRuleField(Convert.toStr(row.get(2)));
					field.setTableField(Convert.toStr(row.get(3)));
					if (row.size() == 5) {
						field.setUpdate(!Convert.toStr(row.get(4)).equalsIgnoreCase("noUpdate"));
					} else {
						field.setUpdate(true);
					}
					fields.add(field);
				} else {
				}
			}
		}
		return retList;
	}

	private void alterTable(SheetDefine sheetDefine, AbsDataSource ds) throws SQLException {
        //查询表结构
        List<Entity> columnList = DbUtil.use(ds.getDataSource()).query("select column_name, column_comment,column_type from information_schema.columns where  table_name = '" + sheetDefine.getTableName() + "'");
        if (CollUtil.isEmpty(columnList)) {
            throw new RuntimeException("表【" + sheetDefine.getTableName() + "】不存在！");
        }

        //比对excel模板字段和表字段是否一致
        List<FieldMapping> fieldMappingList = sheetDefine.getFields();
        List<String> needAddList = new ArrayList<>();
        for (FieldMapping field : fieldMappingList) {
            boolean needAdd = true;
            for (Entity entity : columnList) {
                String colName = entity.getStr("column_name");
                if (field.getTableField().equalsIgnoreCase(colName)) {
                    needAdd = false;
                    continue;
                }
            }
            if (needAdd) {
                needAddList.add("alter table " + sheetDefine.getTableName() + " add  " + field.getTableField() + " varchar(255)");
            }
        }
        //同步表结构
        for (String sql : needAddList) {
            log.info("alter table SQL --> {}", sql);
            DbUtil.use(ds.getDataSource()).execute(sql);
        }
    }

	private void saveExcel(SheetDefine sheetDefine, InputStream fileData, AbsDataSource ds) throws IOException, SQLException {
        //获取模板中定义的sheet
        String sheetName = sheetDefine.getSheetName();
        ExcelReader reader;
        if (NumberUtil.isNumber(sheetName)) {
            reader = ExcelUtil.getReader(fileData, Convert.toInt(sheetName));
        } else {
            reader = ExcelUtil.getReader(fileData, sheetName);
        }
        //解析
        List<List<Object>> reader2 = reader.read();

        Dict sqlMap = new Dict();
        final List<Object[]> insertParamsList = new ArrayList<>();
        final List<Object[]> updateParamsList = new ArrayList<>();
        for (int rowIndex = (sheetDefine.getBeginRow() - 1); rowIndex < reader2.size(); rowIndex++) {
            List<Object> row = reader2.get(rowIndex);

            if (StrUtil.isNotBlank(sheetDefine.getFilter())) {
                //过滤
                Boolean filter = Convert.toBool(JSUtil.execute(sheetDefine.getFilter(), getRowMap(row)));
                if (!filter) {
                    continue;
                }
            }

            String countSql = getCountSql(sheetDefine, row);
            Number count = DbUtil.use(ds.getDataSource()).queryNumber(countSql);
            if (count.intValue() > 0) {
                sqlMap.put("updateSql", getUpdateSql(sheetDefine));
                Object[] objs = getUpdateParam(sheetDefine, row);
                updateParamsList.add(objs);
                Console.log(JSONUtil.toJsonStr(objs));
            } else if (sheetDefine.getInsert()) {
                sqlMap.put("insertSql", getInsertSql(sheetDefine));
                insertParamsList.add(getInsertParam(sheetDefine, row));
            }
        }

        log.info("batch insert sql --> {}", sqlMap.getStr("insertSql"));
        log.info("           param --> {}", JSONUtil.toJsonStr(insertParamsList));
        log.info("batch update sql --> {}", sqlMap.getStr("updateSql"));
        log.info("           param --> {}", JSONUtil.toJsonStr(updateParamsList));

        DbUtil.use(ds.getDataSource()).tx(new TxFunc() {
            @Override
            public void call(Db db) throws SQLException {
                if (StrUtil.isNotBlank(sqlMap.getStr("insertSql"))) {
                    db.executeBatch(sqlMap.getStr("insertSql"), parseListToArray(insertParamsList));
                }
                if (StrUtil.isNotBlank(sqlMap.getStr("updateSql"))) {
                    db.executeBatch(sqlMap.getStr("updateSql"), parseListToArray(updateParamsList));
                }
            }
        });
    }

	private Map getRowMap(List row) {
		Map rowMap = new HashMap();
		// 初始化空列
		for (int i = 1; i < 256; i++) {
			rowMap.put("c" + i, null);
		}

		for (int colIndex = 0; colIndex < row.size(); colIndex++) {
			rowMap.put("c" + (colIndex + 1), Convert.toStr(row.get(colIndex)));
		}
		return rowMap;
	}

	private String getCountSql(SheetDefine sheetDefine, List row) {
		String sql = "select count(*) count from " + sheetDefine.getTableName();
		String whereSql = getWhereSql(sheetDefine, row);
		if (StrUtil.isNotBlank(sheetDefine.getPrimaryKey()) && StrUtil.isNotBlank(whereSql)) {
			return sql + whereSql;
		}
		return null;
	}
	
    public static Object[][] parseListToArray(List<Object[]> data) {
        Object[][] obj = new Object[data.size()][];
        for (int i = 0, len = data.size(); i < len; i++) {
            obj[i] = data.get(i);
        }
        return obj;
    }

	private String getInsertSql(SheetDefine sheetDefine) {
		List<FieldMapping> fm = sheetDefine.getFields();
		StringBuilder colSql = new StringBuilder();
		StringBuilder paramSql = new StringBuilder();
		for (int i = 0; i < fm.size(); i++) {
			colSql.append(fm.get(i).getTableField());
			paramSql.append("?");
			if (i != (fm.size() - 1)) {
				colSql.append(",");
				paramSql.append(",");
			}
		}
		StringBuilder sbSql = new StringBuilder("insert into ");
		sbSql.append(sheetDefine.getTableName()).append("(").append(colSql.toString()).append(")value(")
				.append(paramSql.toString()).append(")");
		return sbSql.toString();
	}

	private Object[] getInsertParam(SheetDefine sheetDefine, List row) {
		Map rowMap = getRowMap(row);
		List<FieldMapping> fm = sheetDefine.getFields();
		List paramsList = new ArrayList();
		for (int i = 0; i < fm.size(); i++) {
			String val = Convert.toStr(JSUtil.execute(fm.get(i).getRuleField(), rowMap));

			paramsList.add(StrUtil.isNotBlank(val) ? val : null);
		}
		return paramsList.toArray();
	}

	private Object[] getUpdateParam(SheetDefine sheetDefine, List row) {
		Map rowMap = getRowMap(row);
		List<FieldMapping> fm = sheetDefine.getFields();
		List paramsList = new ArrayList();

		for (int i = 0; i < fm.size(); i++) {
			FieldMapping fieldMapping = fm.get(i);
			if (fieldMapping.getUpdate()) {
				paramsList.add(getExecuteData(Convert.toStr(JSUtil.execute(fieldMapping.getRuleField(), rowMap))));
			}
		}
		if (StrUtil.isNotBlank(sheetDefine.getPrimaryKey())) {
			String[] primaryKeyArray = sheetDefine.getPrimaryKey().split(",");
			if (primaryKeyArray != null && primaryKeyArray.length > 0) {
				for (String str : primaryKeyArray) {
					for (FieldMapping f : sheetDefine.getFields()) {
						if (f.getTableField().equalsIgnoreCase(str)) {
							paramsList.add(row.get(Convert.toInt(f.getRuleField().replace("c", "")) - 1));
						}
					}
				}
			}
		}
		return paramsList.toArray();
	}

	private String getUpdateSql(SheetDefine sheetDefine) {
		List<FieldMapping> fm = sheetDefine.getFields();
		StringBuilder colSbSql = new StringBuilder();
		for (int fieldMappingIndex = 0; fieldMappingIndex < fm.size(); fieldMappingIndex++) {
			FieldMapping fieldMapping = fm.get(fieldMappingIndex);
			if (fieldMapping.getUpdate()) {
				colSbSql.append(fieldMapping.getTableField()).append("=?,");
			}
		}
		String colSql = StrUtil.removeSuffix(colSbSql, ",");
		if (StrUtil.isBlank(colSql)) {
			return "";
		}
		StringBuilder sbSql = new StringBuilder("update ").append(sheetDefine.getTableName()).append(" set ")
				.append(colSql);

		if (StrUtil.isNotBlank(sheetDefine.getPrimaryKey())) {
			sbSql.append(" where 1=1 ");
			String[] primaryKeyArray = sheetDefine.getPrimaryKey().split(",");
			if (primaryKeyArray != null && primaryKeyArray.length > 0) {
				for (String str : primaryKeyArray) {
					for (FieldMapping f : sheetDefine.getFields()) {
						if (f.getTableField().equalsIgnoreCase(str)) {
							sbSql.append(" and " + str + "=?");
						}
					}
				}
			}
		}
		return sbSql.toString();
	}

	private Object getExecuteData(Object jsDate) {
		if (jsDate instanceof String) {
			String val = Convert.toStr(jsDate);
			if (JSUtil.isDate(val)) {
				return SqlUtil.toSqlDate(DateUtil.parseDate(val));
			}
			return StrUtil.isNotBlank(val) ? val : null;
		}
		return jsDate;
	}
	
	private String getWhereSql(SheetDefine sheetDefine, List row) {
        if (StrUtil.isNotBlank(sheetDefine.getPrimaryKey())) {
            StringBuilder sql = new StringBuilder(" where 1=1 ");
            String[] primaryKeyArray = sheetDefine.getPrimaryKey().split(",");
            if (primaryKeyArray != null && primaryKeyArray.length > 0) {
                for (String str : primaryKeyArray) {
                    for (FieldMapping fm : sheetDefine.getFields()) {
                        if (fm.getTableField().equalsIgnoreCase(str)) {
                            sql.append(" and " + str + "='" + row.get(Convert.toInt(fm.getRuleField().replace("c", "")) - 1) + "'");
                        }
                    }

                }
                return sql.toString();
            }
        }
        return null;
    }
}
