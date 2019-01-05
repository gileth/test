var myTimer;
function timer(intDiff) {
    var i = 0;
    myTimer = window.setInterval(function () {
        i++;
        var day = 0,
            hour = 0,
            minute = 0,
            second = 0;//时间默认值
        if (intDiff > 0) {
            day = Math.floor(intDiff / (60 * 60 * 24));
            hour = Math.floor(intDiff / (60 * 60)) - (day * 24);
            minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);
            second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
        }
        if (minute <= 9) minute = '0' + minute;
        if (second <= 9) second = '0' + second;
        $('#hour_show').html('<s id="h"></s>' + hour + '时');
        $('#minute_show').html('<s></s>' + minute + '分');
        $('#second_show').html('<s></s>' + second + '秒');
        if (hour <= 0 && minute <= 0 && second <= 0) {
            qrcode_timeout()
            clearInterval(myTimer);

        }
        intDiff--;
    }, 1000);
}


try {
    document.ontouchstart = function () {
        $('#use').hide();
    }
} catch (e) {

}
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
        + " " + date.getHours() + seperator2 + date.getMinutes()
        + seperator2 + date.getSeconds();
    return currentdate;
}
myDate = function (s, t, g) {
    if (t == "null") {
        return "?"
    }
    s = "-" + s;
    s = s.toLocaleLowerCase();
    a = s.indexOf("y");
    b = s.indexOf("-m");
    c = s.indexOf("d");
    d = s.indexOf("h");
    e = s.indexOf(":m");
    f = s.indexOf("s");
    t ? date = new Date(t * 1000) : date = new Date();
    b < 0 ? as = "" : as = "-";
    c < 0 ? bs = "" : bs = "-";
    d < 0 ? cs = "" : cs = " ";
    e < 0 ? ds = "" : ds = ":";
    f < 0 ? es = "" : es = ":";
    g ? g1 = ":00" : g1 = ""; //设置显示时分秒则显示否则不显示
    g ? g2 = ":00" : g2 = "";

    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    a >= 0 ? a = date.getFullYear() + as : a = "";
    b >= 0 ? b = month + bs : b = "";
    c >= 0 ? c = strDate + cs : c = "";
    d >= 0 ? d = date.getHours() + ds : d = g1;
    e >= 0 ? e = date.getMinutes() + es : e = g1;
    f >= 0 ? f = date.getSeconds() : f = g2;
    var currentdate = a + b + c + d + e + f;
    return currentdate;
}
qrcode_timeout = function () { //二维码超时则停止显示二维码
    $("#show_qrcode").attr("src", '');
    $("#show_qrcode").attr("alt", '二维码失效');

    $("#msg h1").html("支付超时 请重新提交订单"); //过期提醒信息
}
function getDescMode(key, value) {
    var reslut = value ? '<dt>' + key + '</dt><dd>' + value + '</dd>' : '';
    return reslut;
}
$(document).ready(function () {
    $(function () {
        timer(360);
    });
    //创建临时订单
   // createCodeAndPay();
    $('#orderDetail .arrow').click(function (event) {
        if ($('#orderDetail').hasClass('detail-open')) {
            $('#orderDetail .detail-ct').slideUp(500, function () {
                $('#orderDetail').removeClass('detail-open');
            });
        } else {
            $('#orderDetail .detail-ct').slideDown(500, function () {
                $('#orderDetail').addClass('detail-open');
            });
        }
    });
});

function createCodeAndPay(){
	// 组装参数
	var param = {
		account : account, // 用户平台账号
		pid : 1, // 产品id
		num : price*1, // 充值数量
		gaid : 0, // 游戏大区id
		gsid : 0, // 游戏区服id
		etype : 0, // 游戏充值类型
		mprice : 1, // 产品面额
		bal : 0, // 使用的余额
		gtype : '000', // 游戏类型 000：非游戏，100：游戏无区服，110：游戏有区无服，111：游戏有区服
		rtype : type, // 充值方式 1：支付宝扫码 2：微信扫码 3:余额
		pkey : PKEY, // RSA加密公钥
		ip : $('#page-name').attr("data-ip"), // 充值ip
		mkey : MKEY, // 充值签名MD5加密key
		pname : "金币", // 产品名称
		totalMoney : $payCommon.multiply(1,price) // 订单总金额
	};
	$payCommon.createOrder(param);
}