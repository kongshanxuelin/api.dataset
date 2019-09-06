package com.sumslack.dataset.api.report.util;

import java.util.Map;

import com.sumslack.dataset.api.report.bean.ReportBean;
import com.sumslack.dataset.api.report.vo.ReportVO;

public interface IReport {
	public ReportVO genReport(ReportBean rb,Map paramMap);
}
