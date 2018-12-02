// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.takeback.util.encrypt.CryptoUtils;
import org.takeback.chat.service.admin.SystemConfigService;
import java.util.Iterator;
import org.apache.commons.collections.MapUtils;
import org.takeback.chat.entity.LoginLog;
import java.util.Objects;
import java.util.HashMap;
import org.springframework.web.util.WebUtils;
import org.takeback.chat.utils.IPUtil;
import net.sf.json.JSONObject;
import org.joda.time.LocalDateTime;
import java.util.UUID;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import org.takeback.thirdparty.support.HttpHelper;
import org.springframework.web.bind.annotation.RequestParam;
import org.takeback.util.BeanUtils;
import org.takeback.chat.store.user.User;
import java.util.Date;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.takeback.mvc.ResponseUtils;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.takeback.chat.store.user.UserStore;
import org.takeback.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.thirdparty.support.WxConfig;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping({ "/thirdparty/login" })
public class ThirdPartyLoginController
{
    private static final Logger LOGGER;
    @Autowired
    private WxConfig wxConfig;
    @Autowired
    private UserService userService;
    @Autowired
    private UserStore userStore;
    private static final String BEFORE_LOGIN_STATE = "$beforeLoginState";
    
    @RequestMapping(value = { "/apply" }, method = { RequestMethod.POST })
    public ModelAndView apply(@RequestBody final Map<String, Object> params, final HttpSession session) {
        final String s;
        final String type = s = params.get("type");
        switch (s) {
            case "wx": {
                final String url = this.getWeixinAuthorUrl(true);
                final Map<String, Object> extras = params.get("extras");
                if (extras != null && extras.get("fromUrl") != null) {
                    session.setAttribute("$beforeLoginState", (Object)extras);
                }
                return ResponseUtils.jsonView(url);
            }
            default: {
                return ResponseUtils.jsonView(400, "\u767b\u5f55\u65b9\u5f0f\u4e0d\u652f\u6301.");
            }
        }
    }
    
    @RequestMapping(value = { "/auto" }, method = { RequestMethod.POST })
    public ModelAndView autoLogin(@RequestBody final Map<String, Object> params, final HttpServletRequest request, final HttpSession session) {
        final Integer uid = params.get("uid");
        if (uid == null) {
            return ResponseUtils.jsonView(500, "\u7528\u6237id\u4e3a\u7a7a\u3002");
        }
        final PubUser user = this.userService.get(PubUser.class, uid);
        if (user == null) {
            return ResponseUtils.jsonView(404, "\u7528\u6237\u4e0d\u5b58\u5728\u3002");
        }
        if ("2".equals(user.getStatus()) || "3".equals(user.getStatus())) {
            return ResponseUtils.jsonView(404, "\u8d26\u53f7\u88ab\u9501\u5b9a\u6216\u6ce8\u9500,\u8bf7\u8054\u7cfb\u5ba2\u670d\u54a8\u8be2\u5904\u7406!");
        }
        final Boolean inWeixin = params.get("inWeixin");
        if (user.getWbOpenId() != null) {
            if (!inWeixin) {
                return ResponseUtils.jsonView(500, "\u53ea\u80fd\u5728\u5fae\u4fe1\u6d4f\u89c8\u5668\u4e2d\u767b\u5f55\u3002");
            }
            final ModelAndView mav = this.doWxLogin(user.getWxRefreshToken(), user.getWxOpenId(), false, request, session);
            if (mav != null) {
                return mav;
            }
        }
        else {
            final String token = params.get("accessToken");
            if (token == null || !token.equals(user.getAccessToken())) {
                return ResponseUtils.jsonView(402, "\u7528\u6237\u6388\u6743\u5931\u8d25, \u8bf7\u91cd\u65b0\u767b\u5f55\u3002");
            }
            if (user.getTokenExpireTime().compareTo(new Date()) < 0) {
                return ResponseUtils.jsonView(401, "\u767b\u5f55\u5df2\u8fc7\u671f\u8bf7\u91cd\u65b0\u767b\u5f55\u3002");
            }
        }
        session.setAttribute("$uid", (Object)user.getId());
        final User user2 = BeanUtils.map(user, User.class);
        user2.setUrl(this.wxConfig.getGameServerBaseUrl() + "i?u=" + user2.getId());
        this.userStore.reload(user2.getId());
        return ResponseUtils.jsonView(user2);
    }
    
