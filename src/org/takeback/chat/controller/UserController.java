// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import org.takeback.chat.entity.GcRoom;
import org.takeback.chat.entity.PubBank;
import org.takeback.chat.entity.PubWithdraw;
import org.takeback.chat.entity.PubRecharge;
import javax.servlet.http.HttpSession;
import org.takeback.chat.entity.PubExchangeLog;
import org.takeback.chat.entity.PcRateConfig;
import org.takeback.chat.entity.PcGameLog;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.chat.utils.ValueControl;
import java.math.BigDecimal;
import org.apache.commons.collections.map.HashedMap;
import org.takeback.chat.entity.TransferLog;
import org.takeback.chat.store.room.Room;
import java.util.ArrayList;
import org.takeback.chat.entity.GcLottery;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.takeback.util.exception.CodedBaseRuntimeException;
import web.wx.utils.WeixinOauth2Userinfo;
import web.wx.utils.JsonUtil;
import web.wx.utils.WeixinOauth2Token;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.HttpClient;
import web.wx.utils.http.HttpClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import java.util.List;
import org.takeback.chat.entity.LoginLog;
import com.google.common.collect.ImmutableMap;
import org.joda.time.LocalDateTime;
import org.takeback.chat.utils.IPUtil;
import java.net.URLEncoder;
import java.util.UUID;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.takeback.util.BeanUtils;
import org.takeback.chat.store.user.User;
import java.util.Objects;
import org.springframework.web.bind.annotation.PathVariable;
import org.takeback.mvc.listener.SessionListener;
import org.takeback.chat.utils.SmsUtil3;
import org.takeback.util.valid.ValidateUtil;
import java.util.Date;
import org.takeback.util.encrypt.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.takeback.chat.entity.PubUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.takeback.util.annotation.AuthPassport;
import org.takeback.util.cache.redis.CacheUtils;

