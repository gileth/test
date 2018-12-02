<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<script language="JavaScript">
    var url = '<%=request.getAttribute("url")%>';
    var msg = '<%=request.getAttribute("message")%>';
    if (msg && msg != 'null') {
        <%out.println(request.getAttribute("message"));%>
        setTimeout(function () {
            window.location.href = url;
        }, 5000);
    } else {
        var uid = <%=request.getAttribute("uid")%>;
        var username = '<%=request.getAttribute("username")%>';
        var accessToken = '<%=request.getAttribute("accessToken")%>';
        if (uid && username) {
            localStorage.uid = uid;
            localStorage.username = username;
            localStorage.accessToken = accessToken;
        }
        window.location.href = url;
    }
</script>
</body>
</html>
