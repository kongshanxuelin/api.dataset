<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sn" uri="http://www.sumscope.com/taglib"%>
<html>
<head>
<script type="text/javascript"
	src="<sn:webroot/>/js/jquery-1.11.1.min.js"></script>
<link rel="stylesheet" href="<sn:webroot/>/js/codemirror.css">
<script src="<sn:webroot/>/js/codemirror.js"></script>
<script src="<sn:webroot/>/js/mode/xml/xml.js"></script>


<link rel="stylesheet" href="<sn:webroot/>/js/addon/hint/show-hint.css">
<script src="<sn:webroot/>/js/addon/hint/show-hint.js"></script>
<script src="<sn:webroot/>/js/addon/hint/xml-hint.js"></script>

<style type="text/css">
.CodeMirror {
	border: 1px solid #eee;
}

li {
	margin: 5px;
}

.path {
	color: orange;
	font-weight: bold;
}

.warning {
	font-size:12px;
	color: #155724;
    background-color: #d4edda;
    border-color: #c3e6cb;
    padding: .75rem 1.25rem;
    margin-bottom: 1rem;
    border: 1px solid transparent;
    border-radius: .25rem;
}
</style>
</head>
<body>
	<p style="margin:5px 20px;">
		配置文件所在路径：<span class="path">${path }</span>
		<input style="margin-left:10px;" type="button" id="btnSave" value="保存文件" />
		<span id="tip"></span>
	</p>
	<div class="warning">
		注：修改该文件即可通过 <a target="_blank" 
					href='<%="http://" + request.getLocalAddr() +":" + request.getLocalPort() + "/report/id/xxx" %>'> 
					<%="http://" + request.getLocalAddr() +":" + request.getLocalPort() + "/report/id/xxx" %>
					</a> 访问对应接口，xxx是report节点id的值
	</div>
	<textarea id="code" name="code">${c}</textarea>
	<script type="text/javascript">
		var dss = "${ds}".split(",");
		var tags = {
			"!reports" : [ "reports" ],
			"!attrs" : {
				ds : dss
			},
			reports : {
				children : [ "report" ]
			},
			report : {
				attrs : {
					id : null,
					title : null,
					ds : dss,
					startDate : null,
					endDate : null,
					java : null,
					step : [ "month", "day", "year", "week" ],
					dateFormat : [ "yyyyMM", "yyyy-MM", "yyyy-MM-dd",
							"yyyyMMdd", "yyyy/MM/dd", "yyyy/MM" ]
				},
				children : [ "row" ]
			},
			row : {
				attrs : {
					label : null,
					align : [ "left", "center", "right" ],
					fontWeight : [ "normal", "bold" ]
				}
			}
		};

		function completeAfter(cm, pred) {
			var cur = cm.getCursor();
			if (!pred || pred())
				setTimeout(function() {
					if (!cm.state.completionActive)
						cm.showHint({
							completeSingle : false
						});
				}, 100);
			return CodeMirror.Pass;
		}
		function completeIfAfterLt(cm) {
			return completeAfter(
					cm,
					function() {
						var cur = cm.getCursor();
						return cm.getRange(
								CodeMirror.Pos(cur.line, cur.ch - 1), cur) == "<";
					});
		}
		function completeIfInTag(cm) {
			return completeAfter(
					cm,
					function() {
						var tok = cm.getTokenAt(cm.getCursor());
						if (tok.type == "string"
								&& (!/['"]/.test(tok.string
										.charAt(tok.string.length - 1)) || tok.string.length == 1))
							return false;
						var inner = CodeMirror.innerMode(cm.getMode(),
								tok.state).state;
						return inner.tagName;
					});
		}
		function saveFile(){
			$.ajax({
				type:"post",
				url:"<sn:webroot/>/report/file/save",
				data:{
					content:editor.getValue()
				},
				success:function(json){
					if(json.ret ==0){
						editor.setValue(json.c);
						var ss  = new Date();
						$("#tip").html("保存于：" + ss.getHours() + ":" + ss.getMinutes() + ":" + ss.getSeconds());
					}else{
						alert(json.msg);
					}
				},
				error:function(){
					
				}
			});
		}
		var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
			lineNumbers : true,
			mode : "xml",
			extraKeys : {
				"'<'" : completeAfter,
				"'/'" : completeIfAfterLt,
				"' '" : completeIfInTag,
				"'='" : completeIfInTag,
				"Ctrl-Space" : "autocomplete",
				"Ctrl-S":function(){
					saveFile();
				}
			},
			hintOptions : {
				schemaInfo : tags
			}
		});
		editor.setSize('100%', '90%');
		
		$(function(){
			$("#btnSave").bind("click",function(){
				saveFile();
			});
		});
	</script>
</body>
</html>
