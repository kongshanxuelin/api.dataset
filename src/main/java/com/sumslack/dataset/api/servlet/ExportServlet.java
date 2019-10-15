package com.sumslack.dataset.api.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sumscope.tag.rest.TagRest;
import com.sumscope.tag.rest.servlet.AjaxServlet;
import com.sumscope.tag.util.HttpUtils;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.ReportBean;
import com.sumslack.dataset.api.report.util.IReport;
import com.sumslack.dataset.api.report.util.ReportUtil;
import com.sumslack.dataset.api.report.vo.ReportLineVO;
import com.sumslack.dataset.api.report.vo.ReportVO;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

@TagRest(value="report/export")
public class ExportServlet extends AjaxServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ids = StrUtil.formatNullStr(req.getParameter("ids"));
		String fileName = StrUtil.formatNullStr(req.getParameter("file"),"report");
		String[] idArray = StrUtil.split(ids, ",");
		ExcelWriter ee = ExcelUtil.getWriter("export.xls");
		if(idArray!=null) {
			for(String reportId : idArray) {
				List<ReportLineVO> list = null;
				ReportBean rb = ReportUtil.getReport(fileName,reportId);
				//这种情况下，需要SQL或存储过程定义数据集
				if(StrUtil.isEmpty(rb.getJava())) {
					rb.init(HttpUtils.getParamMap(req));
					try {
						List<ReportLineVO> reportLine = rb.returnReportJSON(HttpUtils.getParamMap(req));
						if(reportLine != null && reportLine.size()>0) {
							list = reportLine;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else { //自己实现完整的报表
					IReport myReport = ReflectUtil.newInstance(rb.getJava());
					ReportVO report = myReport.genReport(rb,HttpUtils.getParamMap(req));
					list = report.getRows();
				}
				if(list!=null && list.size()>0 && list instanceof List) {
					ReportLineVO line = list.get(0);
					List<Map> data = line.getData();
					List<String> vvs = data.stream().map(s -> StrUtil.formatNullStr(s.get("field"))).collect(Collectors.toList());
					vvs.add(0, "");
					
					//编写Excel表格标题
					ee.merge(vvs.size()-1,rb.getTitle());
					//根据field，v写一行行Excel
					String title = line.getLabel().getTitle();
					//写入日期行
					ee.writeRow(vvs);
					for(ReportLineVO _line : list) {
						String _title = _line.getLabel().getTitle();
						List<Map> _data = _line.getData();
						List<String> vvv = _data.stream().map(s -> StrUtil.formatNullStr(s.get("v"))).collect(Collectors.toList());
						vvv.add(0,_title);
						ee.writeRow(vvv);
					}
				}
				ee.writeRow(CollUtil.newArrayList());
			}
		}
		resp.setContentType("application/vnd.ms-excel;charset=utf-8"); 
		resp.setHeader("Content-Disposition","attachment;filename=export.xls"); 
		ServletOutputStream out=resp.getOutputStream(); 
		ee.setColumnWidth(0, 50);
		ee.flush(out);
		IoUtil.close(out);
	}
}