    @RequestMapping(value = { "/wx/code" }, method = { RequestMethod.GET })
    public ModelAndView onCodeReturn(@RequestParam("code") final String code, @RequestParam(name = "isApp", value = "isApp", required = false) final boolean isApp, final HttpServletRequest request, final HttpSession session) {
        final String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + this.wxConfig.getWxJSAPIAppId() + "&secret=" + this.wxConfig.getWxJSAPISecret() + "&code=" + code + "&grant_type=authorization_code";
        JSONObject resultObject;
        try {
            resultObject = HttpHelper.getJson(url);
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to get open id.", e);
        }
        if (resultObject.containsKey((Object)"errcode")) {
            ThirdPartyLoginController.LOGGER.error("Failed to get open id, error code: {}, caused by: {}", (Object)resultObject.getInt("errcode"), resultObject.get("errmsg"));
            if (isApp) {
                return ResponseUtils.jsonView(500, "\u767b\u5f55\u5931\u8d25");
            }
            return ResponseUtils.modelView("jump", (Map<String, Object>)ImmutableMap.of((Object)"url", (Object)"/#/tab/login", (Object)"message", (Object)"\u767b\u5f55\u5931\u8d25\u3002"));
        }
        else {
            final ModelAndView mav = this.doWxLogin(resultObject.getString("refresh_token"), resultObject.getString("openid"), isApp, request, session);
            if (mav != null) {
                return mav;
            }
            final String redirectUrl = this.getRedirectUrl(session);
            final Integer uid = (Integer)session.getAttribute("$uid");
            final User user = this.userStore.get(uid);
            user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
            LocalDateTime expire = new LocalDateTime();
            expire = expire.plusDays(7);
            user.setTokenExpireTime(expire.toDate());
            this.userService.updateUser(uid, (Map<String, Object>)ImmutableMap.of((Object)"accessToken", (Object)user.getAccessToken(), (Object)"tokenExpireTime", (Object)user.getTokenExpireTime()));
            if (isApp) {
                return ResponseUtils.jsonView((Map<String, Object>)ImmutableMap.of((Object)"uid", (Object)uid, (Object)"username", (Object)user.getUserId(), (Object)"accessToken", (Object)user.getAccessToken()));
            }
            return ResponseUtils.modelView("jump", (Map<String, Object>)ImmutableMap.of((Object)"url", (Object)redirectUrl, (Object)"uid", (Object)uid, (Object)"username", (Object)user.getUserId(), (Object)"accessToken", (Object)user.getAccessToken()));
        }
    }
    
