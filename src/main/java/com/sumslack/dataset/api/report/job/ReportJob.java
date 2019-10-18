package com.sumslack.dataset.api.report.job;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.concurrent.Executors;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.VoidAckCallback;
import com.corundumstudio.socketio.listener.DataListener;
import com.sumscope.tag.TagConst;
import com.sumscope.tag.job.TagJob;
import com.sumscope.tag.util.StrUtil;
import com.sumslack.dataset.api.report.bean.WsMsg;
import com.sumslack.dataset.api.report.util.ReportUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.watchers.DelayWatcher;

public class ReportJob extends TagJob{
	//配置文件所在目录
	public static final String REPORT_FILEPATH = StrUtil.formatNullStr(TagConst.globalMap.get("reportFilePath"));
	private static SocketIOServer server;
	
	public static void broadcastLog(String name,Object msg) {
//		if(server!=null) {
//			server.getAllClients().forEach(s -> s.sendEvent("message", msg));
//		}
		if(server!=null) {
			WsMsg wsmsg = new WsMsg();
			wsmsg.setUserName(name);
			wsmsg.setMsg(msg);
			server.getBroadcastOperations().sendEvent("log",wsmsg);
//			server.getAllClients().forEach(s -> s.sendEvent("log", new AckCallback<String>(String.class) {
//				@Override
//				public void onSuccess(String result) {
//					System.out.println("ack from client:" + result);
//				}
//			},wsmsg));
		}
	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//启动websocket
		Executors.newFixedThreadPool(1).execute(new Runnable() {
			@Override
			public void run() {
				System.out.println("web socket start....9092");
				Configuration config = new Configuration();
		        config.setHostname("localhost");
		        config.setPort(9092);
		        server = new SocketIOServer(config);
		        server.addEventListener("login", WsMsg.class, new DataListener<WsMsg>() {
		            @Override
		            public void onData(final SocketIOClient client, WsMsg data, final AckRequest ackRequest) {
		                if (ackRequest.isAckRequested()) {
		                    ackRequest.sendAckData(client.getSessionId().toString(), data);
		                }
//		                WsMsg ackChatObjectData = new WsMsg(data.getUserName(), "message with ack data");
//		                client.sendEvent("ack", new AckCallback<String>(String.class) {
//		                    @Override
//		                    public void onSuccess(String result) {
//		                        System.out.println("ack from client: " + client.getSessionId() + " data: " + result);
//		                    }
//		                }, ackChatObjectData);
		            }
		        });
		        server.start();
			}
		});
        
		//首次初始化配置
		ReportUtil.initReports("all");
		File file = FileUtil.file(REPORT_FILEPATH);
		WatchMonitor.createAll(file, new DelayWatcher(new SimpleWatcher() {
			@Override
			public void onModify(WatchEvent<?> event, Path currentPath) {
				System.out.println("event:" + event.toString());
				if(event.kind() == WatchMonitor.ENTRY_MODIFY) {
					ReportUtil.initReports("all");
				}
			}
		}, 2000)).start();
		
//		WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.ENTRY_MODIFY);
//		watchMonitor.setWatcher(new Watcher(){
//		    @Override
//		    public void onCreate(WatchEvent<?> event, Path currentPath) {
//		        Object obj = event.context();
//		    }
//
//		    @Override
//		    public void onModify(WatchEvent<?> event, Path currentPath) {
//		        Object obj = event.context();
//		        //修改文件了需要更新缓存
//		        ReportUtil.initReports(currentPath.getFileName().toString());
//		    }
//
//		    @Override
//		    public void onDelete(WatchEvent<?> event, Path currentPath) {
//		        Object obj = event.context();
//		    }
//
//		    @Override
//		    public void onOverflow(WatchEvent<?> event, Path currentPath) {
//		        Object obj = event.context();
//		    }
//		});
//		watchMonitor.setMaxDepth(3);
//		watchMonitor.start();
	}
}
