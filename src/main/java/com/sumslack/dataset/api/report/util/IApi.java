package com.sumslack.dataset.api.report.util;

import java.util.Map;

import com.sumslack.dataset.api.report.bean.ApiBean;
import com.sumslack.dataset.api.report.vo.ReportVO;

public interface IApi {
	public ReportVO genApi(String fileName,ApiBean api,Map paramMap);
}