    private ModelAndView doWxLogin(final String refreshToken, final String openId, final boolean isApp, final HttpServletRequest request, final HttpSession session) {
        final String url2 = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + this.wxConfig.getWxJSAPIAppId() + "&grant_type=refresh_token&refresh_token=" + refreshToken;
        JSONObject refreshResult;
        try {
            refreshResult = HttpHelper.getJson(url2);
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to refresh access token.", e);
        }
        if (!refreshResult.has("errcode")) {
            final String token = refreshResult.getString("access_token");
            final String url3 = "https://api.weixin.qq.com/sns/userinfo?access_token=" + token + "&openid=" + openId + "&lang=zh_CN";
            JSONObject result;
            try {
                result = HttpHelper.getJson(url3);
            }
            catch (IOException e2) {
                throw new IllegalStateException("Failed to get access_token.", e2);
            }
            PubUser user = this.userService.getByWxOpenId(openId);
            final String headImgUrl = result.getString("headimgurl");
            final String nickName = result.getString("nickname");
            final String ip = IPUtil.getIp(request);
            if (user == null) {
                final Object invitor = WebUtils.getSessionAttribute(request, "$invitor");
                final Integer parentId = (invitor == null) ? null : ((Integer)invitor);
                user = this.createUser(headImgUrl, nickName);
                if (parentId != null) {
                    user.setParent(parentId);
                }
                user.setWxOpenId(openId);
                user.setRegistIp(ip);
                user.setRegistDate(new Date());
                this.userService.save(PubUser.class, user);
            }
            else {
                final Map<String, Object> data = new HashMap<String, Object>();
                if (!Objects.equals(user.getHeadImg(), headImgUrl)) {
                    user.setHeadImg(headImgUrl);
                    data.put("headImg", headImgUrl);
                }
                if (!Objects.equals(user.getNickName(), nickName)) {
                    user.setNickName(nickName);
                    data.put("nickName", nickName);
                }
                if (!data.isEmpty()) {
                    this.userService.updateUser(user.getId(), data);
                }
            }
            this.userService.setLoginInfo(ip, user.getId());
            final LoginLog l = new LoginLog();
            l.setLoginTime(new Date());
            l.setIp(ip);
            l.setUserId(user.getId());
            l.setUserName(user.getUserId());
            this.userService.save(LoginLog.class, l);
            session.setAttribute("$uid", (Object)user.getId());
            return null;
        }
        ThirdPartyLoginController.LOGGER.error("Cannot refresh token, error: {}, message: {}", (Object)refreshResult.getInt("errcode"), (Object)refreshResult.getString("errmsg"));
        if (isApp) {
            return ResponseUtils.jsonView(500, "\u767b\u5f55\u5931\u8d25\u3002");
        }
        return ResponseUtils.modelView("jump", (Map<String, Object>)ImmutableMap.of((Object)"url", (Object)"/#/tab/login", (Object)"message", (Object)"\u767b\u5f55\u5931\u8d25\u3002"));
    }
    
    private String getRedirectUrl(final HttpSession session) {
        String redirectUrl = "/#/tab/rooms";
        final Map<String, Object> extras = (Map<String, Object>)session.getAttribute("$beforeLoginState");
        session.removeAttribute("$beforeLoginState");
        if (extras != null) {
            redirectUrl = extras.get("fromUrl");
            final Map<String, String> params = extras.get("fromParams");
            int idx = redirectUrl.indexOf(":");
            if (idx > 0) {
                final String pvs = redirectUrl.substring(idx + 1);
                redirectUrl = redirectUrl.substring(0, idx);
                final String[] tmp = pvs.split("/:");
                if (tmp.length > 0 && MapUtils.isNotEmpty((Map)params)) {
                    for (final String pv : tmp) {
                        redirectUrl = redirectUrl + params.get(pv) + "/";
                    }
                    redirectUrl = redirectUrl.substring(0, redirectUrl.length() - 1);
                }
            }
            idx = redirectUrl.indexOf("?");
            if (idx > 0) {
                redirectUrl = redirectUrl.substring(0, idx + 1);
                if (params != null) {
                    for (final Map.Entry<String, String> en : params.entrySet()) {
                        redirectUrl = redirectUrl + en.getKey() + "=" + en.getValue() + "&";
                    }
                    if (redirectUrl.endsWith("&")) {
                        redirectUrl = redirectUrl.substring(0, redirectUrl.length() - 1);
                    }
                }
            }
        }
        return "/#/tab" + redirectUrl;
    }
    
    private PubUser createUser(final String avatar, final String nickName) {
        final PubUser user = new PubUser();
        user.setHeadImg(avatar);
        user.setMoney(0.0);
        final Object conf = SystemConfigService.getInstance().getValue("conf_init_money");
        if (conf != null) {
            try {
                user.setMoney(Double.valueOf(conf.toString()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        user.setNickName(nickName);
        user.setRegistDate(new Date());
        user.setExp(0.0);
        user.setUserType("1");
        user.setSalt(CryptoUtils.getSalt());
        user.setUserId(UUID.randomUUID().toString().replace("-", ""));
        user.setPwd("");
        user.setMoneyCode(user.getPwd());
        user.setUserType("1");
        return user;
    }
    
    private String getWeixinAuthorUrl(final boolean needUserInfo) {
        String redirectUrl = this.wxConfig.getGameServerBaseUrl() + "thirdparty/login/wx/code";
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + this.wxConfig.getWxJSAPIAppId() + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=" + (needUserInfo ? "snsapi_userinfo" : "snsapi_base") + "&state=STATE#wechat_redirect";
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ThirdPartyLoginController.class);
    }
}
