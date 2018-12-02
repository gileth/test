// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import java.text.SimpleDateFormat;
import cn.beecloud.BCCache;
import org.takeback.util.MD5StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import com.obaopay.util.EkaPayEncrypt;
import com.obaopay.util.StringUtils;
import com.obaopay.util.EkaPayConfig;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import org.springframework.web.util.WebUtils;
import java.util.Date;
import org.takeback.chat.entity.PubRecharge;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.takeback.util.annotation.AuthPassport;
import cn.beecloud.bean.BCOrder;
import org.takeback.pay.PaymentException;
import org.takeback.mvc.ResponseUtils;
import java.util.HashMap;
import org.takeback.pay.PayOrderFactory;
import java.util.UUID;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.takeback.thirdparty.support.XinTongConfig;
import org.takeback.thirdparty.support.KouDaiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.PubRechargeService;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping({ "/pay" })
public class PayController
{
    public static final Logger LOGGER;
    @Autowired
    private PubRechargeService pubRechargeService;
    @Autowired
    private KouDaiConfig kouDaiConfig;
    @Autowired
    private XinTongConfig xinTongConfig;
    private static long orderNum;
    private static String date;
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PayController.class);
        PayController.orderNum = 0L;
    }
    
    public PrintWriter getOut(final HttpServletResponse resp) {
        try {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("text/html");
            final PrintWriter out = resp.getWriter();
            return out;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply" }, method = { RequestMethod.POST })
    public ModelAndView payApply(@RequestBody final Map<String, String> data, final HttpSession session) {
        final String payChannel = data.get("payChannel");
        final String strTotalFee = data.get("totalFee");
        final String title = "\u5145\u503c";
        String identityId = null;
        if (payChannel.equals("YEE_WAP")) {
            identityId = UUID.randomUUID().toString().replace("-", "");
            session.setAttribute("identityId", (Object)identityId);
        }
        final Integer totalFee = (int)(double)Double.valueOf(strTotalFee);
        try {
            final BCOrder bcOrder = PayOrderFactory.getInstance().getPayOrder(payChannel, totalFee, title, identityId);
            final Map<String, String> result = new HashMap<String, String>();
            result.put("url", bcOrder.getUrl());
            result.put("html", bcOrder.getHtml());
            return ResponseUtils.jsonView(result);
        }
        catch (PaymentException e) {
            PayController.LOGGER.error("Failed to apply payment transaction", (Throwable)e);
            return ResponseUtils.jsonView(500, "\u652f\u4ed8\u5931\u8d25.");
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply/wx" }, method = { RequestMethod.POST })
    public ModelAndView payApplyWx(@RequestBody final Map<String, String> data) {
        final String strTotalFee = data.get("totalFee");
        final String title = "\u5145\u503c";
        final double totalFee = Double.valueOf(strTotalFee);
        final String url = PayOrderFactory.getInstance().getWxAuthorizeUrl(title, totalFee);
        return ResponseUtils.jsonView(url);
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply/wx" }, method = { RequestMethod.GET })
    public ModelAndView payApplyWxRedirected(@RequestParam final double totalFee, @RequestParam final String code, final HttpServletRequest request) {
        final String title = "\u5145\u503c";
        BCOrder bcOrder = null;
        try {
            bcOrder = PayOrderFactory.getInstance().getWxJSPayOrder((int)totalFee, title, code);
        }
        catch (PaymentException e) {
            PayController.LOGGER.error("Failed to apply payment transaction", (Throwable)e);
            final ModelAndView modelAndView = new ModelAndView("wxpay", (Map)new HashMap<String, Integer>() {
                {
                    this.put("code", 500);
                }
            });
        }
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt(title);
        pubRecharge.setFee(totalFee);
        pubRecharge.setGoodsname(title);
        pubRecharge.setPayno(bcOrder.getObjectId());
        pubRecharge.setTradeno(bcOrder.getBillNo());
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("1");
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        pubRecharge.setUid(uid);
        final PubUser u = this.pubRechargeService.get(PubUser.class, uid);
        pubRecharge.setUserIdText(u.getUserId());
        this.pubRechargeService.addRechargeRecord(pubRecharge);
        final Map<String, String> map = (Map<String, String>)bcOrder.getWxJSAPIMap();
        return new ModelAndView("wxpay", (Map)map);
    }
    
    @RequestMapping(value = { "hrefbackurl" }, method = { RequestMethod.GET })
    public void hrefbackurl(final HttpServletRequest request, final HttpServletResponse response) {
        System.out.println("\u8fdb\u6765hrefbackurl");
        final String md5key = StringUtils.formatString(EkaPayConfig.key);
        final String orderid = StringUtils.formatString(request.getParameter("orderid"));
        final String opstate = StringUtils.formatString(request.getParameter("opstate"));
        final String ovalue = StringUtils.formatString(request.getParameter("ovalue"));
        final String sign = StringUtils.formatString(request.getParameter("sign"));
        final String sysorderid = StringUtils.formatString(request.getParameter("sysorderid"));
        final String completiontime = StringUtils.formatString(request.getParameter("completiontime"));
        final String attach = StringUtils.formatString(request.getParameter("attach"));
        final String msg = StringUtils.formatString(request.getParameter("msg"));
        if (!StringUtils.hasText(orderid) || !StringUtils.hasText(opstate) || !StringUtils.hasText(ovalue) || !StringUtils.hasText(sign)) {
            System.out.println("\u51fa\u95ee\u9898\u4e86orderid=" + orderid + "&opstate=" + opstate + "&ovalue=" + ovalue + "&sign=" + sign);
            this.getOut(response).println("opstate=-1");
            return;
        }
        System.out.println("\u6b63\u786e\u7684");
        final String checksign = EkaPayEncrypt.obaopayCardBackMd5Sign(orderid, opstate, ovalue, md5key);
        if (checksign.equals(sign)) {
            System.out.println("\u9a8c\u8bc1\u901a\u8fc7");
            if (opstate.equals("0") || opstate.equals("-3")) {
                final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(orderid);
                System.out.println("///////////////////////////////////////////////////" + pubRecharge.getStatus());
                if (pubRecharge.getStatus().equals("1")) {
                    System.out.println("\u8fdb\u6765\u8fd9\u91cc" + Double.valueOf(ovalue));
                    pubRecharge.setRealfee((double)Double.valueOf(ovalue));
                    this.pubRechargeService.setRechargeFinished(pubRecharge);
                }
                this.getOut(response).println("<script>alert('\u5145\u503c\u6210\u529f');location.href='http://www.6556hb.com/#/tab/account';</script>");
            }
            else {
                this.getOut(response).println("<script>alert('\u5145\u503c\u5931\u8d25');location.href='http://www.6556hb.com/#/tab/account';</script>");
            }
        }
        else {
            System.out.println("\u9a8c\u8bc1\u5931\u8d25");
            this.getOut(response).println("<script>alert('\u5145\u503c\u5931\u8d25');location.href='http://www.6556hb.com/#/tab/account';</script>");
        }
    }
    
    @RequestMapping(value = { "callbackurl" }, method = { RequestMethod.GET })
    public void callbackurl(final HttpServletRequest request, final HttpServletResponse response) {
        final String md5key = StringUtils.formatString(EkaPayConfig.key);
        final String orderid = StringUtils.formatString(request.getParameter("orderid"));
        final String opstate = StringUtils.formatString(request.getParameter("opstate"));
        final String ovalue = StringUtils.formatString(request.getParameter("ovalue"));
        final String sign = StringUtils.formatString(request.getParameter("sign"));
        final String sysorderid = StringUtils.formatString(request.getParameter("sysorderid"));
        final String completiontime = StringUtils.formatString(request.getParameter("completiontime"));
        final String attach = StringUtils.formatString(request.getParameter("attach"));
        final String msg = StringUtils.formatString(request.getParameter("msg"));
        if (!StringUtils.hasText(orderid) || !StringUtils.hasText(opstate) || !StringUtils.hasText(ovalue) || !StringUtils.hasText(sign)) {
            this.getOut(response).println("opstate=-1");
            return;
        }
        final String checksign = EkaPayEncrypt.obaopayCardBackMd5Sign(orderid, opstate, ovalue, md5key);
        if (checksign.equals(sign)) {
            if (opstate.equals("0")) {
                final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(orderid);
                System.out.println("**************************" + pubRecharge.getStatus());
                if (pubRecharge.getStatus().equals("1")) {
                    pubRecharge.setRealfee((double)Double.valueOf(ovalue));
                    this.pubRechargeService.setRechargeFinished(pubRecharge);
                }
            }
            this.getOut(response).println("opstate=0");
        }
        else {
            this.getOut(response).println("opstate=-2");
        }
    }
    
    @RequestMapping(value = { "goPay" }, method = { RequestMethod.POST })
    public ModelAndView goPay(@RequestBody final Map<String, String> params, final HttpServletRequest request, final HttpServletResponse response) {
        final Object payIndex = params.get("payIndex");
        final Object amount = params.get("amount");
        final String parter = StringUtils.formatString(EkaPayConfig.parter);
        final String md5key = StringUtils.formatString(EkaPayConfig.key);
        final String api_url = StringUtils.formatString(EkaPayConfig.bank_url);
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final String title = "\u5145\u503c";
        String orderid = new StringBuilder().append(uid).append(new Date().getTime()).toString();
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt(title);
        pubRecharge.setFee(Double.valueOf(amount.toString()));
        pubRecharge.setGoodsname(title);
        pubRecharge.setPayno(null);
        pubRecharge.setTradeno(orderid);
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("1");
        pubRecharge.setUid(uid);
        final PubUser u = this.pubRechargeService.get(PubUser.class, uid);
        pubRecharge.setUserIdText(u.getUserId());
        this.pubRechargeService.addRechargeRecord(pubRecharge);
        final String callbackurl = StringUtils.formatString("http://www.6556hb.com/pay/callbackurl");
        final String hrefbackurl = StringUtils.formatString("http://www.6556hb.com/pay/hrefbackurl");
        orderid = StringUtils.formatString(orderid);
        String type = "";
        final Integer pi = Integer.valueOf(payIndex.toString());
        if (pi == 0) {
            type = StringUtils.formatString("2099");
        }
        else if (pi == 1) {
            type = StringUtils.formatString("2098");
        }
        final String value = StringUtils.formatString(amount.toString());
        final String attach = "";
        final String sign = EkaPayEncrypt.obaopayBankMd5Sign(type, parter, value, orderid, callbackurl, md5key);
        final String payerIp = request.getRemoteAddr();
        final String str = "<form id=\"payBillForm\" action=\"" + api_url + "\" method=\"GET\">" + "<input type='hidden' name='parter'   value='" + parter + "'>" + "<input type='hidden' name='type' value='" + type + "'>" + "<input type='hidden' name='orderid' value='" + orderid + "'>" + "<input type='hidden' name='callbackurl'   value='" + callbackurl + "'>" + "<input type='hidden' name='hrefbackurl'   value='" + hrefbackurl + "'>" + "<input type='hidden' name='value'   value='" + value + "'>" + "<input type='hidden' name='attach'  value='" + attach + "'>" + "<input type='hidden' name='payerIp' value='" + payerIp + "'>" + "<input type='hidden' name='sign'   value='" + sign + "'>" + "</form>";
        return ResponseUtils.jsonView(str);
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply/koudai" }, method = { RequestMethod.POST })
    public ModelAndView payApplyKoudai(@RequestBody final Map<String, String> params, final HttpServletRequest request) {
        final Object totalFee = params.get("totalFee");
        if (totalFee == null || !NumberUtils.isNumber(totalFee.toString())) {
            return ResponseUtils.jsonView(500, "\u5145\u503c\u91d1\u989d\u4e0d\u5bf9\u3002");
        }
        final Double fee = NumberUtils.createDouble(totalFee.toString());
        if (fee < 2.0) {
            return ResponseUtils.jsonView(500, "\u5145\u503c\u91d1\u989d\u4e0d\u80fd\u5c0f\u4e8e\uffe52.00\u3002");
        }
        String title = "\u5145\u503c";
        if (params.get("title") != null) {
            title = params.get("title");
        }
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt(title);
        pubRecharge.setFee(fee);
        pubRecharge.setGoodsname(title);
        pubRecharge.setTradeno(UUID.randomUUID().toString().replace("-", ""));
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("1");
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        pubRecharge.setUid(uid);
        final PubUser u = this.pubRechargeService.get(PubUser.class, uid);
        pubRecharge.setUserIdText(u.getUserId());
        this.pubRechargeService.addRechargeRecord(pubRecharge);
        final String chanel = params.get("payChannel");
        final String price = String.format("%.2f", pubRecharge.getFee());
        String url = String.valueOf(this.kouDaiConfig.getRestApiAddress()) + "?P_UserId=" + this.kouDaiConfig.getPartnerId() + "&P_OrderId=" + pubRecharge.getTradeno() + "&P_FaceValue=" + price + "&P_Price=" + price + "&P_ChannelId=" + chanel + "&P_Quantity=1&P_Result_URL=" + this.kouDaiConfig.getGameServerBaseUrl() + "pay/feedback/koudai&P_PostKey=";
        final String raw = String.valueOf(this.kouDaiConfig.getPartnerId()) + "|" + pubRecharge.getTradeno() + "|||" + String.format("%.2f", pubRecharge.getFee()) + "|" + chanel + "|" + this.kouDaiConfig.getSecretCode();
        final String postKey = MD5StringUtil.MD5Encode(raw);
        url = String.valueOf(url) + postKey;
        return ResponseUtils.jsonView(url);
    }
    
    @RequestMapping(value = { "feedback/koudai" }, method = { RequestMethod.GET })
    public void koudaiCallback(@RequestParam("P_OrderId") final String tradeNo, @RequestParam("P_UserId") final String partnerId, @RequestParam("P_ErrCode") final int errorCode, @RequestParam(value = "P_ErrMsg", defaultValue = "") final String errorMsg, @RequestParam("P_FaceValue") final double totalFee, @RequestParam("P_ChannelId") final String chanelId, @RequestParam("P_PostKey") final String postKey0, @RequestParam("P_CardId") final String cardId, @RequestParam("P_CardPass") final String cardPass, final HttpServletResponse response) throws IOException {
        if (!partnerId.equals(this.kouDaiConfig.getPartnerId())) {
            PayController.LOGGER.error("Pay trade [{}] is not mine.", (Object)tradeNo);
            response.getOutputStream().println("errcode=0");
            return;
        }
        final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(tradeNo);
        if (errorCode != 0) {
            PayController.LOGGER.error("Pay trade [{}] failed: ", (Object)tradeNo, (Object)errorMsg);
        }
        else {
            if (pubRecharge.getStatus().equals("2")) {
                response.getOutputStream().println("errcode=0");
                return;
            }
            if (pubRecharge.getFee() != totalFee) {
                PayController.LOGGER.error("Total fee of trade [{}] is different with the pay trade, expect: {}, {} in fact", new Object[] { tradeNo, pubRecharge.getFee(), totalFee });
                return;
            }
            final String raw = String.valueOf(partnerId) + "|" + tradeNo + "|" + cardId + "|" + cardPass + "|" + String.format("%.5f", totalFee) + "|" + chanelId + "|" + this.kouDaiConfig.getSecretCode();
            final String postKey = MD5StringUtil.MD5Encode(raw);
            if (!postKey.equals(postKey0)) {
                PayController.LOGGER.error("Post key of trade [{}] not match, expected is: {}, {} in fact", new Object[] { tradeNo, postKey, postKey0 });
                return;
            }
            pubRecharge.setRealfee(totalFee);
            this.pubRechargeService.setRechargeFinished(pubRecharge);
        }
        response.getOutputStream().println("errcode=0");
    }
    
    @RequestMapping(value = { "feedback/koudai" }, method = { RequestMethod.POST })
    public void koudaiCallback0(@RequestBody final Map<String, Object> data, final HttpServletResponse response) throws IOException {
        final String tradeNo = data.get("P_OrderId");
        final String partnerId = data.get("P_UserId");
        if (!partnerId.equals(this.kouDaiConfig.getPartnerId())) {
            PayController.LOGGER.error("Pay trade is not mine.");
            response.getOutputStream().println("errcode=0");
            return;
        }
        final int errorCode = data.get("P_ErrCode");
        final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(tradeNo);
        if (errorCode != 0) {
            PayController.LOGGER.error("Pay trade failed: ", data.get("P_ErrMsg"));
        }
        else {
            if (pubRecharge.getStatus().equals("2")) {
                response.getOutputStream().println("errcode=0");
                return;
            }
            final double totalFee = data.get("P_FaceValue");
            final String chanelId = data.get("P_ChannelId");
            if (pubRecharge.getFee() != totalFee) {
                PayController.LOGGER.error("Total fee is different with the pay trade, expect: {}, {} in fact", (Object)pubRecharge.getFee(), (Object)totalFee);
                return;
            }
            final String raw = String.valueOf(partnerId) + "|" + tradeNo + "|||" + String.format("%.2f", totalFee) + "|" + chanelId + "|" + this.kouDaiConfig.getSecretCode();
            final String postKey = MD5StringUtil.MD5Encode(raw);
            if (!postKey.equals(data.get("P_PostKey"))) {
                PayController.LOGGER.error("Post key not match, expected is: {}, {} in fact", (Object)postKey, data.get("P_PostKey"));
                return;
            }
            pubRecharge.setRealfee(totalFee);
            this.pubRechargeService.setRechargeFinished(pubRecharge);
        }
        response.getOutputStream().println("errcode=0");
    }
    
    @RequestMapping(value = { "webhook" }, method = { RequestMethod.POST })
    public void callback(@RequestBody final Map<String, Object> data, final HttpServletResponse response) throws IOException {
        final String sign = data.get("sign");
        final Long timestamp = data.get("timestamp");
        final String transactionId = data.get("transaction_id");
        final String text = String.valueOf(BCCache.getAppID()) + BCCache.getAppSecret() + timestamp;
        final String mySign = MD5StringUtil.MD5EncodeUTF8(text);
        final long timeDifference = System.currentTimeMillis() - timestamp;
        if (!mySign.equals(sign) || timeDifference > 300000L) {
            PayController.LOGGER.error("Sign validation failed: {}.", (Object)transactionId);
            response.getOutputStream().println("fail");
            return;
        }
        final Boolean success = data.get("trade_success");
        if (success) {
            final String transactionType = data.get("transaction_type");
            if (transactionType.equals("PAY")) {
                final Integer transactionFee = data.get("transaction_fee");
                final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(transactionId);
                if (pubRecharge == null) {
                    response.getOutputStream().println("fail");
                    return;
                }
                if (pubRecharge.getStatus().equals("2")) {
                    response.getOutputStream().println("success");
                    return;
                }
                pubRecharge.setRealfee((double)transactionFee);
                this.pubRechargeService.setRechargeFinished(pubRecharge);
                response.getOutputStream().println("success");
            }
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "apply/xintong" }, method = { RequestMethod.POST })
    public ModelAndView payApplyXintong(@RequestBody final Map<String, String> params, final HttpServletRequest request) {
        final Object totalFee = params.get("totalFee");
        if (totalFee == null || !NumberUtils.isNumber(totalFee.toString())) {
            return ResponseUtils.jsonView(500, "\u5145\u503c\u91d1\u989d\u4e0d\u5bf9\u3002");
        }
        final Double fee = NumberUtils.createDouble(totalFee.toString());
        String title = "\u5145\u503c";
        if (params.get("title") != null) {
            title = params.get("title");
        }
        final PubRecharge pubRecharge = new PubRecharge();
        pubRecharge.setStatus("1");
        pubRecharge.setDescpt(title);
        pubRecharge.setFee(fee);
        pubRecharge.setGoodsname(title);
        pubRecharge.setTradeno(getOrderNo());
        pubRecharge.setTradetime(new Date());
        pubRecharge.setGift(0.0);
        pubRecharge.setRechargeType("1");
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        pubRecharge.setUid(uid);
        final PubUser u = this.pubRechargeService.get(PubUser.class, uid);
        pubRecharge.setUserIdText(u.getUserId());
        this.pubRechargeService.addRechargeRecord(pubRecharge);
        final String price = String.format("%.2f", pubRecharge.getFee());
        final String type = params.get("payChannel");
        final String sign = "parter=" + this.xinTongConfig.getPartnerId() + "&type=" + type + "&value=" + price + "&orderid=" + pubRecharge.getTradeno() + "&callbackurl=" + this.xinTongConfig.getGameServerBaseUrl() + "pay/feedback/xintong";
        System.out.println(String.valueOf(sign) + this.xinTongConfig.getSecretCode());
        final String signKey = MD5StringUtil.MD5Encode(String.valueOf(sign) + this.xinTongConfig.getSecretCode());
        final String url = String.valueOf(this.xinTongConfig.getRestApiAddress()) + "?" + sign + "&hrefbackurl=" + this.xinTongConfig.getGameServerBaseUrl() + "&agent=&sign=" + signKey;
        System.out.println(url);
        return ResponseUtils.jsonView(url);
    }
    
    @RequestMapping(value = { "feedback/xintong" }, method = { RequestMethod.GET })
    public void xintongCallback(@RequestParam("orderid") final String tradeNo, @RequestParam("opstate") final int opstate, @RequestParam("ovalue") final double totalFee, @RequestParam("sign") final String postKey0, final HttpServletResponse response) throws IOException {
        final String raw = "orderid=" + tradeNo + "&opstate=" + opstate + "&ovalue=" + String.format("%.2f", totalFee) + this.xinTongConfig.getSecretCode();
        final String postKey = MD5StringUtil.MD5Encode(raw);
        if (!postKey.equals(postKey0)) {
            PayController.LOGGER.error("Post key of trade [{}] not match, expected is: {}, {} in fact", new Object[] { tradeNo, postKey, postKey0 });
            response.getOutputStream().println("opstate=-2");
            return;
        }
        final PubRecharge pubRecharge = this.pubRechargeService.getRechargeRecordByTradeNo(tradeNo);
        if (opstate != 0) {
            PayController.LOGGER.error("Pay trade [{}] failed: ", (Object)tradeNo, (Object)"\u53c2\u6570\u65e0\u6548\u6216\u7b7e\u540d\u9519\u8bef\uff01");
        }
        else {
            if (pubRecharge.getStatus().equals("2")) {
                response.getOutputStream().println("opstate=0");
                return;
            }
            if (pubRecharge.getFee() != totalFee) {
                PayController.LOGGER.error("Total fee of trade [{}] is different with the pay trade, expect: {}, {} in fact", new Object[] { tradeNo, pubRecharge.getFee(), totalFee });
                return;
            }
            pubRecharge.setRealfee(totalFee);
            this.pubRechargeService.setRechargeFinished(pubRecharge);
        }
        response.getOutputStream().println("opstate=0");
    }
    
    public static void main(final String[] args) {
        final Double d = 0.1;
        System.out.println(String.format("%.2f", d));
    }
    
    public static synchronized String getOrderNo() {
        final String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        if (PayController.date == null || !PayController.date.equals(str)) {
            PayController.date = str;
            PayController.orderNum = 0L;
        }
        ++PayController.orderNum;
        long orderNo = Long.parseLong(PayController.date) * 10000L;
        orderNo += PayController.orderNum;
        return new StringBuilder(String.valueOf(orderNo)).toString();
    }
}
