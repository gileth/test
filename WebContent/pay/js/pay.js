var $recharge={
    onRecharge : function ($data) {
        //避免重复提交
        $that.disabledBtn($(".pay-btn"));
        $that.orderPay($data);
    },
    orderPay : function($data){
        var $that = this;
        
        // 获取渠道
        var chl = 0;
        // 获取ip
        if (!cip) {
            cip = $data.ip;
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
            data = $.trim($data.account) + "|" + $data.pid + "|" + $data.num,
            rsa = new JSEncrypt();
        rsa.setPublicKey($data.pkey);
        var akey = rsa.encrypt(data, $data.pkey);
        if($param.gaid) {
            paid = $data.gaid;
        }
        if($param.gsid) {
            psid = $data.gsid;
        }
        if($param.etype) {
            etype = $data.etype;
        }
        // 签名
        var sign = hex_md5(encodeURI($data.account + $data.pid + $data.num + paid + psid + etype
            + cip + $data.mprice + $data.gtype + $data.bal + chl) + $data.mkey);
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
                    uacc: $data.account,
                    akey: akey,
                    aid: paid,
                    sid: psid,
                    etype: etype,
                    reqip: cip,
                    mprice: $data.mprice,
                    gtype: $data.gtype,
                    bal: $data.bal,
                    chl: chl,
                    type: 0,
                    rctype : rctype,
                    sign: sign
                };
            //微信支付
            if(data.type == 3){
                //t_n:游戏账号
                console.log("微信");
                if(this.isWeiXin()){
                    var url = BASE_PATH + "recharge/wx_pay";
                    $.ajax({
                        type:"post",
                        url:url,
                        data:param,
                        success:function(data){
                            //联网成功的回调
                            if(data.status == 'success'){
                                $that.wxpay(data.data);
                            }
                        },
                        error:function(xhr){
                            //联网失败的回调
                            console.log(xhr);
                            this.orderPay();
                        }
                    });
                }else{
                    console.log("不是来自微信内置浏览器");
                    if (this.isMobile()){
                        $that.getPayPage(param);
                    }else{
                        $.ajax({
                            dataType: "json",
                            type: "post",
                            url: BASE_PATH + "order/create-order",
                            data: param,
                            async:false,
                            success: function ($dt) {
                                var $qrcodeImg = $("#qrcode_div").find('.qrcode-img');
                                $qrcodeImg.empty();
                                if ($dt.status == 'success') {
                                    clearTimeout(callbackCode);
                                    qrnum = 0;
                                    qynum = 0;
                                    clearTimeout(callbackObj);
                                    $that.getQrcode($dt.data,param.type, data, totype);
                                }
                            },
                            error: function(xhr){
                                console.log(xhr);
                            }
                        });
                    }
                }
            }else if($zf.hasClass("icon-duigou")){
                //支付宝支付
                console.log("支付宝");
                param.type="ALIPAY";
                if(this.isWeiXin()){
                    console.log("来自微信内置浏览器调用支付宝支付");
                    $.ajax({
                        type:"post",
                        dataType: "json",
                        url:BASE_PATH + "order/pay",
                        data:param,
                        success:function(result){
                            if(result.status == 'success'){
                                window.location.href = BASE_PATH + "/order/wx-alipay.html?goto="+result.url;
                            }
                        },
                        error:function(xhr){
                            //联网失败的回调
                            console.log(xhr);
                        }
                    });
                }else{
                    if (this.isMobile()){
                        $that.getPayPage(param);
                    }else{
                        $.ajax({
                            dataType: "json",
                            type: "post",
                            url: BASE_PATH + "order/create-order",
                            data: param,
                            async:false,
                            success: function ($dt) {
                                var $qrcodeImg = $("#qrcode_div").find('.qrcode-img');
                                $qrcodeImg.empty();
                                if ($dt.status == 'success') {
                                    clearTimeout(callbackCode);
                                    qrnum = 0;
                                    qynum = 0;
                                    clearTimeout(callbackObj);
                                    $that.getQrcode($dt.data,param.type, data, totype);
                                }
                            },
                            error: function(xhr){
                                console.log(xhr);
                            }
                        });
                    }

                }
            }
        }
        if (!callbackObj) {
        	$that.callbackResult();
        }
    },
    getPayPage: function(param){
        var $that = this;
        $.ajax({
            type:"post",
            dataType: "json",
            url:BASE_PATH + "reacharge/pay",
            data:param,
            async:true,
            success:function(result){
                if(result.status == 'success'){
                    var url = result.url;
                    if(!$that.isWeiXin() && $that.isMobile()){
                        top.document.location.href = url;
                    }else{
                        window.location.href = url;
                    }
                }
            },
            error:function(xhr){
                //联网失败的回调
                console.log(xhr);
            }
        });
    },
    // 刷新二维码
    refreshQrcode : function($qrcodeImg,orno, tp, data, totype) {
        var $that = this;
        clearTimeout(callbackCode);
        $qrcodeImg.html('支付码获取失败<br/>请<a class="refresh blue" href="javascript:void(0);">刷新</a>重试');
        $qrcodeImg.find('.refresh').on('click', function () {
            qrnum = 0;
            clearTimeout(callbackCode);
            $that.getQrcode(orno, tp, data, totype);
        });
    },
    //获取二维码
    getQrcode : function (orno, tp, data, totype) {
        // console.log("orno="+orno);
        var $that = this;
        var $qrcodeImg = $("#qrcode_div").find('.qrcode-img');
        $.ajax({
            dataType: "json",
            type: "post",
            url: BASE_PATH + "order/qrcode",
            data: { or_no: orno},
            async:false,
            success: function ($dt) {
                $qrcodeImg.html('<img class="waitpng" src="' + BASE_RES + 'plug/mescroll/mescroll-progress.jpg"/>');
                $(".shade").find(".money").html('扫一扫付款 <span class="s-f">'+data.fee+'</span> 元');
                $(".shade").show();
                $("#qrcode_div").find('.qrcode-tip').show();
                if(tp == 'WEIXIN'){
                    $("#qrcode_div").find('.qrcode-tip').find(".right").empty().html("打开手机微信<br />扫一扫继续付款");
                }else{
                    $("#qrcode_div").find(".qrcode-tip").find(".right").empty().html("打开手机支付宝<br />扫一扫继续付款");
                }
                layer.closeAll();
                if ($dt.status == 'success') {
                    if($dt.qrcode != '' && $dt.qrcode != null){
                        $qrcodeImg.empty().qrcode({
                            render: "canvas",
                            text: $dt.qrcode,
                            width: 160,
                            height: 160
                        });
                        $qrcodeImg.find("canvas").css({"width":"3rem","height":"3rem"});
                        clearTimeout(callbackCode);
                        $that.query(orno, data, totype);
                    }else{
                        callbackCode = setTimeout(function(){
                            if (qrnum <= 4) {
                                $that.getQrcode(orno, tp, data, totype);
                                qrnum ++;
                            }
                        },1000);
                    }

                } else {
                    $that.refreshQrcode($qrcodeImg,orno, tp, data, totype);
                }
            },
            error: function(xhr){
                $that.refreshQrcode($qrcodeImg,orno, tp, data, totype);
            }
        });
    },
    // 查询结果回调
    callback: function(orno, data,totype) {
        var $this = this;
        // 每5秒钟回调一次
        var time = 5000 + qynum * 1000;
        callbackObj = setTimeout(function(){
            if (qynum <= 30) {
                $this.query(orno, data, totype);
                qynum ++;
            } else {
                clearTimeout(callbackObj);
            }
        },time);
    },
    // 获取支付结果
    query: function (orno, data, totype) {
        var $this = this;
        if (orno) {
            $.ajax({
                dataType : "json",
                type: "post",
                url: BASE_PATH + "order/queryOrder",
                data:{or_no: orno},
                success: function ($dt) {
                    // 支付成功
                    if ($dt.status == 'success' || $dt.status == 'complete') {
                        clearTimeout(callbackObj);
                        $("#qrcode_div").find('.qrcode-tip').hide();
                        $("#qrcode_div").find(".qrcode-img").html('<div class="pay-success"><i class="iconfont icon-duigou"></i> <p>支付成功</p></div>');
                        setTimeout(function(){
                            // console.log("支付成功");
                            $(".shade").hide();
                            if(totype == '58coin'){
                                window.location.href = BASE_PATH + "myInfo";
                            }
                        },10000);
                        // 等待支付
                    } else if ($dt.status == 'wait') {
                        $this.callback(orno, data, totype);
                    } else {
                        // 支付失败
                        clearTimeout(callbackObj);
                    }
                }
            });
        }
    },
    qrcodeDivClose:function(){
        $("#qrcode_div").find(".close").click(function(){
            $(".shade").hide();
        });
    },
    //微信内置浏览器支付
    onBridgeReady : function (data,totype){
        layer.closeAll();
        var paramdata = {
            appId : data.appId,
            nonceStr : data.nonceStr,
            package : data.package,
            signType : data.signType,
            timeStamp : data.timeStamp,
            paySign : data.paySign
        };
        WeixinJSBridge.invoke(
            'getBrandWCPayRequest', paramdata,function(res){
                console.log(res);
                if(res.err_msg == "get_brand_wcpay_request:ok" ) {
                    if(totype == '58coin'){
                        window.location.href = BASE_PATH + "myInfo";
                    }
                }
                // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
            }
        );
    },
    //微信内置浏览器支付
    wxpay : function (data, totype){
        var $that = this;
        if (typeof WeixinJSBridge == "undefined"){
            if( document.addEventListener ){
                document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
            }else if (document.attachEvent){
                document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
            }
        }else{
            $that.onBridgeReady(data, totype);
        }
    },
    //浏览器设备是移动端还是pc端
    isMobile : function() {
        var sUserAgent = navigator.userAgent.toLowerCase();
        var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
        var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
        var bIsMidp = sUserAgent.match(/midp/i) == "midp";
        var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
        var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
        var bIsAndroid = sUserAgent.match(/android/i) == "android";
        var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
        var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
        if (bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {
            return true;
        } else {
            return false;
        }
    },
    //判断是否微信内置浏览器
    isWeiXin : function() {
        var ua = window.navigator.userAgent.toLowerCase();
        if (ua.match(/MicroMessenger/i) == 'micromessenger') {
            return true;
        } else {
            return false;
        }
    },
    // 避免重复提交
    disabledBtn : function($btn) {
        $btn.attr("disabled", "disabled");
        setTimeout(function(){
            $btn.removeAttr("disabled");
        },2000);
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

    }
};