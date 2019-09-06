/****
 * @author:cxlh
 * @version:1.0.0
 * Sumscope JSPTag JQuery插件
 * 用于定义公用函数集，数据集相关Ajax操作，WebSocket相关操作等
 */
$.mozilla = /firefox/.test(navigator.userAgent.toLowerCase());
$.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
$.opera = /opera/.test(navigator.userAgent.toLowerCase());
$.msie = /msie/.test(navigator.userAgent.toLowerCase());
;(function ($,window){
	if ((typeof jQuery === 'undefined') || (typeof $ === 'undefined')) {
	    throw new Error("jquery.nbs.tag.ajax.js requires the jQuery");
	}

	function execAjax(settings,suffix)
	{
		  var _url = settings.webroot + "/tag/sql/"+settings.ds_rel+"/"+suffix;
		  var jsonArg = settings.arg;
		  if(typeof settings.type === "undefined"){
			  settings.type = 'POST';
		  }
		  $.ajax({
			   type: settings.type,
			   url: _url,
			   data: jsonArg,
			   success: function(json){
				  if(settings.callback){
					  settings.callback(json);
				  }else{
					  if("get" === suffix){
						  if(typeof(settings.callback) === "undefined"){
							  $.each(json.ret,function(i,n){
								 $("[bind_col="+i+"]").val(n); 
							  });
						  }
					  }
				  }
			   },
			   error:function (XMLHttpRequest, textStatus, errorThrown) {
				   var json = {};
				   json.res = false;
				   json.ret = textStatus;
				   settings.callback(json);
			   }
		  });	
	};
	var NBSJSPTag = {
		version:'1.0.0',
		description:'宁波团队Sumscope Ajax标签库' 
	};
	
	
	$.tagBase = {
		init:function(options){
			var options = $.extend({
				  'url' 	: '',			 //请求URL
				  'type'		: 'POST',    //元素Ajax请求方式
				  'webroot'	: '',
			      'arg'		: {}			 //URL对应的参数列表
			    }, options,NBSJSPTag);
			this.render(options);
		},
		render:function(settings){
			//override it
		}
	}; 

	$.nbs = {
			/***
			 * Ajax获取某个数据集的selectOne结果，返回的是SQL执行的Map的JSON对象
			 * @author:cxlh
			 */
			tagSelectOne:function(options){
				  var settings = $.extend( {
					  'webroot'	: '',
				      'ds_rel'  : '',
				      'type'	: 'POST',
				      'arg'		: {}
				    }, options);
				  execAjax(options,"get");
			},
			/***
			 * Ajax获取删除结果集的某条记录或一批记录，对应结果集的Delete SQL
			 * @author:cxlh
			 */		
			tagDelete:function (options){
			  var settings = $.extend( {
				  'webroot'	: '',
			      'ds_rel'  : '',
			      'type'		: 'POST',
			      'arg'		: {}
			    }, options);
			  execAjax(settings,"del");
		   },
			/***
			 * Ajax获取保存结果集，对应结果集的Update SQL
			 * @author:cxlh
			 */
		   tagSave:function (options){
			  var settings = $.extend( {
				  'webroot'	: '',
			      'ds_rel'  : '',
			      'type'		: 'POST',
			      'arg'		: {}
			    }, options);
			  
			  execAjax(settings,"save");
		   },
			/***
			 * Ajax获取业务代码，执行业务代码Java或SQL，对应Biz节点
			 * @author:cxlh
			 */
		   tagBiz:function (options){
			  var settings = $.extend( {
				  'webroot'	: '',
			      'ds_rel'  : '',
			      'biz_rel'	: '',
			      'type'		: 'POST',
			      'arg'		: {}
			    }, options);
			  settings.arg.biz_rel = settings.biz_rel; 
			  execAjax(settings,"biz");
		   },	  
			/***
			 * Ajax获取某个数据集的Insert操作，对应Insert节点
			 * @author:cxlh
			 */
		   tagAdd:function (options){
			  var settings = $.extend( {
				  'webroot'	: '',
			      'ds_rel'  : '',
			      'type'		: 'POST',
			      'arg'		: {}
			    }, options);
			  
			  execAjax(settings,"add");
		   }
	};
	//WebSocket长连接全局变量
	var socket;
	$.nbs_ws = {
		 /*******
		  * WebSocket通信函数，必须引入socket.io.js库
		  */
		 init:function(options){
			var settings = $.extend({
			  'ip'	: 	'http://localhost',
			  'port': '8000',
			  'debug' 	: false,
			  'arg'	: {}
			}, options);
			var _url = settings.ip;
			if(settings.port!="80" && settings.port!="443"){
				  _url+=":"+settings.port;
			}
			socket = io.connect(_url, {});
			/**
			 * Websocket成功连接时
			 */
			socket.on('connect', function(user) {
				if(settings.debug){
					console.debug("[ws]connected");
				}
				if(settings.onConnected)
					settings.onConnected(null);
			});
			/**
			 * Websocket验证登录成功时，调用login接口的回调函数，但需要发送点对点消息或房间消息时，login操作是必须的
			 */
			socket.on('onLogined', function(user) {
				if(settings.debug){
					console.debug("[ws]onLogined");
					console.debug(user);
				}
				if(settings.onLogined)
					settings.onLogined(null);
			});
			/**
			 * 断开连接时的回调
			 */
			socket.on('disconnect', function() {
				if(settings.debug){
					console.debug("[ws]disconnect");
				}
				if(settings.onCisconnected)
					settings.onCisconnected(null);
			});
			/**
			 * 有人进入房间的回调
			 */
			socket.on('onUserJoinRoom', function(obj){
				if(settings.debug){
					console.debug("[ws]onUserJoinRoom");
					console.debug(obj);
				}
				if(settings.onJoinRoom)
					settings.onJoinRoom(obj);
			});
			/**
			 * 有人离开房间的回调
			 */
			socket.on('onUserLeaveRoom', function(obj){
				if(settings.debug){
					console.debug("[ws]onUserLeaveRoom");
					console.debug(obj);
				}
				if(settings.onLeaveRoom)
					settings.onLeaveRoom(obj);
			});
			/**
			 * 收到房间消息时的回调
			 */
			socket.on('onRoomMessage', function(obj){
				if(settings.debug){
					console.debug("[ws]onRoomMessage");
					console.debug(obj);
				}
				if(settings.onRoomMessage)
					settings.onRoomMessage(obj);
			});
			/**
			 * 收到点对点消息的回调
			 */
			socket.on('onMessage', function(obj){
				if(settings.debug){
					console.debug("[ws]onMessage");
					console.debug(obj);
				}
				if(settings.onMessage)
					settings.onMessage(obj);
			});
			/**
			 * 收到广播消息的回调
			 */
			socket.on('onBroadcast', function(obj) {
				if(settings.debug){
					console.debug("[ws]broadcast");
					console.debug(obj);
				}
				if(settings.onBroadcast)
					settings.onBroadcast(obj);
			});				  
		 },
		 /**
		  * 广播
		  * @param channel：频道
		  * @param argsJSON：json参数列表
		  */
		 broadcast:function(channel,argsJSON){
			var _json = {e:channel,args:argsJSON};
		    socket.emit("broadcast",_json);
		 },
		 /**
		  * 进入房间或订阅，roomid是房间ID或频道ID
		  * @param _roomid：房间ID
		  * @param _nickname：进入房间使用的昵称，不是必须的
		  */
		 joinRoom:function(_roomid,_nickname){
			socket.emit("joinRoom", {room:_roomid,nickname:_nickname});
		 },
		 /**
		  * 离开房间
		  * @param _roomid
		  */
		 leaveRoom:function(_roomid){
			 socket.emit("leaveRoom", {room:_roomid});
		 },
		 /**
		  * 发送房间消息
		  * @param _roomid
		  * @param _msg
		  */
		 sendRoomMessage:function(_roomid,_msg){
			 socket.emit("sendRoomMessage", {room:_roomid,msg:_msg});
		 },
		 /**
		  * 登录
		  * @param _username：用户名
		  * @param _password：密码
		  */
		 login:function(_username,_password){
			 socket.emit("login", {username:_username,password:_password});
		 },
		 /**
		  * 断开连接
		  */
		 disconnect:function(){
			socket.emit("disconnect", {});
		 },
		 /**
		  * 发送点对点消息
		  * @param _to：对方username
		  * @param _msg：发送的消息内容
		  */
		 sendMessage:function(_to,_msg){
			 socket.emit("sendMessage",{to:_to,msg:_msg});
		 }
	};
	$.fn.extend({
		/*
		 * 智能提示输入框控件
		 */
		tagAutoCompleter:function(options){
			return this.each(function() {
				var o = options;
				var ui = $(this);
				var thisObj = $.extend($.tagBase,{
					render:function(){
						ui.val(o.defaultValue);
					}
				});
				thisObj.init(options);
			});
		},
		/*
		 * 下拉框控件
		 */
		tagSelector:function(options){
			return this.each(function() {
				var o = options;
				var ui = $(this);
				var thisObj = $.extend($.tagBase,{
					render:function(){
						ui.empty();
						var _bizRel = ui.attr("biz-rel");
						var _dsRel =  ui.attr("ds-rel");
						var _arg = eval("("+ui.attr("arg")+")");
						$.nbs.tagBiz({webroot:o.webroot,biz_rel:_bizRel,type:'GET',ds_rel:_dsRel,arg:_arg,callback:function(json){
							if(json.ret.res.length>0){
								$.each(json.ret.res,function(i,n){
					    			var fieldKey = ui.attr("field-key");
					    			if(fieldKey == "undefined" || fieldKey == "") fieldKey = "id";
					    			var fieldValue = ui.attr("field-value");
					    			var _fieldKey = eval("n."+fieldKey);
					    			var _fieldValue = eval("n."+fieldValue);
					    			ui.append("<option value=\""+_fieldKey+"\">"+_fieldValue+"</option>");									
								});
							}
			    		}}); 
						
						
					}
				});
				thisObj.init(options);
			});
		}
			 
	});
})(jQuery,window);