<%@ page language="java" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="Content-Language" content="zh-cn">
    <meta name="apple-mobile-web-app-capable" content="no"/>
    <meta name="apple-touch-fullscreen" content="yes"/>
    <meta name="format-detection" content="telephone=no,email=no"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="white">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <title><%=request.getAttribute("payName")%>扫码支付 - 码支付</title>
    <link href="./css/wechat_pay.css" rel="stylesheet" media="screen">
    <script>
    	var BASE_PATH = '<%=request.getAttribute("basePath")%>';
    	var account = '<%=request.getAttribute("account")%>';
    	var PubUserName = '<%=request.getAttribute("pubUserName")%>';
    	var price = <%=request.getAttribute("payMoney")%>;
    	<% 
    		int type = 1; 
    		int payType = (int)request.getAttribute("payType");
    		if(payType == 1) { type = 1; }else if(payType == 3){ type = 2; }
    	%>
   		var type = <%=type%>;
        var PKEY = '<%=request.getAttribute("pkey")%>';
        var MKEY = '<%=request.getAttribute("mkey")%>';
        var SKEY = '<%=request.getAttribute("skey")%>';
    </script>
</head>

<body>
<script language="JavaScript">
	if (500 == <%=request.getAttribute("code")%>) {
	    window.location.href = '/#/tab/account/recharge';
	}
</script>
<div class="body">
    <h1 class="mod-title">
        <span class="ico_log ico-<%=request.getAttribute("payType")%>"></span>
    </h1>

    <div class="mod-ct">
        <div class="order">
        </div>
        <div class="amount" id="money">￥<%=request.getAttribute("payMoney")%></div>
       <div class="qrcode-img-wrapper" data-role="qrPayImgWrapper">
            <div data-role="qrPayImg" class="qrcode-img-area">
                <div class="ui-loading qrcode-loading" data-role="qrPayImgLoading" style="display: none;">加载中</div>
                 <div style="position: relative;display: inline-block;">
                    <img id='show_qrcode' alt="加载中..." src="./img/xxx_<%=request.getAttribute("payType")%>.png" width="210" height="210" style="display: block;">
                    <img onclick="$('#use').hide()" id="use"
                         src='./img/use_<%=request.getAttribute("payType")%>.png'
                         style="position: absolute;top: 50%;left: 50%;width:32px;height:32px;margin-left: -21px;margin-top: -21px">
                </div>
            </div>
        </div>
        
        <div class="tip">
            <div class="ico-scan"></div>
            <div class="tip-text">
                <p>请使用<%=request.getAttribute("payName")%>扫一扫</p>
                <p>扫描二维码完成支付</p>
            </div>
        </div>
        <div class="time-item" id="msg">
            <h1>订单过期时间</h1>
            <strong id="hour_show">0时</strong>
            <strong id="minute_show">0分</strong>
            <strong id="second_show">0秒</strong>
        </div>
		<div class="detail detail-open" id="orderDetail">
            <dl class="detail-ct" id="desc" style="display: block;">
            </dl>
        </div>
		<h1>
			<div class="tps_btn" style="padding-top: 10px;">
			<a href="" id="payBtn" target="_blank" style="color: #fff;text-decoration: none;
			 text-align: center;padding: .55rem 0; display: inline-block; width: 88%; border-radius: .3rem; 
			 font-size: 14px;background-color: #428bca; border: 1px solid #428bca;letter-spacing:normal;
			 font-weight: normal">立即开启<%=request.getAttribute("payName")%>支付</a></div>
		</h1>
    </div>

</div>


<!--注意下面加载顺序 顺序错乱会影响业务-->
<script src="./js/jquery-1.10.2.min.js"></script>
<!--[if lt IE 8]>
<script src="./js/json3.min.js"></script><![endif]-->
<script src="./js/q.min.js"></script>
<script src="./js/pay-common.js"></script>
<script src="./js/codepay_util.js"></script>
<script>
    setTimeout(function () {
        //$('#use').hide()
    },1000)
</script>
</body>
</html>