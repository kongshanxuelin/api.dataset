package com.sumslack.dataset.api.report.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sumscope.tag.sql.bean.KV;
import com.sumslack.dataset.api.report.bean.ReportBean;
import com.sumslack.dataset.api.report.util.IReport;
import com.sumslack.dataset.api.report.vo.ReportColVO;
import com.sumslack.dataset.api.report.vo.ReportLineVO;
import com.sumslack.dataset.api.report.vo.ReportVO;
import com.sumslack.dataset.api.report.vo.ReportVO.RET;

public class ReportTest implements IReport{
	@Override
	public ReportVO genReport(ReportBean rb, Map paramMap) {
		ReportVO rr = new ReportVO();
		List<ReportColVO> cols = new ArrayList(); 
		cols.add(new ReportColVO("201902", "201902"));
		cols.add(new ReportColVO("201903", "201903"));
		rr.setCols(cols);
		List<ReportLineVO> list = new ArrayList();
		list.add(new ReportLineVO("test1",new KV("201902","12.32"),new KV("201903","22.32")));
		list.add(new ReportLineVO("test2",new KV("201902","42.32"),new KV("201903","52.32")));
		rr.setRows(list);
		rr.setRet(RET.SUCCESS);
		return rr;
	}

}
