<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sn" uri="http://www.sumscope.com/taglib"%>
<html>
<head>
<!-- bootstrap -->
<link rel="stylesheet" href="<sn:webroot/>/css/bootstrap.min.css">
<script type="text/javascript"
	src="<sn:webroot/>/js/jquery-1.11.1.min.js"></script>
<link rel="stylesheet" href="<sn:webroot/>/js/codemirror.css">
<script src="<sn:webroot/>/js/codemirror.js"></script>
<script src="<sn:webroot/>/js/mode/javascript/javascript.js"></script>

<link rel="stylesheet" href="<sn:webroot/>/js/addon/fold/foldgutter.css">
<link rel="stylesheet" href="<sn:webroot/>/js/addon/hint/show-hint.css">
<script src="<sn:webroot/>/js/addon/hint/show-hint.js"></script>
<script src="<sn:webroot/>/js/addon/hint/xml-hint.js"></script>

<script src="<sn:webroot/>/js/addon/fold/foldcode.js"></script>
<script src="<sn:webroot/>/js/addon/fold/foldgutter.js"></script>
<script src="<sn:webroot/>/js/addon/fold/xml-fold.js"></script>

<script src="<sn:webroot/>/js/addon/selection/active-line.js"></script>
<script src="<sn:webroot/>/js/addon/edit/matchtags.js"></script>

<!-- upload file -->
<script src="<sn:webroot/>/js/socket.io/socket.io.js"></script>

<style type="text/css">
.CodeMirror {
	border: 1px solid #eee;
}

li {
	margin: 5px;
}
.logp {
	line-height: 25px;
	height:25px;
	color: green;
	margin:2px 5px;
	padding:0;
}
.log-time {
	color:#B03131;
}
</style>
</head>
<body>
<div class="d-flex flex-column" style="width: 100%;height:100%;">
		<div region="north" class="north" style="height: 40px;">
        	<div style="margin: 2px 20px;">
				<table width="100%">
					<tr>
						<td align="right" style="padding-right: 10px;">	
							<span class="alert alert-primary">当前正在调试：${file }</span>	
							<input class="btn btn-primary"
							type="button" id="btnSave" value="运行 Ctrl+R" />
							<input class="btn btn-dark"
							type="button" id="btnClear" value="清空Ctrl+D" />
							
						</td>
					</tr>
				</table>
			</div>
    	</div>
		<div region="center" class="center flex-fill">
			<textarea id="code" name="code">${c}</textarea>
		</div>
		<div id="loginfo"
			style="border: 1px solid #ccc; height: 100px; color: grey; overflow-x: hidden; overflow-y: auto;"
			region="south" class="south"></div>

</div>
	<script type="text/javascript">
		var WSUserName = "";
		function clearLog() {
			$('#loginfo').empty();
		}
		function appendLog(msg,tag) {
			if(typeof(tag)!="undefined"){
				$("#loginfo").append("<p class='logp' style='color:"+tag+"'>" + msg + "</p>");
			}else{
				$("#loginfo").append(msg);
			}
			var scrollHeight = $('#loginfo').prop("scrollHeight");
			$('#loginfo').scrollTop(scrollHeight, 200);
		}
		function saveFile() {
			$.ajax({
				type : "post",
				url : "<sn:webroot/>/report/runcode",
				data : {
					content : editor.getValue(),
					lang : "js",
					file:"${file }"
				},
				success : function(json) {
					console.log(json);
					appendLog("！！！执行结果：" + json.ret,"red");
				},
				error : function() {

				}
			});
		}
		
		CodeMirror.javascriptKeywords = ("Db logger Auth Cloud").split(" ");
		CodeMirror.commands.autocomplete = function (cm) {
            CodeMirror.showHint(cm, CodeMirror.javascriptHint);
        }
		
		var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
			lineWrapping : true,
			firstLineNumber : 1,
			autofocus : true,
			lineNumbers : true,
			styleActiveLine : true,
			matchTags : {
				bothTags : true
			},
			showCursorWhenSelecting : true,
			foldGutter : true,
			gutters : [ "CodeMirror-linenumbers", "CodeMirror-foldgutter" ],
			mode : "javascript",
			extraKeys : {
				"Ctrl-E" : "autocomplete",
				"Ctrl-R" : function() {
					saveFile();
				},
				"Ctrl-D" : function() {
					clearLog();
				}
			}
		});
		editor.setSize('100%', (document.body.clientHeight - 145) + 'px');

		$(function() {
			//ws
			WSUserName = "user_" + Math.floor((Math.random() * 1000) + 1);
			var socket = io.connect('http://localhost:9092');
			socket.on('connect', function() {
				console.log("ws connected.");
				socket.emit('login', {
					userName : WSUserName
				}, function(arg1, arg2) {
					console.log("login ok: ", arg1, arg2);
				});
			});
			socket.on('disconnect', function() {
				console.log("ws disconnected.");
			});
			socket.on('log', function(data, callback) {
				console.log("log", data);
				if (data.msg) {
					appendLog("<p class='logp'>" + data.msg + "</p>");
				}
			});
			$("#btnSave").bind("click", function() {
				saveFile();
			});
			$("#btnClear").bind("click",function(){
				clearLog();
			});
		});
	</script>
</body>
</html>
