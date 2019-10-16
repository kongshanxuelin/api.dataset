package com.sumslack.js;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.compress.utils.Lists;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sumscope.tag.TagConst;
import com.sumslack.dataset.api.report.bean.DatasourceBean;

import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
@JSFunc(id="Db",description = "数据库操作类")
public class Db {
	private DataSource ds = TagConst.dataSourceMap.get("default").getDataSource();;
	public Db use(String datasource) {
		this.ds = TagConst.dataSourceMap.get(datasource).getDataSource();
		return this;
	}
	public Db use() {
		this.ds = TagConst.dataSourceMap.get("default").getDataSource();
		return this;
	}
	public Db use(DatasourceBean dsBean) {
		if(dsBean!=null) {
			DruidDataSource ds2 = new DruidDataSource();
			ds2.setUrl(dsBean.getUrl());
			ds2.setUsername(dsBean.getUser());
			ds2.setPassword(dsBean.getPassword());
			this.ds = ds2;
		}
		return this;
	}
	public List<Entity> query(String sql,Object... params) {
		try {
			return DbUtil.use(this.ds).query(sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Entity queryOne(String sql,Object... params) {
		try {
			return DbUtil.use(this.ds).queryOne(sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject one(String sql,Object... params) {
		try {
			Map result = new HashMap();
			Entity rec = DbUtil.use(this.ds).queryOne(sql, params);
			if(rec!=null) {
				for(String k : rec.keySet()) {
					result.put(k, rec.get(k));
				}
			}
			return new JSONObject(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public JSONArray list(String sql,Object... params) {
		try {
			List<Entity> rec = DbUtil.use(this.ds).query(sql, params);
			if(rec!=null) {
				List result = Lists.newArrayList();
				for(Entity en : rec) {
					Map _map = new HashMap();
					for(String k : en.keySet()) {
						_map.put(k, en.get(k));
					}
					result.add(_map);
				}
				return new JSONArray(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public PageResult<Entity> page(String tableName,Object where,int pageNumber,int pageSize) {
		try {
			Entity whereEntity =  entityIt(tableName,where);
			return DbUtil.use(this.ds).page(whereEntity,
					new Page(pageNumber, pageSize));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** 为了安全性，我们强烈不建议你开启数据库的增删改操作对外
	public int execute(String sql,Object... params) {
		try {
			return DbUtil.use(this.ds).execute(sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public int update(String tableName,Object updateFields,Object where) {
		if(updateFields instanceof ScriptObjectMirror && where instanceof ScriptObjectMirror) {
			Entity updateEntity = entityIt(updateFields);
			Entity whereEntity =  entityIt(tableName,where);
			try {
				return DbUtil.use(this.ds).update(updateEntity,whereEntity);
			} catch (SQLException e) {
				e.printStackTrace();
				return -2;
			}
		}
		return -1;
	}
	
	public int del(String tableName,Object where) {
		try {
			return DbUtil.use(this.ds).del(
					entityIt(tableName,where)
				);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int insert(String tableName,Object fields) {
		try {
			return DbUtil.use(this.ds).insert(
				    entityIt(tableName, fields)
				);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	**/
	private Entity entityIt(String tableName,Object where) {
		Entity whereEntity = null;
		if(tableName!=null) {
			whereEntity = Entity.create(tableName);
		}else {
			whereEntity = Entity.create();
		}
		ScriptObjectMirror _where = (ScriptObjectMirror)where;
		for(String _key : _where.keySet()) {
			whereEntity.set(_key, _where.get(_key));
		}
		return whereEntity;
	}
	
	private Entity entityIt(Object where) {
		return entityIt(null,where);
	}
	
}
