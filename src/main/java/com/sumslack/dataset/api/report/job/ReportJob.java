package com.sumslack.dataset.api.report.job;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sumscope.tag.TagConst;
import com.sumscope.tag.job.TagJob;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.util.ReportUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;

public class ReportJob extends TagJob{
	public static final String REPORT_FILEPATH = StrUtil.formatNullStr(TagConst.globalMap.get("reportFilePath"));
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//首次初始化配置
		ReportUtil.initReports();
		File file = FileUtil.file(REPORT_FILEPATH);
		WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.ENTRY_MODIFY);
		watchMonitor.setWatcher(new Watcher(){
		    @Override
		    public void onCreate(WatchEvent<?> event, Path currentPath) {
		        Object obj = event.context();
		    }

		    @Override
		    public void onModify(WatchEvent<?> event, Path currentPath) {
		        Object obj = event.context();
		        //修改文件了需要更新缓存
		        ReportUtil.initReports();
		    }

		    @Override
		    public void onDelete(WatchEvent<?> event, Path currentPath) {
		        Object obj = event.context();
		    }

		    @Override
		    public void onOverflow(WatchEvent<?> event, Path currentPath) {
		        Object obj = event.context();
		    }
		});
		watchMonitor.setMaxDepth(3);
		watchMonitor.start();
	}
}
