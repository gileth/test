var PAY_BASE_PATH = BASE_PATH + "recharge/";
var ALIPAY_URL = PAY_BASE_PATH + "ali_h5_pay";
var VC_URL = PAY_BASE_PATH + "virtual_pay";
var WXPAY_URL = PAY_BASE_PATH + "wx_h5_pay";
var QRCODE_URL = PAY_BASE_PATH + "qrcode";
var QUERY_URL = PAY_BASE_PATH + "result";
var QUERY_URL2 = PAY_BASE_PATH + "aliresult";
var CREATE_QRCODE_URL = PAY_BASE_PATH + "qrcreate";
var skey,
    qrnum = 0, // 二维码请求次数
    callbackObj,
    callbackCode,
    order = {},
    orderParam = {},
    orderids = new Array(),
    statusShow,
    $qrcode,
    rctype;

var $payCommon = {
    // 创建订单
    createOrder : function($param) {
        var that = this,
            cip = $('#page-name').attr("data-ip");// 充值ip

        // 获取创建订单的参数
        if ($param.pkey) {
            rctype = $param.rtype;
            $qrcode = $param.qrcodeObj;
            // 获取渠道
            var chl = 0;
            // 获取ip
            if (!cip) {
                cip = $param.ip;
                if (!cip || cip === 'undefined') {
                    cip = '1.1.1.1';
                }
            }
            //使用公钥加密
            var url = ALIPAY_URL,
                paid = 0,
                psid = 0,
                etype = 0,
                cflag = false,
                data = $.trim($param.account) + "|" + $param.pid + "|" + $param.num,
                rsa = new JSEncrypt();
            rsa.setPublicKey($param.pkey);
            var akey = rsa.encrypt(data, $param.pkey);
            if($param.gaid) {
                paid = $param.gaid;
            }
            if($param.gsid) {
                psid = $param.gsid;
            }
            if($param.etype) {
                etype = $param.etype;
            }
            // 签名
            var sign = hex_md5(encodeURI($param.account + $param.pid + $param.num + paid + psid + etype
                + cip + $param.mprice + $param.gtype + $param.bal + chl) + $param.mkey);
            skey = sign + rctype;

            // 先验证订单是否存在
            if (order[skey]) {
                // 存在则直接取出订单号展示二维码
                var orderInfo = order[skey],
                    oid = orderInfo.id,
                    time = orderInfo.tm,
                    qr = orderInfo.qr;
                if (oid && time && (rctype == '3' || qr) && orderInfo.url == url) {
                    var timestamp = $.now();
                    // 已超过60分钟，订单过期
                    if ((timestamp-Number(time)) > (1000 * 60 * 60)) {
                        orderids.remove(oid);
                        order[skey] = null;
                        cflag = true;
                    } else if (rctype != '3') {
                        // 直接展示订单信息
                        that.showQrcode(qr, oid);
                    }
                } else {
                    order[skey] = null;
                    cflag = true;
                }
            } else {
                cflag = true;
            }
            if (cflag) {
                var param = {
                    uacc: $param.account,
                    akey: akey,
                    aid: paid,
                    sid: psid,
                    etype: etype,
                    reqip: cip,
                    mprice: $param.mprice,
                    gtype: $param.gtype,
                    bal: $param.bal,
                    chl: chl,
                    type: 0,
                    rctype : rctype,
                    sign: sign
                };
				if (rctype == '2') {
					url = WXPAY_URL;
				} else if (rctype == '3') {
					url = VC_URL;
				}
				$.ajax({
					dataType: "json",
					type: "post",
					url: url + "?v=" + new Date().getTime(),
					data: param,
//					async: false,
//					cache:false,
					success: function ($dt) {
						if ($dt.status == 'success') {
							var orderid = $dt.data;
							qrnum = 0;
							that.setOrderParam(orderid, param, $param);
							if (rctype != '3') {
								 that.getQrcode(orderid, url, $param);
							} else {
								if (orderids.length >= 10) {
									var firstId = orderids[0];
									var key = orderParam[firstId].param.sign + orderParam[firstId].rtype;
									orderParam[firstId] = {};
									order[key] = null;
									orderids.shift();
								}
								order[skey] = {
									id: orderid,
									tm: $.now()
								};
								orderids.push(orderid);
							}
						}
					},
					error: function (e) {
						console.log(e);
					}
				});
            }
        }
        if (!callbackObj) {
            that.callbackResult();
        }
    },
    setOrderParam : function (orderid, param, $param) {
        orderParam[orderid] = {};
        orderParam[orderid].param = param;
        orderParam[orderid].time = $.now();
        orderParam[orderid].pid = $param.pid;
        orderParam[orderid].pname = $param.pname;
        orderParam[orderid].num = $param.num;
        orderParam[orderid].mprice = $param.mprice;
        orderParam[orderid].acc = $param.racc;
        orderParam[orderid].account = $param.account;
        orderParam[orderid].money = $param.totalMoney;
        orderParam[orderid].rtype = $param.rtype;

    },
    // 获取支付二维码
    getQrcode : function ($data, url, $param) {
        if (rctype != orderParam[$data].rtype) {
            return;
        }
        var that = this;
        $.ajax({
            dataType : "json",
            type: "post",
            url: QRCODE_URL + "?v=" + new Date().getTime(),
            data: {
                orderid: $data
            },
            async:false,
            cache:false,
            success: function ($dt) {
                if ($dt.status == 'success') {
                    if ($dt.qrcode != "" && $dt.qrcode != undefined && $dt.qrcode != null) {
                        if (orderids.length >= 10) {
                            var firstId = orderids[0];
                            var key = orderParam[firstId].param.sign + orderParam[firstId].rtype;
                            orderParam[firstId] = {};
                            order[key] = null;
                            orderids.shift();
                        }
                        // 设置已生成的订单信息
                        order[skey] = {
                            id : $data,
                            tm : $.now(),
                            qr : $dt.qrcode,
                            url : url
                        };
                        orderids.push($data);
                        show_order($dat,$dt.qrcode)
                    } else {
                        callbackCode = setTimeout(function(){
                            if (qrnum <= 20) {
                                that.getQrcode($data, url, $param);
                                qrnum ++;
                            } else {
                                that.createOrder($param);
                            }
                        },1000);
                    }
                } else {
                	//that.getQrcode($data, url, $param);
                }
            },
            error: function() {
            	//that.createOrder($param);
            }
        });
    },
    checkUsable : function($orderid) {
        var that = this,
            timestamp = $.now(),
            ctime = orderParam[$orderid].time,
            sign = orderParam[$orderid].param.sign;
        // 已超过19分钟，订单过期
        if (((timestamp-Number(ctime)) > (1000 * 60 * 19)) && !orderParam[$orderid].status) {
            // 重新生成订单并清空缓存的订单二维码等信息
            var key = sign + "1";
            order[key] = null;
            orderids.remove($orderid);
            that.createOrder(orderParam[$orderid].param);
            return false;
        }
        return true;
    },
    // 获取支付结果
    query : function ($orderid, url) {
        var that = this;
        $.ajax({
            dataType: "json",
            type: "post",
            url: url + "?v=" + new Date().getTime(),
            data: {
                orderid: $orderid
            },
            async:false,
            cache:false,
            success: function ($dt) {
                var orderId = $dt.result;
                // 支付成功
                if ($dt.status == 'success' || $dt.status == 'complete' || $dt.status == 'ye') {
                    $qrcode.hide();
                    $qrcode.next().html('支付成功');
                    orderParam[orderId].status = $dt.status;
                    // 展示交易状态
                    if (!statusShow) {
                        if ( $dt.status == 'ye') {
                            that.paySuccess("由于您下单后余额支付金额不足，系统自动将此次支付金额充值到余额。您可以继续使用余额购买金钥匙。");
                        } else {
                            that.paySuccess("恭喜您购买金钥匙成功");
                        }
                    }
                    // 等待支付
                } else if ($dt.status == 'wait') {
                    // 余额不足
                } else if ($dt.status == 'notbalance') {
                    that.paySuccess("余额不足");
                } else {
                    // 支付失败
                    clearTimeout(callbackObj);
                    // 解除锁屏层
                    layer.closeAll('dialog');
                    layui.layer.msg("当前充值用户过多，请稍后再试");
                }
            }
        });
    },
    // 查询结果回调
    callbackResult : function () {
        // 每5秒钟回调一次
        var that = this;
        callbackObj = setInterval(function () {
            if (orderids) {
                var ids = "";
                for (var i = 0; i < orderids.length; i++) {
                    if (that.checkUsable(orderids[i])) {
                        ids += orderids[i] + ",";
                    }
                    // that.query(orderids[i], QUERY_URL);
                }
                if (ids != "") {
                    that.query(ids, QUERY_URL);
                }
            }
            // if (queryReqno) {
            //     that.query(queryReqno, QUERY_URL2);
            // }
        }, 3000);
    },
    //提示弹窗
    checktip : function(msg){
        var that = this;
        var index = layer.open({
            type:1,
            title : "",
            closeBtn: 0,
            shade : 0.5,
            content : $(".tip-dialog"),
            area :["458px","308px"],
            success : function(layero,index){
                $(layero).find(".tip").html(msg);
                $("#layui-layer"+index).css({"background":"none","boxShadow":"none"});
                $(layero).find(".cancel").on("click",function(){
                    layer.close(index);
                    // window.location.reload();
                    reload();
                    $(".tip-dialog").hide();
                });

                $(layero).find(".know").on("click",function(){
                    layer.close(index);
                    // window.location.reload();
                    reload();
                    $(".tip-dialog").hide();
                });
            }
        });
    },
    // 支付成功
    paySuccess : function(msg){
    	var that = this;
        // 关闭支付弹窗
        layer.closeAll('dialog');
        clearTimeout(callbackObj);
        //打开购买金钥匙成功弹窗
        that.checktip(msg);
    },
    // 判断浏览器
    myBrowser : function (){
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
        var isOpera = userAgent.indexOf("Opera") > -1; //判断是否Opera浏览器
        if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
            return "IE";
        } //判断是否IE浏览器
        return "";
    },

    // 乘法
    multiply : function (arg1,arg2) {
        if (arg1 && arg2) {
            var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
            if (s1.indexOf(".") != -1) {
                m += s1.split(".")[1].length;
            }
            if (s2.indexOf(".") != -1) {
                m += s2.split(".")[1].length;
            }
            return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
        } else {
            return 0;
        }
    }
};

show_order = function($dt,url){
	var data = {"pay_id":PubUserName,"money":price,"order_id":$dt.orderid};
	show_desc(data);
	$("payBtn").find("a").attr("href",url);
	$("payBtn").show();
}

show_desc = function (data) { //商品描述
    var html = '';
    html += getDescMode('账号', data.pay_id);
    html += getDescMode('金额', "￥" + data.money);
    html += getDescMode('金币', data.money*1);
    html += getDescMode('云端单号', data.order_id);
    html += getDescMode('创建时间', getNowFormatDate());
//    html += getDescMode('过期时间', myDate("y-m-d h:m:s", data.endTime));
    $("#desc").html(html);
}
