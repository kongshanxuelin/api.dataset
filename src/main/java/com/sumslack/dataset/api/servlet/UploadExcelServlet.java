package com.sumslack.dataset.api.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.sumscope.tag.rest.TagRest;
import com.sumscope.tag.rest.servlet.AjaxServlet;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.excel.ImportExcelUtil;
import com.sumslack.excel.R;

@TagRest(value = "excel/upload")
public class UploadExcelServlet extends AjaxServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		request.setCharacterEncoding("UTF-8");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> files = new ArrayList();
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		Iterator itr = items.iterator();
		Map param = new HashMap();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			if (item.isFormField()) {
				String _key = item.getFieldName();
				param.put(StrUtil.formatNullStr(_key), StrUtil.formatNullStr(item.getString()));
			}else {
				if (item.getName() != null && !"".equals(item.getName())) {
					files.add(item);
				}
			}
		}
		try {
			for (FileItem _file : files) {
				ImportExcelUtil ex = new ImportExcelUtil();
				R r = ex.importExcel(_file);
				printOut(response, request, JSON.toJSONString(r));
			}
		}catch(Exception ex) {
			R r = new R();
			r.error(ex.getMessage());
			printOut(response, request, JSON.toJSONString(r));
		}
	}
}
