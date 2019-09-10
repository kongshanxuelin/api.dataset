<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sn" uri="http://www.sumscope.com/taglib"%>
<html>
<head>
<script type="text/javascript"
	src="<sn:webroot/>/js/jquery-1.11.1.min.js"></script>
<link rel="stylesheet" href="<sn:webroot/>/js/codemirror.css">
<script src="<sn:webroot/>/js/codemirror.js"></script>
<script src="<sn:webroot/>/js/mode/xml/xml.js"></script>

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
<script src="<sn:webroot/>/js/jquery.ocupload-1.1.2.js"></script>

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
#tip {
	font-size:12px;
	color: #155724;
	margin-left:10px;
}
#btnSave,#btnImpExcel {
    display: inline-block;
    font-weight: 400;
    color: #212529;
    background-color: #ffc107;
    border-color: #ffc107;
        border: 1px solid transparent;
    padding: .175rem .35rem;
    font-size: 1rem;
    line-height: 1.5;
    border-radius: .25rem;
        user-select: none;
    transition: color .15s ease-in-out,background-color .15s ease-in-out,border-color .15s ease-in-out,box-shadow .15s ease-in-out;
}
</style>
</head>
<body>
	<div style="margin:5px 20px;">
		<table width="100%">
			<tr>
				<td width="50%">配置文件所在路径：<span class="path">${path }</span></td>
				<td align="right" style="padding-right:10px;">
					<span id="tip"></span>
					<input style="margin-left:10px;" type="button" id="btnSave" value="保存 Ctrl+S" />
					<input type="button" id="btnImpExcel" value="导入Excel" />
				</td>
			</tr>
		</table>
	</div>
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
					"java-align-data":["true","false"],
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
					fontWeight : [ "normal", "bold" ],
					lang:["sql","js"]
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
			lineWrapping: true,
			firstLineNumber: 1,
			autofocus: true,
			lineNumbers : true,
			styleActiveLine: true,
			matchTags: {bothTags: true},
			showCursorWhenSelecting:true,
			foldGutter: true,
			gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
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
		editor.setSize('100%', (document.body.clientHeight-120) + 'px');
		
		$(function(){
			$("#btnSave").bind("click",function(){
				saveFile();
			});
			$("#btnImpExcel").upload({
                name: 'file',
                action: '<sn:webroot/>/excel/upload',
                enctype: 'multipart/form-data',
                params: {},
                autoSubmit: true,
                onSubmit: function() {},
                onComplete: function(data) {
                	console.log("onComplete:",data);
                	data = JSON.parse(data);
                	if(data.code === 0){
                		alert("导入成功！");
                	}else{
                		alert("导入失败:"+data.error);
                	}
                },
                onSelect: function() {
                	this.autoSubmit=false;
                	var regex =/^.*\.(?:xls|xlsx)$/i;
                	if(regex.test($("[name = '"+this.name()+"']").val())){
                        this.submit();
                    }else{
                        alert("请选择一个Excel文件！");
                    }
                }
        	});
		});
	</script>
</body>
</html>
