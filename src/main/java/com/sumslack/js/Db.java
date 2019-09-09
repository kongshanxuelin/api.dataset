package com.sumslack.js;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.sumscope.tag.TagConst;

import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;

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
	public List<Entity> query(String sql,Object... params) {
		try {
			return DbUtil.use(this.ds).query(sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
