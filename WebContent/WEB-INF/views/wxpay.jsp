<%@ page language="java" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>微信支付</title>
</head>
<body>
<%
    String jsapiAppId = (String) request.getAttribute("appId");
    String timeStamp = (String) request.getAttribute("timeStamp");
    String nonceStr = (String) request.getAttribute("nonceStr");
    String jsapipackage = (String) request.getAttribute("package");
    String signType = (String) request.getAttribute("signType");
    String paySign = (String) request.getAttribute("paySign");
%>
</body>
<script language="JavaScript">
    if (500 == <%=request.getAttribute("code")%>) {
        alert("支付失败, 请重试!");
        window.location.href = '/#/tab/account';
    } else {
        function onBridgeReady() {
            WeixinJSBridge.invoke(
                    'getBrandWCPayRequest', {
                        'appId': '<%=jsapiAppId%>',
                        'timeStamp': '<%=timeStamp%>',
                        'nonceStr': '<%=nonceStr%>',
                        'package': '<%=jsapipackage%>',
                        'signType': '<%=signType%>',
                        'paySign': '<%=paySign%>'
                    }, function (res) {
                        if(res.err_msg == 'get_brand_wcpay_request:ok') {
                            alert('支付成功, 因银行业务延时, 将会在30分钟内到账, 请注意查收');
                        } else  if (res.err_msg == 'get_brand_wcpay_request:fail') {
                            alert('支付失败, 请稍后重试.');
                        }
                        window.location.href = '/#/tab/account';
                    });
        }
        if (typeof WeixinJSBridge == "undefined") {
            if (document.addEventListener) {
                document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
            } else if (document.attachEvent) {
                document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
            }
        } else {
            onBridgeReady();
        }
    }
</script>
</html>