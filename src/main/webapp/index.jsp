<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sn" uri="http://www.sumscope.com/taglib"%>
<html>
<head>
</head>
<body>
<%
response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);   
response.setHeader("Location",request.getContextPath() + "/report/home/test");   
%>   
</body>
</html>
