<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sn" uri="http://www.sumscope.com/taglib"%>
<html>
<head>
	<link href="<sn:webroot/>/table-ui/css/tabulator.min.css" rel="stylesheet">
	<script type="text/javascript" src="<sn:webroot/>/js/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="<sn:webroot/>/table-ui/js/tabulator.min.js"></script>
	<style type="text/css">
		body{
			margin:10px;
		}
		.header {
			margin:5px 10px;
		}
		.footer {
			margin:5px 10px;
		}
	</style>
</head>
<body>
	<div class="header">
		${uiHeader }
	</div>
	<div id="${fileName}_${apiId}">
	</div>
	<div class="footer">
		${uiFooter }
	</div>
	<script type="text/javascript">
		var id = "${fileName}_${apiId}";
		var table = null;
		$(function(){
			var params = ${params!=null?params:"{}"};
			params.apiId = "${apiId}";
			params.fileName = "${fileName}";
			$.get("<sn:webroot />/report/ui",params,function(result){
				console.log(result.fields.length);
				if(!result.res){
					$("#"+id).html("<p>执行异常："+result.msg+"</p>");
				}else if(result.fields.length<1){
					$("#"+id).html("<p>没有数据！</p>");
				}else{
					var fields = result.fields;
					var titles = result.titles;
					var _columns = [];
					$.each(fields,function(i,n){
						if(i ==0)
							_columns.push({title:titles[i], field:fields[i],hozAlign:"left",formatter:"textarea",frozen:true});
						else
							_columns.push({title:titles[i], field:fields[i],hozAlign:"left",formatter:"textarea"});
					});

					table = new Tabulator("#"+id, {
						height:"600px",
						layout:"fitColumns",
						columnHeaderHozAlign:"center",
						cellHozAlign:"center",
						cellVertAlign:"middle",
					    layout:"fitColumns",
					    placeholder:"查无数据",
					    columns:_columns
					});
					table.setData(result.result);
					
					//start customize js loaded
					${uiJs }
				}
			});
		});
		
	</script>
</body>
</html>
