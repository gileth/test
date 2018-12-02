<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<%exception.printStackTrace();%>
<div style="height: 434px;width: 100%;" align="center">

    <div style="width: 800px; height: 434px;">
        <div style="padding-top: 60px;">
            错误提示：<br/>
            <div id="errorMsg"><%=exception.getMessage()%></div>
        </div>
    </div>


</div>
</html>