import java.util.Map;
import javax.imageio.ImageReader;
import java.util.Iterator;
import javax.imageio.stream.ImageInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.io.File;
import org.takeback.mvc.ResponseUtils;
import javax.imageio.ImageIO;
import org.springframework.web.util.WebUtils;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.takeback.thirdparty.support.WxConfig;
import org.takeback.chat.service.PcEggService;
import org.takeback.chat.service.RoomService;
import org.takeback.chat.service.SystemService;
import org.takeback.chat.store.room.RoomStore;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserController
{
    @Autowired
    private UserService userService;
    @Autowired
    private UserStore userStore;
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private SystemService systemService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private PcEggService pcService;
    @Autowired
    private WxConfig wxConfig;
    @Autowired
    private CacheUtils cacheUtils;
    private static final String wx_appid = "wxb6dc334873451fe4";
    private static final String wx_secret = "e60b80c1ed998e312c63ce99392ca345";
    
    @AuthPassport
    @RequestMapping(value = { "/user/upload" }, method = { RequestMethod.POST })
    public ModelAndView upload(@RequestParam(value = "file", required = true) final MultipartFile file, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final String path = request.getSession().getServletContext().getRealPath("img/user");
        String fileName = file.getOriginalFilename();
        try {
            final ImageInputStream iis = ImageIO.createImageInputStream(file);
            final Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                return ResponseUtils.jsonView(300, "文件格式错误!");
            }
            iis.close();
        }
        catch (Exception ex) {}
        fileName = "/" + uid + fileName.substring(fileName.lastIndexOf("."));
        final File targetFile = new File(path, fileName);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        try {
            file.transferTo(targetFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final String headImage = "img/user" + fileName;
        this.userService.updateHeadImg(uid, headImage);
        final Map<String, Object> res = new HashMap<String, Object>();
        res.put("headImage", String.valueOf(headImage) + "?t=" + Math.random());
        this.userStore.reload(uid);
        return ResponseUtils.jsonView(200, "上传成功!", res);
    }
    
    @RequestMapping(value = { "/user/update" }, method = { RequestMethod.POST })
    public ModelAndView updateUser(@RequestBody final Map data, final HttpServletRequest request) {
        final Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (userId == null) {
            return ResponseUtils.jsonView(403, "notLogin");
        }
        try {
            if (data.get("id") == null || !userId.equals(data.get("id"))) {
                return ResponseUtils.jsonView(500, "userId not matched");
            }
            final PubUser pubUser = this.userService.updateUser(userId, data);
            this.userStore.reload(userId);
            return ResponseUtils.jsonView(200, "修改成功!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, "修改失败!");
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/updatePsw" }, method = { RequestMethod.POST })
    public ModelAndView updatePsw(@RequestBody final Map data, final HttpServletRequest request) {
        final Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (userId == null) {
            return ResponseUtils.jsonView(403, "notLogin");
        }
        try {
            if (data.get("id") == null || !userId.equals(data.get("id"))) {
                return ResponseUtils.jsonView(500, "userId not matched");
            }
            final Object oldPwd = data.get("oldPwd");
            if (oldPwd == null || oldPwd.toString().length() == 0) {
                return ResponseUtils.jsonView(500, "原密码不能为空!");
            }
            final Object newPwd = data.get("newPwd");
            final Object confirmPwd = data.get("confirmPwd");
            if (newPwd == null || newPwd.toString().length() == 0) {
                return ResponseUtils.jsonView(500, "新密码不能为空!");
            }
            if (!newPwd.equals(confirmPwd)) {
                return ResponseUtils.jsonView(500, "两次输入新密码不一致!");
            }
            final PubUser user = this.userService.get(userId, oldPwd.toString());
            if (user == null) {
                return ResponseUtils.jsonView(500, "原密码不正确!");
            }
            this.userService.updatePwd(userId, CryptoUtils.getHash(newPwd.toString(), StringUtils.reverse(user.getSalt())));
            return ResponseUtils.jsonView(200, "密码修改成功!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, "修改失败!");
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/sendSmsCode" }, method = { RequestMethod.POST })
    public ModelAndView sendSmsCode(@RequestBody final Map data, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (uid == null) {
            return ResponseUtils.jsonView(403, "notLogin");
        }
        if (WebUtils.getSessionAttribute(request, "mobileCodeTime") != null) {
            final Date d1 = (Date)WebUtils.getSessionAttribute(request, "mobileCodeTime");
            final Date d2 = new Date();
            final Long deep = (d2.getTime() - d1.getTime()) / 1000L;
            if (deep <= 120L) {
                return ResponseUtils.jsonView(500, "请" + (120L - deep) + "秒后再尝试!");
            }
        }
        if (data.get("mobile") == null) {
            return ResponseUtils.jsonView(500, "手机号不能为空!");
        }
        final String mobile = (String) data.get("mobile");
        if (!ValidateUtil.instance().validatePhone(mobile)) {
            return ResponseUtils.jsonView(500, "手机号码格式不正确!");
        }
        final Long rand = Math.round(Math.random() * 1000000.0);
        final String msg = "您的验证码为:" + rand.toString();
        try {
            SmsUtil3.send(mobile, msg);
            WebUtils.setSessionAttribute(request, "mobile", mobile);
            WebUtils.setSessionAttribute(request, "mobileCode", rand.toString());
            WebUtils.setSessionAttribute(request, "mobileCodeTime", new Date());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, "验证码发送失败!");
        }
        return ResponseUtils.jsonView(200, "验证码已成功发送!");
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/bindMobile" }, method = { RequestMethod.POST })
    public ModelAndView bindMobile(@RequestBody final Map data, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (uid == null) {
            return ResponseUtils.jsonView(403, "notLogin");
        }
        if (WebUtils.getSessionAttribute(request, "mobile") == null || WebUtils.getSessionAttribute(request, "mobileCode") == null) {
            return ResponseUtils.jsonView(500, "手机号码绑定失败!");
        }
        final String sMobile = (String)WebUtils.getSessionAttribute(request, "mobile");
        final Object smsCode = data.get("smsCode");
        final String sCode = (String)WebUtils.getSessionAttribute(request, "mobileCode");
        if (!sCode.equals(smsCode)) {
            return ResponseUtils.jsonView(500, "验证码不正确!");
        }
        try {
            this.userService.bindMobile(uid, sMobile);
            WebUtils.setSessionAttribute(request, "mobile", null);
            WebUtils.setSessionAttribute(request, "mobileCode", null);
            WebUtils.setSessionAttribute(request, "mobileCodeTime", null);
            return ResponseUtils.jsonView(200, "手机号码绑定成功!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, "手机号码绑定失败!");
        }
    }
    
    @RequestMapping(value = { "/lottery/adminInfo" }, method = { RequestMethod.GET })
    public ModelAndView adminInfo() {
        final Map<String, Object> rs = new HashMap<String, Object>();
        rs.put("withdraw", this.systemService.getWidthdraw());
        rs.put("online", SessionListener.getOnlineNumber());
        rs.put("recharge", 0);
        return ResponseUtils.jsonView(200, "ok", rs);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/{uid}" })
    public ModelAndView getUser(@PathVariable final Integer uid, final HttpServletRequest request) {
        final Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (Objects.equals(userId, uid)) {
            final PubUser user = this.userService.get(PubUser.class, uid);
            final User user2 = BeanUtils.map(user, User.class);
            user2.setUrl(String.valueOf(this.wxConfig.getGameServerBaseUrl()) + "/i?u=" + user2.getId());
            return ResponseUtils.jsonView(user2);
        }
        return ResponseUtils.jsonView(501, "not authorized");
    }
    
    @AuthPassport
    @RequestMapping({ "/user/balance" })
    public ModelAndView getBalance(final HttpServletRequest request) {
        final Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        return ResponseUtils.jsonView(this.userService.getBalance(userId));
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
    
    @RequestMapping(value = { "/wx_login" }, method = { RequestMethod.GET })
    public void wx_login(final HttpServletRequest request, final HttpServletResponse response) {
        System.out.println("***************1");
        final String state = UUID.randomUUID().toString();
        request.getSession().setAttribute("wx_state", state);
        final StringBuffer url = request.getRequestURL();
        final String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
        final String callback = String.valueOf(tempContextUrl) + "wx_login_callback";
        this.getOut(response).println("<script>location.href='https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxb6dc334873451fe4&redirect_uri=" + URLEncoder.encode(callback) + "&response_type=code&scope=snsapi_userinfo&state=" + state + "#wechat_redirect';</script>");
    }
    
    @RequestMapping(value = { "/wx_loginCheck" }, method = { RequestMethod.GET })
    public ModelAndView wx_loginCheck(final HttpServletRequest request, final HttpServletResponse response) {
        final String user_wx_username = (String)request.getSession().getAttribute("user_wx_username");
        request.getSession().removeAttribute("user_wx_username");
        final String ip = IPUtil.getIp(request);
        final List<PubUser> ulist = this.userService.findByProperty(PubUser.class, "userId", user_wx_username);
        if (ulist == null || ulist.isEmpty()) {
            return ResponseUtils.jsonView(404, "用户不存在或者密码错误");
        }
        final PubUser user = ulist.get(0);
        if ("9".equals(user.getUserType())) {
            return ResponseUtils.jsonView(404, "不能登陆非玩家账号!");
        }
        if ("2".equals(user.getStatus()) || "3".equals(user.getStatus())) {
            return ResponseUtils.jsonView(404, "账号被锁定或注销,请联系客服咨询处理!");
        }
        WebUtils.setSessionAttribute(request, "$uid", user.getId());
        user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
        final LocalDateTime expire = new LocalDateTime().plusDays(7);
        user.setTokenExpireTime(expire.toDate());
        this.userService.updateUser(user.getId(),  ImmutableMap.of("accessToken", user.getAccessToken(), "tokenExpireTime", user.getTokenExpireTime()));
        this.userService.setLoginInfo(ip, user.getId());
        final LoginLog l = new LoginLog();
        l.setIp(ip);
        l.setLoginTime(new Date());
        l.setUserId(user.getId());
        l.setUserName(user.getUserId());
        this.userService.save(LoginLog.class, l);
        final User user2 = BeanUtils.map(user, User.class);
        final StringBuffer url = request.getRequestURL();
        final String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
        user2.setUrl(String.valueOf(tempContextUrl) + "i?u=" + user2.getId());
        this.userStore.reload(user2.getId());
        return ResponseUtils.jsonView(user2);
    }
    
    @RequestMapping(value = { "/wx_login_callback" }, method = { RequestMethod.GET })
    public void wx_login_callback(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            System.out.println("***************1");
            final String code = request.getParameter("code");
            final String state = request.getParameter("state");
            final String temp_state = (String)request.getSession().getAttribute("wx_state");
            if (StringUtils.isEmpty((CharSequence)temp_state) || StringUtils.isEmpty((CharSequence)state) || !state.equals(temp_state)) {
                System.out.println("***************2");
                return;
            }
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient = (DefaultHttpClient)HttpClientConnectionManager.getSSLInstance((HttpClient)httpclient);
            HttpPost httpost = HttpClientConnectionManager.getPostMethod("https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxb6dc334873451fe4&secret=e60b80c1ed998e312c63ce99392ca345&code=" + code + "&grant_type=authorization_code");
            HttpResponse response_http = (HttpResponse)httpclient.execute((HttpUriRequest)httpost);
            String jsonStr = EntityUtils.toString(response_http.getEntity(), "UTF-8");
            if (jsonStr.indexOf("FAIL") != -1) {
                return;
            }
            final WeixinOauth2Token tk = (WeixinOauth2Token)JsonUtil.getDTO(jsonStr, WeixinOauth2Token.class);
            System.out.println("tk.getOpenid():" + tk.getOpenid());
            List<PubUser> pulist = this.userService.findByProperty(PubUser.class, "wxOpenId", tk.getOpenid());
            if (pulist == null || pulist.isEmpty()) {
                System.out.println("数据库中没有数据");
                httpost = HttpClientConnectionManager.getPostMethod("https://api.weixin.qq.com/sns/userinfo?access_token=" + tk.getAccess_token() + "&openid=" + tk.getOpenid() + "&lang=zh_CN");
                response_http = (HttpResponse)httpclient.execute((HttpUriRequest)httpost);
                jsonStr = EntityUtils.toString(response_http.getEntity(), "UTF-8");
                System.out.println("jsonStr----:" + jsonStr);
                if (jsonStr.indexOf("FAIL") != -1) {
                    return;
                }
                final WeixinOauth2Userinfo userinfo = (WeixinOauth2Userinfo)JsonUtil.getDTO(jsonStr, WeixinOauth2Userinfo.class);
                System.out.println(userinfo.getHeadimgurl());
                System.out.println(userinfo.getNickname());
                String username;
                for (username = "wx_" + (int)((Math.random() * 9.0 + 1.0) * 100000.0), pulist = this.userService.findByProperty(PubUser.class, "userId", username); pulist != null && pulist.size() > 0; pulist = this.userService.findByProperty(PubUser.class, "userId", username)) {
                    username = "wx_" + (int)((Math.random() * 9.0 + 1.0) * 100000.0);
                }
                final String password = "123456";
                final String mobile = "";
                final String wx = "";
                final String alipay = "";
                try {
                    Integer parentId = null;
                    parentId = (Integer)request.getSession().getAttribute("$invitor");
                    System.out.println(new StringBuffer().append("found proxy ==> ").append(parentId).toString());
                    final String ip = IPUtil.getIp(request);
                    final PubUser user = this.userService.register(username, password, mobile, wx, alipay, parentId, ip);
                    if (user == null) {
                        return;
                    }
                    user.setWxOpenId(tk.getOpenid());
                    user.setHeadImg(userinfo.getHeadimgurl());
                    this.userService.update(PubUser.class, user);
                    WebUtils.setSessionAttribute(request, "$uid", user.getId());
                    user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
                    final LocalDateTime expire = new LocalDateTime().plusDays(7);
                    user.setTokenExpireTime(expire.toDate());
                    this.userService.updateUser(user.getId(),  ImmutableMap.of( "accessToken",  user.getAccessToken(),  "tokenExpireTime", user.getTokenExpireTime()));
                    final User user2 = BeanUtils.map(user, User.class);
                    final StringBuffer url = request.getRequestURL();
                    final String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
                    user2.setUrl(String.valueOf(tempContextUrl) + "i?u=" + user2.getId());
                    this.userStore.reload(user2.getId());
                    request.getSession().setAttribute("user_wx_username", user.getUserId());
                    this.getOut(response).println("<script>location.href='/wxLoginCheck.jsp';</script>");
                }
                catch (CodedBaseRuntimeException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("数据库中有数据1111");
                final PubUser user3 = pulist.get(0);
                request.getSession().setAttribute("user_wx_username", user3.getUserId());
                this.getOut(response).println("<script>location.href='/wxLoginCheck.jsp';</script>");
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    @RequestMapping(value = { "/login" }, method = { RequestMethod.POST })
    public ModelAndView login(@RequestBody final Map data, final HttpServletRequest request) {
        final String ip = IPUtil.getIp(request);
        final String username = (String) data.get("username");
        final String password = (String) data.get("password");
        final PubUser user = this.userService.login(username, password);
        if (user == null) {
            return ResponseUtils.jsonView(404, "用户不存在或者密码错误");
        }
        if ("9".equals(user.getUserType())) {
            return ResponseUtils.jsonView(404, "不能登陆非玩家账号!");
        }
        if ("2".equals(user.getStatus()) || "3".equals(user.getStatus())) {
            return ResponseUtils.jsonView(404, "账号被锁定或注销,请联系客服咨询处理!");
        }
        WebUtils.setSessionAttribute(request, "$uid", user.getId());
        user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
        final LocalDateTime expire = new LocalDateTime().plusDays(7);
        user.setTokenExpireTime(expire.toDate());
        this.userService.updateUser(user.getId(),  ImmutableMap.of( "accessToken",  user.getAccessToken(),  "tokenExpireTime",  user.getTokenExpireTime()) );
        this.userService.setLoginInfo(ip, user.getId());
        final LoginLog l = new LoginLog();
        l.setIp(ip);
        l.setLoginTime(new Date());
        l.setUserId(user.getId());
        l.setUserName(user.getUserId());
        this.userService.save(LoginLog.class, l);
        final User user2 = BeanUtils.map(user, User.class);
        final StringBuffer url = request.getRequestURL();
        final String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
        user2.setUrl(String.valueOf(tempContextUrl) + "i?u=" + user2.getId());
        this.userStore.reload(user2.getId());
        return ResponseUtils.jsonView(user2);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/createUser" }, method = { RequestMethod.POST })
    public ModelAndView createUser(@RequestBody final Map data, final HttpServletRequest request) {
        final Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final String username = (String) data.get("username");
        final String password = (String) data.get("password");
        final String mobile = data.containsKey("mobile") ? (String) data.get("mobile") : "";
        final String wx = data.containsKey("wx") ? (String) data.get("wx") : "";
        final String alipay = data.containsKey("alipay") ? (String) data.get("alipay") : "";
        try {
            final Integer parentId = userId;
            final String ip = IPUtil.getIp(request);
            final PubUser user = this.userService.register(username, password, mobile, wx, alipay, parentId, ip);
            if (user == null) {
                return ResponseUtils.jsonView(500, "注册失败");
            }
            user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
            final LocalDateTime expire = new LocalDateTime().plusDays(7);
            user.setTokenExpireTime(expire.toDate());
            this.userService.updateUser(user.getId(), ImmutableMap.of( "accessToken",  user.getAccessToken(), "tokenExpireTime",  user.getTokenExpireTime()) );
            cacheUtils.setUser(user.getId(), user);
            return ResponseUtils.jsonView(200, "创建成功!");
        }
        catch (CodedBaseRuntimeException e) {
            return ResponseUtils.jsonView(e.getCode(), e.getMessage());
        }
    }
    
    @RequestMapping(value = { "/register" }, method = { RequestMethod.POST })
    public ModelAndView register(@RequestBody final Map data, final HttpServletRequest request) {
        final String username = (String) data.get("username");
        final String password = (String) data.get("password");
        final String mobile = data.containsKey("mobile") ? (String) data.get("mobile") : "";
        final String wx = data.containsKey("wx") ? (String) data.get("wx") : "";
        final String alipay = data.containsKey("alipay") ? (String) data.get("alipay") : "";
        try {
            Integer parentId = null;
            final Object invitor = WebUtils.getSessionAttribute(request, "$invitor");
            parentId = ((invitor == null) ? null : ((Integer)invitor));
            if (parentId == null && data.containsKey("parentId")) {
                parentId = (Integer)data.get("parentId");
            }
            final String ip = IPUtil.getIp(request);
            final PubUser user = this.userService.register(username, password, mobile, wx, alipay, parentId, ip);
            if (user == null) {
                return ResponseUtils.jsonView(500, "注册失败");
            }
            WebUtils.setSessionAttribute(request, "$uid", user.getId());
            user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
            final LocalDateTime expire = new LocalDateTime().plusDays(7);
            user.setTokenExpireTime(expire.toDate());
            this.userService.updateUser(user.getId(),  ImmutableMap.of( "accessToken", user.getAccessToken(),  "tokenExpireTime", user.getTokenExpireTime()) );
            final User user2 = BeanUtils.map(user, User.class);
            final StringBuffer url = request.getRequestURL();
            final String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
            user2.setUrl(String.valueOf(tempContextUrl) + "i?u=" + user2.getId());
            this.userStore.reload(user2.getId());
            cacheUtils.setUser(user.getId(), user);
            return ResponseUtils.jsonView(user2);
        }
        catch (CodedBaseRuntimeException e) {
            return ResponseUtils.jsonView(e.getCode(), e.getMessage());
        }
    }
    
    @RequestMapping({ "/user/myLottery" })
    public ModelAndView myLottery(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (uid == null) {
            return ResponseUtils.jsonView(403, "notLogin");
        }
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final List<GcLottery> list = this.userService.find(GcLottery.class, ImmutableMap.of( "sender",  uid) , pageSize, pageNo, "createTime desc");
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(list.size());
        for (final GcLottery gcLottery : list) {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("createTime", gcLottery.getCreateTime());
            map.put("money", gcLottery.getMoney());
            final Room room = this.roomStore.get(gcLottery.getRoomId());
            map.put("roomName", (room == null) ? "不明" : room.getName());
            records.add(map);
        }
        return ResponseUtils.jsonView(records);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/transfer" })
    public ModelAndView transfer(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            final Integer targetId = (Integer) params.get("userId");
            final Integer money = (Integer) params.get("money");
            this.userService.transfer(uid, targetId, money);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
        return ResponseUtils.jsonView(200, "转账成功");
    }
    
    @AuthPassport
    @RequestMapping({ "/user/prixyRecharge" })
    public ModelAndView prixyRecharge(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            final Integer targetId = (Integer) params.get("userId");
            final Integer money = (Integer) params.get("money");
            this.userService.prixyRecharge(uid, targetId, money);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
        return ResponseUtils.jsonView(200, "上分成功");
    }
    
    @AuthPassport
    @RequestMapping({ "/user/prixyUnRecharge" })
    public ModelAndView prixyUnRecharge(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            final Integer targetId = (Integer) params.get("userId");
            final Integer money = (Integer) params.get("money");
            this.userService.prixyUnRecharge(uid, targetId, money);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
        return ResponseUtils.jsonView(200, "下分成功");
    }
    
    @AuthPassport
    @RequestMapping({ "/user/prixyRechargeLog" })
    public ModelAndView prixyRechargeLog(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final String hql = "from PubRecharge where operator=:uid and rechargeType=:rechargeType order by id desc ";
        final Map map = new HashMap();
        map.put("uid", uid);
        map.put("rechargeType", "2");
        final List<TransferLog> list = this.userService.findByHql(hql, map, pageSize, pageNo);
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/prixyUnRechargeLog" })
    public ModelAndView prixyUnRechargeLog(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final String hql = "from PubRecharge where operator=:uid and rechargeType=:rechargeType order by id desc ";
        final Map map = new HashMap();
        map.put("uid", uid);
        map.put("rechargeType", "3");
        final List<TransferLog> list = this.userService.findByHql(hql, map, pageSize, pageNo);
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/getNickName" }, method = { RequestMethod.POST })
    public ModelAndView getNickName(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
        try {
            final Integer uid = (Integer) params.get("uid");
            final PubUser u = this.userService.get(PubUser.class, uid);
            if (u == null) {
                return ResponseUtils.jsonView(500, "目标账号不存在!");
            }
            final Map m = (Map)new HashedMap();
            m.put("nickName", u.getNickName());
            m.put("money", u.getMoney());
            m.put("code", 200);
            return ResponseUtils.jsonView(m);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/checkRecharge" }, method = { RequestMethod.POST })
    public ModelAndView checkRecharge(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
        try {
            final Integer uid = (Integer) params.get("uid");
            final PubUser u = this.userService.get(PubUser.class, uid);
            if (u == null) {
                return ResponseUtils.jsonView(500, "目标账号不存在!");
            }
            final Map m = (Map)new HashedMap();
            m.put("nickName", u.getNickName());
            m.put("money", u.getMoney());
            m.put("code", 200);
            return ResponseUtils.jsonView(m);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
    }
    
    @AuthPassport
    @RequestMapping({ "/user/exchange" })
    public ModelAndView exchange(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            final Integer shopId = Integer.valueOf((String)params.get("shopId"));
            final String name = (String) params.get("name");
            final String address = (String) params.get("address");
            final String mobile = (String) params.get("mobile");
            this.userService.exchange(uid, shopId, name, address, mobile);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
        return ResponseUtils.jsonView(200, "兑换成功,请等待管理员处理发货!");
    }
    
    @AuthPassport
    @RequestMapping({ "/user/transferLogs" })
    public ModelAndView transferLogs(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final String hql = "from TransferLog where fromUid =:uid or toUid=:uid order by id desc ";
        final List<TransferLog> list = this.userService.findByHql(hql,   ImmutableMap.of( "uid",  uid) , pageSize, pageNo);
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/proxyUsers" })
    public ModelAndView proxyUsers(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final String hql = "select id,userId ,nickName , registDate,money from PubUser where parent=:uid order by id desc ";
        final List<PubUser> list = this.userService.findByHql(hql,    ImmutableMap.of( "uid",  uid) , pageSize, pageNo);
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/proxyApply" })
    public ModelAndView proxyApply(final HttpServletRequest request) {
        try {
            final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
            final Map<String, Object> conf = this.systemService.getProxyConfig();
            this.userService.proxyApply(uid, conf);
            return ResponseUtils.jsonView(200, "申请成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, e.getMessage());
        }
    }
    
    @AuthPassport
    @RequestMapping({ "/user/proxyConfig" })
    public ModelAndView proxyConfig(final HttpServletRequest request) {
        try {
            final Map res = (Map)new HashedMap();
            res.put("code", 200);
            res.put("body", this.systemService.getProxyConfig());
            return ResponseUtils.jsonView(res);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.jsonView(500, "配置获取失败!");
        }
    }
    
    @AuthPassport
    @RequestMapping({ "/user/proxyLogs" })
    public ModelAndView proxyLogs(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final String hql = "from ProxyVote where parentId=:uid order by id desc ";
        final List<TransferLog> list = this.userService.findByHql(hql,  ImmutableMap.of( "uid",  uid) , pageSize, pageNo);
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/getUid" }, method = { RequestMethod.GET })
    public ModelAndView getUid(final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        return ResponseUtils.jsonView(uid);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/vc" }, method = { RequestMethod.GET })
    public ModelAndView vc(@RequestParam final int uid, @RequestParam final String roomId, @RequestParam double value, final HttpServletRequest request) {
        ValueControl.setValue(roomId, uid, new BigDecimal(value));
        return ResponseUtils.jsonView(ValueControl.getStore());
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/clean" }, method = { RequestMethod.GET })
    public ModelAndView clean(@RequestParam final int uid, @RequestParam final String roomId, final HttpServletRequest request) {
        ValueControl.clean(roomId, uid);
        return ResponseUtils.jsonView(ValueControl.getStore());
    }
    
    @AuthPassport
    @RequestMapping({ "/user/myBonus" })
    public ModelAndView myBonus(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final List<GcLotteryDetail> list = this.userService.findByHql(" from GcLotteryDetail where uid=:uid and roomId is not null and roomId<>'' order by id desc ",  ImmutableMap.of( "uid",  uid) , pageSize, pageNo);
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(list.size());
        for (final GcLotteryDetail gcLottery : list) {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("createTime", gcLottery.getCreateDate());
            map.put("money", gcLottery.getCoin());
            map.put("desc1", gcLottery.getDesc1());
            map.put("inoutNum", gcLottery.getInoutNum());
            final Room room = this.roomStore.get(gcLottery.getRoomId());
            if (room != null) {
                map.put("roomName", (room == null) ? "不明" : room.getName());
                records.add(map);
            }
        }
        return ResponseUtils.jsonView(records);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/myBonus03" })
    public ModelAndView myBonus03(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final List<PcGameLog> list = this.userService.find(PcGameLog.class,   ImmutableMap.of("uid", uid) , pageSize, pageNo, "betTime desc");
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(list.size());
        final Map<String, PcRateConfig> rates = this.pcService.getPcRateConfig();
        for (final PcGameLog gcLottery : list) {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("createTime", gcLottery.getBetTime());
            map.put("money", gcLottery.getFreeze());
            map.put("luckyNumber", gcLottery.getLuckyNumber());
            if ("num".equals(gcLottery.getBetType())) {
                map.put("desc1", "数字" + gcLottery.getBet());
            }
            else {
                map.put("desc1", rates.get(gcLottery.getBet()).getAlias());
            }
            map.put("inoutNum", gcLottery.getUserInout());
            map.put("num", gcLottery.getNum());
            records.add(map);
        }
        return ResponseUtils.jsonView(records);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/exchangeLogs" })
    public ModelAndView exchangeLogs(@RequestParam int pageNo, @RequestParam int pageSize, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final List<PubExchangeLog> list = this.userService.find(PubExchangeLog.class, ImmutableMap.of( "uid",  uid) , pageSize, pageNo, "id desc");
        if (list == null || list.isEmpty()) {
            return ResponseUtils.jsonView(null);
        }
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/roomHistory" })
    public ModelAndView roomHistory(final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        return ResponseUtils.jsonView(this.userService.findByProperty(GcLottery.class, "sender", uid, "createTime desc"));
    }
    
    @RequestMapping(value = { "/user/logout" }, method = { RequestMethod.POST })
    public ModelAndView logout(final HttpSession session) {
        if (session != null) {
            final Integer uid = (Integer)session.getAttribute("$uid");
            if (uid != null) {
                final Map<String, Object> map = new HashMap<String, Object>();
                map.put("accessToken", null);
                map.put("tokenExpireTime", null);
                this.userService.updateUser(uid, map);
            }
            session.invalidate();
        }
        return ResponseUtils.jsonView(200, "成功退出.");
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/rechargeRecords" }, method = { RequestMethod.GET })
    public ModelAndView getRechargeRecords(@RequestParam int pageSize, @RequestParam int pageNo, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final List<PubRecharge> list = this.userService.findByHql("from PubRecharge where uid=:uid and status =2 order by finishtime desc",  ImmutableMap.of( "uid",  uid) , pageSize, pageNo);
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/withdrawRecords" })
    public ModelAndView getWithdrawRecords(@RequestParam int pageSize, @RequestParam int pageNo, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize > 20) {
            pageSize = 20;
        }
        final List<PubWithdraw> list = this.userService.findByHql("from PubWithdraw where uid=:uid  order by tradetime desc",  ImmutableMap.of("uid",uid), pageSize, pageNo);
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping({ "/user/bankRecords" })
    public ModelAndView getBankRecords(final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final List<PubBank> list = this.userService.findByProperty(PubBank.class, "userId", uid, "createTime desc");
        return ResponseUtils.jsonView(list);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/withdraw" }, method = { RequestMethod.POST })
    public ModelAndView withdraw(@RequestBody final Map<String, Object> data, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            this.userService.withdraw(data, uid);
        }
        catch (Exception e) {
            return ResponseUtils.jsonView(500, e.getMessage());
        }
        return ResponseUtils.jsonView(200, "提现成功.");
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/roomApply" }, method = { RequestMethod.POST })
    public ModelAndView roomApply(final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        try {
            this.userService.createRoom(uid);
        }
        catch (Exception e) {
            return ResponseUtils.jsonView(500, e.getMessage());
        }
        return ResponseUtils.jsonView(200, "success.");
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/roomCount" }, method = { RequestMethod.GET })
    public ModelAndView getUserRoomCount(final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final int count = this.roomService.getUserRoomCount(uid);
        return ResponseUtils.jsonView(count);
    }
    
    @AuthPassport
    @RequestMapping(value = { "/user/rooms" }, method = { RequestMethod.GET })
    public ModelAndView getUserRooms(@RequestParam final int pageSize, @RequestParam final int pageNo, final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        final List<GcRoom> list = this.roomService.getUserRooms(uid, pageSize, pageNo);
        if (list != null && !list.isEmpty()) {
            final List<Room> rooms = new ArrayList<Room>(list.size());
            for (final GcRoom gcRoom : list) {
                rooms.add(this.roomStore.get(gcRoom.getId()));
            }
            return ResponseUtils.jsonView(rooms);
        }
        return ResponseUtils.jsonView(list);
    }
    
    @RequestMapping(value = { "/i" }, method = { RequestMethod.GET })
    public void getUserRooms(@RequestParam final Integer u, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            WebUtils.setSessionAttribute(request, "$invitor", u);
            response.sendRedirect("/");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
