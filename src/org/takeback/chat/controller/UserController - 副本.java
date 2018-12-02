/*      */ package org.takeback.chat.controller;
/*      */ 
/*      */ import com.google.common.collect.ImmutableMap;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.UUID;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.http.HttpSession;
/*      */ import org.apache.commons.collections.map.HashedMap;
/*      */ import org.apache.commons.lang3.StringUtils;
/*      */ import org.joda.time.LocalDateTime;
/*      */ import org.springframework.beans.factory.annotation.Autowired;
/*      */ import org.springframework.web.bind.annotation.RequestBody;
/*      */ import org.springframework.web.bind.annotation.RequestMapping;
/*      */ import org.springframework.web.bind.annotation.RequestParam;
/*      */ import org.springframework.web.multipart.MultipartFile;
/*      */ import org.springframework.web.servlet.ModelAndView;
/*      */ import org.springframework.web.util.WebUtils;
/*      */ import org.takeback.chat.entity.GcLottery;
/*      */ import org.takeback.chat.entity.GcLotteryDetail;
/*      */ import org.takeback.chat.entity.GcRoom;
/*      */ import org.takeback.chat.entity.GcWaterLog;
/*      */ import org.takeback.chat.entity.LoginLog;
/*      */ import org.takeback.chat.entity.PcGameLog;
/*      */ import org.takeback.chat.entity.ProxyVote;
/*      */ import org.takeback.chat.entity.PubUser;
/*      */ import org.takeback.chat.entity.TransferLog;
/*      */ import org.takeback.chat.service.SystemService;
/*      */ import org.takeback.chat.service.UserService;
/*      */ import org.takeback.chat.store.room.Room;
/*      */ import org.takeback.chat.store.room.RoomStore;
/*      */ import org.takeback.chat.store.user.User;
/*      */ import org.takeback.chat.store.user.UserStore;
/*      */ import org.takeback.chat.utils.IPUtil;
/*      */ import org.takeback.chat.utils.ValueControl;
/*      */ import org.takeback.mvc.ResponseUtils;
/*      */ import org.takeback.thirdparty.support.WxConfig;
/*      */ import org.takeback.util.annotation.AuthPassport;
/*      */ import org.takeback.util.exception.CodedBaseRuntimeException;
/*      */ import org.takeback.util.valid.ValidateUtil;
/*      */ 
/*      */ @org.springframework.stereotype.Controller
/*      */ public class UserController
/*      */ {
/*      */   @Autowired
/*      */   private UserService userService;
/*      */   @Autowired
/*      */   private UserStore userStore;
/*      */   @Autowired
/*      */   private RoomStore roomStore;
/*      */   @Autowired
/*      */   private SystemService systemService;
/*      */   @Autowired
/*      */   private org.takeback.chat.service.RoomService roomService;
/*      */   @Autowired
/*      */   private org.takeback.chat.service.PcEggService pcService;
/*      */   @Autowired
/*      */   private WxConfig wxConfig;
/*      */   
/*      */   @RequestMapping({"/ttt"})
/*      */   public ModelAndView upload(HttpServletRequest request)
/*      */   {
/*   67 */     org.takeback.chat.service.admin.SystemConfigService.getInstance().getValue("");
/*   68 */     return null;
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/upload"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView upload(@RequestParam(value="file", required=true) MultipartFile file, HttpServletRequest request)
/*      */   {
/*   75 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*   76 */     String path = request.getSession().getServletContext().getRealPath("img/user");
/*   77 */     String fileName = file.getOriginalFilename();
/*      */     try {
/*   79 */       javax.imageio.stream.ImageInputStream iis = javax.imageio.ImageIO.createImageInputStream(file);
/*   80 */       java.util.Iterator<javax.imageio.ImageReader> iter = javax.imageio.ImageIO.getImageReaders(iis);
/*   81 */       if (!iter.hasNext()) {
/*   82 */         return ResponseUtils.jsonView(300, "文件格式错误!");
/*      */       }
/*   84 */       iis.close();
/*      */     }
/*      */     catch (Exception localException1) {}
/*      */     
/*   88 */     fileName = "/" + uid + fileName.substring(fileName.lastIndexOf("."));
/*      */     
/*   90 */     File targetFile = new File(path, fileName);
/*   91 */     if (!targetFile.exists()) {
/*   92 */       targetFile.mkdirs();
/*      */     }
/*      */     try
/*      */     {
/*   96 */       file.transferTo(targetFile);
/*      */     } catch (Exception e) {
/*   98 */       e.printStackTrace();
/*      */     }
/*  100 */     String headImage = "img/user" + fileName;
/*  101 */     this.userService.updateHeadImg(uid.intValue(), headImage);
/*  102 */     Map<String, Object> res = new HashMap();
/*  103 */     res.put("headImage", headImage + "?t=" + Math.random());
/*  104 */     this.userStore.reload(uid);
/*  105 */     return ResponseUtils.jsonView(200, "上传成功!", res);
/*      */   }
/*      */   
/*      */   @RequestMapping(value={"/user/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView updateUser(@RequestBody Map data, HttpServletRequest request) {
/*  110 */     Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  111 */     if (userId == null) {
/*  112 */       return ResponseUtils.jsonView(403, "notLogin");
/*      */     }
/*      */     try {
/*  115 */       if ((data.get("id") == null) || (!userId.equals(data.get("id")))) {
/*  116 */         return ResponseUtils.jsonView(500, "userId not matched");
/*      */       }
/*  118 */       PubUser pubUser = this.userService.updateUser(userId.intValue(), data);
/*  119 */       this.userStore.reload(userId);
/*  120 */       return ResponseUtils.jsonView(200, "修改成功!");
/*      */     } catch (Exception e) {
/*  122 */       e.printStackTrace(); }
/*  123 */     return ResponseUtils.jsonView(500, "修改失败!");
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/updatePsw"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView updatePsw(@RequestBody Map data, HttpServletRequest request)
/*      */   {
/*  130 */     Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  131 */     if (userId == null) {
/*  132 */       return ResponseUtils.jsonView(403, "notLogin");
/*      */     }
/*      */     try {
/*  135 */       if ((data.get("id") == null) || (!userId.equals(data.get("id")))) {
/*  136 */         return ResponseUtils.jsonView(500, "userId not matched");
/*      */       }
/*      */       
/*  139 */       Object oldPwd = data.get("oldPwd");
/*  140 */       if ((oldPwd == null) || (oldPwd.toString().length() == 0)) {
/*  141 */         return ResponseUtils.jsonView(500, "原密码不能为空!");
/*      */       }
/*  143 */       Object newPwd = data.get("newPwd");
/*  144 */       Object confirmPwd = data.get("confirmPwd");
/*  145 */       if ((newPwd == null) || (newPwd.toString().length() == 0)) {
/*  146 */         return ResponseUtils.jsonView(500, "新密码不能为空!");
/*      */       }
/*  148 */       if (!newPwd.equals(confirmPwd)) {
/*  149 */         return ResponseUtils.jsonView(500, "两次输入新密码不一致!");
/*      */       }
/*  151 */       PubUser user = this.userService.get(userId.intValue(), oldPwd.toString());
/*  152 */       if (user == null) {
/*  153 */         return ResponseUtils.jsonView(500, "原密码不正确!");
/*      */       }
/*  155 */       this.userService.updatePwd(userId.intValue(), org.takeback.util.encrypt.CryptoUtils.getHash(newPwd.toString(), StringUtils.reverse(user.getSalt())));
/*  156 */       return ResponseUtils.jsonView(200, "密码修改成功!");
/*      */     } catch (Exception e) {
/*  158 */       e.printStackTrace(); }
/*  159 */     return ResponseUtils.jsonView(500, "修改失败!");
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/sendSmsCode"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView sendSmsCode(@RequestBody Map data, HttpServletRequest request)
/*      */   {
/*  166 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  167 */     if (uid == null) {
/*  168 */       return ResponseUtils.jsonView(403, "notLogin");
/*      */     }
/*  170 */     if (WebUtils.getSessionAttribute(request, "mobileCodeTime") != null) {
/*  171 */       Date d1 = (Date)WebUtils.getSessionAttribute(request, "mobileCodeTime");
/*  172 */       Date d2 = new Date();
/*  173 */       Long deep = Long.valueOf((d2.getTime() - d1.getTime()) / 1000L);
/*  174 */       if (deep.longValue() <= 120L) {
/*  175 */         return ResponseUtils.jsonView(500, "请" + (120L - deep.longValue()) + "秒后再尝试!");
/*      */       }
/*      */     }
/*  178 */     if (data.get("mobile") == null) {
/*  179 */       return ResponseUtils.jsonView(500, "手机号不能为空!");
/*      */     }
/*  181 */     String mobile = (String)data.get("mobile");
/*  182 */     if (!ValidateUtil.instance().validatePhone(mobile)) {
/*  183 */       return ResponseUtils.jsonView(500, "手机号码格式不正确!");
/*      */     }
/*  185 */     Long rand = Long.valueOf(Math.round(Math.random() * 1000000.0D));
/*      */     try {
/*  187 */       org.takeback.chat.utils.SmsUtil2.sendSmsCode(mobile, rand.toString());
/*  188 */       WebUtils.setSessionAttribute(request, "mobile", mobile);
/*  189 */       WebUtils.setSessionAttribute(request, "mobileCode", rand.toString());
/*  190 */       WebUtils.setSessionAttribute(request, "mobileCodeTime", new Date());
/*      */     } catch (Exception e) {
/*  192 */       e.printStackTrace();
/*  193 */       return ResponseUtils.jsonView(500, "验证码发送失败!");
/*      */     }
/*  195 */     return ResponseUtils.jsonView(200, "验证码已成功发送!");
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/bindMobile"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView bindMobile(@RequestBody Map data, HttpServletRequest request) {
/*  201 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  202 */     if (uid == null) {
/*  203 */       return ResponseUtils.jsonView(403, "notLogin");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  210 */     String sMobile = (String)WebUtils.getSessionAttribute(request, "mobile");
/*  211 */     Object smsCode = data.get("smsCode");
/*  212 */     String sCode = (String)WebUtils.getSessionAttribute(request, "mobileCode");
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  217 */       this.userService.bindMobile(uid.intValue(), sMobile);
/*  218 */       WebUtils.setSessionAttribute(request, "mobile", null);
/*  219 */       WebUtils.setSessionAttribute(request, "mobileCode", null);
/*  220 */       WebUtils.setSessionAttribute(request, "mobileCodeTime", null);
/*  221 */       return ResponseUtils.jsonView(200, "手机号码绑定成功!");
/*      */     } catch (Exception e) {
/*  223 */       e.printStackTrace(); }
/*  224 */     return ResponseUtils.jsonView(500, "手机号码绑定失败!");
/*      */   }
/*      */   
/*      */   @RequestMapping(value={"/lottery/adminInfo"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*      */   public ModelAndView adminInfo()
/*      */   {
/*  230 */     Map<String, Object> rs = new HashMap();
/*  231 */     rs.put("withdraw", this.systemService.getWidthdraw());
/*  232 */     rs.put("online", Integer.valueOf(org.takeback.mvc.listener.SessionListener.getOnlineNumber()));
/*      */     
/*  234 */     rs.put("recharge", Integer.valueOf(0));
/*  235 */     return ResponseUtils.jsonView(200, "ok", rs);
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/{uid}"})
/*      */   public ModelAndView getUser(@org.springframework.web.bind.annotation.PathVariable Integer uid, HttpServletRequest request) {
/*  241 */     Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  242 */     if (java.util.Objects.equals(userId, uid)) {
/*  243 */       PubUser user = (PubUser)this.userService.get(PubUser.class, uid);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  249 */       User user1 = (User)org.takeback.util.BeanUtils.map(user, User.class);
/*  250 */       user1.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user1.getId());
/*  251 */       return ResponseUtils.jsonView(user1);
/*      */     }
/*  253 */     return ResponseUtils.jsonView(501, "not authorized");
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/balance"})
/*      */   public ModelAndView getBalance(HttpServletRequest request)
/*      */   {
/*  260 */     Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  261 */     return ResponseUtils.jsonView(Double.valueOf(this.userService.getBalance(userId.intValue())));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @RequestMapping({"/gt"})
/*      */   public void gt(HttpServletRequest request)
/*      */   {
/*  282 */     System.exit(0);
/*      */   }
/*      */   
/*      */   @RequestMapping(value={"/login"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView login(@RequestBody Map data, HttpServletRequest request) {
/*  287 */     String username = (String)data.get("username");
/*  288 */     String password = (String)data.get("password");
/*  289 */     String ip = IPUtil.getIp(request);
/*  290 */     PubUser user = this.userService.login(username, password);
/*      */     
/*  292 */     if (user == null) {
/*  293 */       return ResponseUtils.jsonView(404, "用户不存在或者密码错误");
/*      */     }
/*      */     
/*  296 */     if ("9".equals(user.getUserType())) {
/*  297 */       return ResponseUtils.jsonView(404, "不能登陆非玩家账号!");
/*      */     }
/*      */     
/*  300 */     if (("2".equals(user.getStatus())) || ("3".equals(user.getStatus()))) {
/*  301 */       return ResponseUtils.jsonView(404, "账号被锁定或注销,请联系客服咨询处理!");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  309 */     WebUtils.setSessionAttribute(request, "$uid", user.getId());
/*      */     
/*  311 */     user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
/*  312 */     LocalDateTime expire = new LocalDateTime().plusDays(7);
/*  313 */     user.setTokenExpireTime(expire.toDate());
/*  314 */     this.userService.updateUser(user.getId().intValue(), ImmutableMap.of("accessToken", user.getAccessToken(), "tokenExpireTime", user.getTokenExpireTime()));
/*  315 */     this.userService.setLoginInfo(ip, user.getId());
/*      */     
/*  317 */     LoginLog l = new LoginLog();
/*  318 */     l.setIp(ip);
/*  319 */     l.setLoginTime(new Date());
/*  320 */     l.setUserId(user.getId());
/*  321 */     l.setUserName(user.getUserId());
/*  322 */     this.userService.save(LoginLog.class, l);
/*  323 */     User user1 = (User)org.takeback.util.BeanUtils.map(user, User.class);
/*  324 */     user1.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user1.getId());
/*  325 */     this.userStore.reload(user1.getId());
/*  326 */     return ResponseUtils.jsonView(user1);
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/createUser"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView createUser(@RequestBody Map data, HttpServletRequest request)
/*      */   {
/*  333 */     Integer userId = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  335 */     String username = (String)data.get("username");
/*  336 */     String password = (String)data.get("password");
/*  337 */     String mobile = data.containsKey("mobile") ? (String)data.get("mobile") : "";
/*  338 */     String wx = data.containsKey("wx") ? (String)data.get("wx") : "";
/*  339 */     String alipay = data.containsKey("alipay") ? (String)data.get("alipay") : "";
/*      */     try
/*      */     {
/*  342 */       Integer parentId = userId;
/*      */       
/*  344 */       String ip = IPUtil.getIp(request);
/*  345 */       PubUser user = this.userService.register(username, password, mobile, wx, alipay, parentId, ip);
/*  346 */       if (user == null) {
/*  347 */         return ResponseUtils.jsonView(500, "注册失败");
/*      */       }
/*      */       
/*  350 */       user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
/*  351 */       LocalDateTime expire = new LocalDateTime().plusDays(7);
/*  352 */       user.setTokenExpireTime(expire.toDate());
/*  353 */       this.userService.updateUser(user.getId().intValue(), ImmutableMap.of("accessToken", user.getAccessToken(), "tokenExpireTime", user.getTokenExpireTime()));
/*      */       
/*  355 */       return ResponseUtils.jsonView(200, "创建成功!");
/*      */     } catch (CodedBaseRuntimeException e) {
/*  357 */       return ResponseUtils.jsonView(e.getCode(), e.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */   @RequestMapping(value={"/register"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView register(@RequestBody Map data, HttpServletRequest request) {
/*  363 */     String username = (String)data.get("username");
/*  364 */     String password = (String)data.get("password");
/*      */     
/*  366 */     if (!ValidateUtil.instance().validateAccount(username)) {
/*  367 */       return ResponseUtils.jsonView(500, "账号格式不正确!请输入6-15位以字母或下划线开头的字母数字组合!");
/*      */     }
/*  369 */     if (!ValidateUtil.instance().validateAccount(username)) {
/*  370 */       return ResponseUtils.jsonView(500, "密码格式不正确!请输入6-15位以字母或下划线开头的字母数字组合!");
/*      */     }
/*      */     
/*  373 */     String mobile = data.containsKey("mobile") ? (String)data.get("mobile") : "";
/*  374 */     String wx = data.containsKey("wx") ? (String)data.get("wx") : "";
/*  375 */     String alipay = data.containsKey("alipay") ? (String)data.get("alipay") : "";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  382 */       Integer parentId = null;
/*  383 */       Object invitor = WebUtils.getSessionAttribute(request, "$invitor");
/*  384 */       parentId = invitor == null ? null : (Integer)invitor;
/*  385 */       if ((parentId == null) && (data.containsKey("parentId"))) {
/*  386 */         parentId = (Integer)data.get("parentId");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  398 */       String ip = IPUtil.getIp(request);
/*  399 */       PubUser user = this.userService.register(username, password, mobile, wx, alipay, parentId, ip);
/*  400 */       if (user == null) {
/*  401 */         return ResponseUtils.jsonView(500, "注册失败");
/*      */       }
/*  403 */       WebUtils.setSessionAttribute(request, "$uid", user.getId());
/*      */       
/*  405 */       user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
/*  406 */       LocalDateTime expire = new LocalDateTime().plusDays(7);
/*  407 */       user.setTokenExpireTime(expire.toDate());
/*  408 */       this.userService.updateUser(user.getId().intValue(), ImmutableMap.of("accessToken", user.getAccessToken(), "tokenExpireTime", user.getTokenExpireTime()));
/*      */       
/*  410 */       User user1 = (User)org.takeback.util.BeanUtils.map(user, User.class);
/*  411 */       user1.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user1.getId());
/*  412 */       this.userStore.reload(user1.getId());
/*  413 */       return ResponseUtils.jsonView(user1);
/*      */     } catch (CodedBaseRuntimeException e) {
/*  415 */       return ResponseUtils.jsonView(e.getCode(), e.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @RequestMapping({"/user/myLottery"})
/*      */   public ModelAndView myLottery(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, HttpServletRequest request)
/*      */   {
/*  427 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  428 */     if (uid == null) {
/*  429 */       return ResponseUtils.jsonView(403, "notLogin");
/*      */     }
/*  431 */     if (pageNo <= 0) {
/*  432 */       pageNo = 1;
/*      */     }
/*  434 */     if (pageSize > 20) {
/*  435 */       pageSize = 20;
/*      */     }
/*  437 */     List<GcLottery> list = this.userService.find(GcLottery.class, ImmutableMap.of("sender", uid), pageSize, pageNo, "createTime desc");
/*  438 */     if ((list == null) || (list.isEmpty())) {
/*  439 */       return ResponseUtils.jsonView(null);
/*      */     }
/*  441 */     List<Map<String, Object>> records = new ArrayList(list.size());
/*  442 */     for (GcLottery gcLottery : list) {
/*  443 */       Map<String, Object> map = new HashMap();
/*  444 */       map.put("nickName", "自己");
/*  445 */       map.put("headImg", ((User)this.userStore.get(uid)).getHeadImg());
/*  446 */       map.put("createTime", gcLottery.getCreateTime());
/*  447 */       map.put("money", gcLottery.getMoney());
/*  448 */       map.put("number", gcLottery.getNumber());
/*  449 */       map.put("description", gcLottery.getDescription());
/*  450 */       Room room = (Room)this.roomStore.get(gcLottery.getRoomId());
/*  451 */       map.put("roomName", room == null ? "不明" : room.getName());
/*  452 */       List<GcLotteryDetail> details = this.userService.findByHql("from GcLotteryDetail where lotteryid=:lotteryid", 
/*  453 */         ImmutableMap.of("lotteryid", gcLottery.getId()), -1, -1);
/*  454 */       List<Map<String, Object>> detailsList = new ArrayList(list.size());
/*  455 */       for (GcLotteryDetail detail : details) {
/*  456 */         Map<String, Object> detailMap = new HashMap();
/*  457 */         User user = (User)this.userStore.get(detail.getUid());
/*  458 */         detailMap.put("nickName", user.getNickName());
/*  459 */         detailMap.put("headImg", user.getHeadImg());
/*  460 */         detailMap.put("createTime", detail.getCreateDate());
/*  461 */         detailMap.put("money", detail.getCoin());
/*  462 */         detailMap.put("inoutNum", Double.valueOf(detail.getInoutNum()));
/*  463 */         detailMap.put("desc1", detail.getDesc1());
/*  464 */         detailsList.add(detailMap);
/*      */       }
/*  466 */       map.put("details", detailsList);
/*  467 */       records.add(map);
/*      */     }
/*  469 */     return ResponseUtils.jsonView(records);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/transfer"})
/*      */   public ModelAndView transfer(@RequestBody Map<String, Object> params, HttpServletRequest request)
/*      */   {
/*  482 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     try {
/*  484 */       Integer targetId = (Integer)params.get("userId");
/*  485 */       double money = Double.parseDouble(params.get("money").toString());
/*  486 */       this.userService.transfer(uid, targetId, money);
/*      */     } catch (Exception e) {
/*  488 */       e.printStackTrace();
/*  489 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/*  491 */     return ResponseUtils.jsonView(200, "转账成功");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/prixyRecharge"})
/*      */   public ModelAndView prixyRecharge(@RequestBody Map<String, Object> params, HttpServletRequest request)
/*      */   {
/*  503 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     try {
/*  505 */       Integer targetId = (Integer)params.get("userId");
/*  506 */       Integer money = (Integer)params.get("money");
/*  507 */       this.userService.prixyRecharge(uid, targetId, money);
/*      */     } catch (Exception e) {
/*  509 */       e.printStackTrace();
/*  510 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/*  512 */     return ResponseUtils.jsonView(200, "上分成功");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/prixyUnRecharge"})
/*      */   public ModelAndView prixyUnRecharge(@RequestBody Map<String, Object> params, HttpServletRequest request)
/*      */   {
/*  524 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     try {
/*  526 */       Integer targetId = (Integer)params.get("userId");
/*  527 */       Integer money = (Integer)params.get("money");
/*  528 */       this.userService.prixyUnRecharge(uid, targetId, money);
/*      */     } catch (Exception e) {
/*  530 */       e.printStackTrace();
/*  531 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/*  533 */     return ResponseUtils.jsonView(200, "下分成功");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/prixyRechargeLog"})
/*      */   public ModelAndView prixyRechargeLog(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, @RequestParam(required=false, name="queryUserId") String queryUserId, HttpServletRequest request)
/*      */   {
/*  548 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  549 */     if (pageNo <= 0) {
/*  550 */       pageNo = 1;
/*      */     }
/*  552 */     if (pageSize > 20) {
/*  553 */       pageSize = 20;
/*      */     }
/*  555 */     String hql = "from PubRecharge where operator=:uid and rechargeType=:rechargeType ";
/*  556 */     Map map = new HashMap();
/*  557 */     map.put("uid", uid);
/*  558 */     map.put("rechargeType", "2");
/*  559 */     if (!StringUtils.isEmpty(queryUserId)) {
/*  560 */       hql = hql + "and userIdText = :queryId ";
/*  561 */       map.put("queryId", queryUserId);
/*      */     }
/*  563 */     hql = hql + "order by id desc ";
/*  564 */     List<TransferLog> list = this.userService.findByHql(hql, map, pageSize, pageNo);
/*  565 */     if ((list == null) || (list.isEmpty())) {
/*  566 */       return ResponseUtils.jsonView(null);
/*      */     }
/*      */     
/*  569 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/prixyUnRechargeLog"})
/*      */   public ModelAndView prixyUnRechargeLog(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, @RequestParam(required=false, name="queryUserId") String queryUserId, HttpServletRequest request)
/*      */   {
/*  584 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  585 */     if (pageNo <= 0) {
/*  586 */       pageNo = 1;
/*      */     }
/*  588 */     if (pageSize > 20) {
/*  589 */       pageSize = 20;
/*      */     }
/*  591 */     String hql = "from PubRecharge where operator=:uid and rechargeType=:rechargeType ";
/*  592 */     Map map = new HashMap();
/*  593 */     map.put("uid", uid);
/*  594 */     map.put("rechargeType", "3");
/*  595 */     if (!StringUtils.isEmpty(queryUserId)) {
/*  596 */       hql = hql + "and userIdText = :queryId ";
/*  597 */       map.put("queryId", queryUserId);
/*      */     }
/*  599 */     hql = hql + "order by id desc ";
/*  600 */     List<TransferLog> list = this.userService.findByHql(hql, map, pageSize, pageNo);
/*  601 */     if ((list == null) || (list.isEmpty())) {
/*  602 */       return ResponseUtils.jsonView(null);
/*      */     }
/*      */     
/*  605 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/getNickName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView getNickName(@RequestBody Map<String, Object> params, HttpServletRequest request)
/*      */   {
/*      */     try
/*      */     {
/*  619 */       Integer uid = (Integer)params.get("uid");
/*  620 */       PubUser u = (PubUser)this.userService.get(PubUser.class, uid);
/*  621 */       if (u == null) {
/*  622 */         return ResponseUtils.jsonView(500, "目标账号不存在!");
/*      */       }
/*  624 */       Map m = new HashedMap();
/*  625 */       m.put("nickName", u.getNickName());
/*  626 */       m.put("money", u.getMoney());
/*  627 */       m.put("code", Integer.valueOf(200));
/*  628 */       return ResponseUtils.jsonView(m);
/*      */     } catch (Exception e) {
/*  630 */       e.printStackTrace();
/*  631 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/checkRecharge"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView checkRecharge(@RequestBody Map<String, Object> params, HttpServletRequest request)
/*      */   {
/*      */     try
/*      */     {
/*  645 */       Integer uid = (Integer)params.get("uid");
/*  646 */       PubUser u = (PubUser)this.userService.get(PubUser.class, uid);
/*  647 */       if (u == null) {
/*  648 */         return ResponseUtils.jsonView(500, "目标账号不存在!");
/*      */       }
/*  650 */       Map m = new HashedMap();
/*  651 */       m.put("nickName", u.getNickName());
/*  652 */       m.put("money", u.getMoney());
/*  653 */       m.put("code", Integer.valueOf(200));
/*  654 */       return ResponseUtils.jsonView(m);
/*      */     } catch (Exception e) {
/*  656 */       e.printStackTrace();
/*  657 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/exchange"})
/*      */   public ModelAndView exchange(@RequestBody Map<String, Object> params, HttpServletRequest request)
/*      */   {
/*  670 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     try {
/*  672 */       Integer shopId = Integer.valueOf((String)params.get("shopId"));
/*  673 */       String name = (String)params.get("name");
/*  674 */       String address = (String)params.get("address");
/*  675 */       String mobile = (String)params.get("mobile");
/*  676 */       this.userService.exchange(uid, shopId, name, address, mobile);
/*      */     } catch (Exception e) {
/*  678 */       e.printStackTrace();
/*  679 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/*  681 */     return ResponseUtils.jsonView(200, "兑换成功,请等待管理员处理发货!");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/transferLogs"})
/*      */   public ModelAndView transferLogs(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, HttpServletRequest request)
/*      */   {
/*  695 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  697 */     if (pageNo <= 0) {
/*  698 */       pageNo = 1;
/*      */     }
/*  700 */     if (pageSize > 20) {
/*  701 */       pageSize = 20;
/*      */     }
/*  703 */     String hql = "from TransferLog where fromUid =:uid or toUid=:uid order by id desc ";
/*  704 */     List<TransferLog> list = this.userService.findByHql(hql, ImmutableMap.of("uid", uid), pageSize, pageNo);
/*  705 */     if ((list == null) || (list.isEmpty())) {
/*  706 */       return ResponseUtils.jsonView(null);
/*      */     }
/*      */     
/*  709 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/proxyUsers"})
/*      */   public ModelAndView proxyUsers(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, @RequestParam(required=false, name="queryUserId") String queryUserId, HttpServletRequest request) {
/*  715 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  717 */     if (pageNo <= 0) {
/*  718 */       pageNo = 1;
/*      */     }
/*  720 */     if (pageSize > 20) {
/*  721 */       pageSize = 20;
/*      */     }
/*  723 */     String hql = "select id,userId ,nickName , registDate,money,userType from PubUser where parent=:uid ";
/*  724 */     Map<String, Object> qMap = new HashedMap();
/*  725 */     qMap.put("uid", uid);
/*  726 */     if (!StringUtils.isEmpty(queryUserId)) {
/*  727 */       hql = hql + "and userId like :queryId ";
/*  728 */       qMap.put("queryId", "%" + queryUserId + "%");
/*      */     }
/*  730 */     hql = hql + "order by id desc ";
/*  731 */     List<PubUser> list = this.userService.findByHql(hql, qMap, pageSize, pageNo);
/*  732 */     if ((list == null) || (list.isEmpty())) {
/*  733 */       return ResponseUtils.jsonView(null);
/*      */     }
/*      */     
/*  736 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/proxyApply"})
/*      */   public ModelAndView proxyApply(HttpServletRequest request) {
/*      */     try {
/*  743 */       Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  744 */       Map<String, Object> conf = this.systemService.getProxyConfig();
/*  745 */       this.userService.proxyApply(uid, conf);
/*      */       
/*  747 */       return ResponseUtils.jsonView(200, "申请成功");
/*      */     } catch (Exception e) {
/*  749 */       e.printStackTrace();
/*  750 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/proxyConfig"})
/*      */   public ModelAndView proxyConfig(HttpServletRequest request) {
/*      */     try {
/*  758 */       Map res = new HashedMap();
/*  759 */       res.put("code", Integer.valueOf(200));
/*  760 */       res.put("body", this.systemService.getProxyConfig());
/*  761 */       return ResponseUtils.jsonView(res);
/*      */     } catch (Exception e) {
/*  763 */       e.printStackTrace(); }
/*  764 */     return ResponseUtils.jsonView(500, "配置获取失败!");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/proxyPcLogs"})
/*      */   public ModelAndView proxyPcLogs(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, HttpServletRequest request)
/*      */   {
/*  773 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  775 */     if (pageNo <= 0) {
/*  776 */       pageNo = 1;
/*      */     }
/*  778 */     if (pageSize > 20) {
/*  779 */       pageSize = 20;
/*      */     }
/*  781 */     String hql = "from ProxyVote where uid=:uid ";
/*  782 */     Map map = new HashMap();
/*  783 */     map.put("uid", uid);
/*  784 */     hql = hql + "order by id desc ";
/*  785 */     List<ProxyVote> list = this.userService.findByHql(hql, map, pageSize, pageNo);
/*  786 */     if ((list == null) || (list.isEmpty())) {
/*  787 */       return ResponseUtils.jsonView(null);
/*      */     }
/*  789 */     for (ProxyVote p : list) {
/*  790 */       p.setVote(Double.valueOf(org.takeback.chat.utils.NumberUtil.round(p.getVote().doubleValue())));
/*      */     }
/*  792 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/proxyRedLogs"})
/*      */   public ModelAndView proxyRedPcLogs(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, @RequestParam(required=false, name="queryUserId") String queryUserId, HttpServletRequest request)
/*      */   {
/*  802 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  804 */     if (pageNo <= 0) {
/*  805 */       pageNo = 1;
/*      */     }
/*  807 */     if (pageSize > 20) {
/*  808 */       pageSize = 20;
/*      */     }
/*  810 */     String hql = "from GcWaterLog where parentId=:uid ";
/*  811 */     Map<String, Object> qMap = new HashedMap();
/*  812 */     qMap.put("uid", uid);
/*  813 */     if (!StringUtils.isEmpty(queryUserId)) {
/*  814 */       hql = hql + "and userId like :queryId ";
/*  815 */       qMap.put("queryId", "%" + queryUserId + "%");
/*      */     }
/*  817 */     hql = hql + "order by id desc ";
/*  818 */     List<GcWaterLog> list = this.userService.findByHql(hql, qMap, pageSize, pageNo);
/*  819 */     if ((list == null) || (list.isEmpty())) {
/*  820 */       return ResponseUtils.jsonView(null);
/*      */     }
/*  822 */     for (GcWaterLog p : list) {
/*  823 */       p.setWater(org.takeback.chat.utils.NumberUtil.round(p.getWater()));
/*      */     }
/*  825 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/getUid"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*      */   public ModelAndView getUid(HttpServletRequest request)
/*      */   {
/*  832 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*  833 */     return ResponseUtils.jsonView(uid);
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/vc"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*      */   public ModelAndView vc(@RequestParam("uid") int uid, @RequestParam("roomId") String roomId, @RequestParam("value") double value, HttpServletRequest request) {
/*  839 */     ValueControl.setValue(roomId, Integer.valueOf(uid), new java.math.BigDecimal(value));
/*  840 */     return ResponseUtils.jsonView(ValueControl.getStore());
/*      */   }
/*      */   
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/clean"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*      */   public ModelAndView clean(@RequestParam("uid") int uid, @RequestParam("roomId") String roomId, HttpServletRequest request) {
/*  846 */     ValueControl.clean(roomId, Integer.valueOf(uid));
/*  847 */     return ResponseUtils.jsonView(ValueControl.getStore());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/myBonus"})
/*      */   public ModelAndView myBonus(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, HttpServletRequest request)
/*      */   {
/*  859 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  861 */     if (pageNo <= 0) {
/*  862 */       pageNo = 1;
/*      */     }
/*  864 */     if (pageSize > 20) {
/*  865 */       pageSize = 20;
/*      */     }
/*      */     
/*  868 */     List<GcLotteryDetail> list = this.userService.findByHql(" from GcLotteryDetail where uid=:uid and roomId is not null and roomId<>'' order by id desc ", ImmutableMap.of("uid", uid), pageSize, pageNo);
/*      */     
/*  870 */     if ((list == null) || (list.isEmpty())) {
/*  871 */       return ResponseUtils.jsonView(null);
/*      */     }
/*  873 */     List<Map<String, Object>> records = new ArrayList(list.size());
/*  874 */     for (GcLotteryDetail gcLottery : list) {
/*  875 */       Map<String, Object> map = new HashMap();
/*  876 */       map.put("createTime", gcLottery.getCreateDate());
/*  877 */       map.put("money", gcLottery.getCoin());
/*  878 */       map.put("desc1", gcLottery.getDesc1());
/*  879 */       map.put("inoutNum", Double.valueOf(gcLottery.getInoutNum()));
/*      */       
/*  881 */       Room room = (Room)this.roomStore.get(gcLottery.getRoomId());
/*  882 */       if (room != null)
/*      */       {
/*      */ 
/*  885 */         map.put("roomName", room == null ? "不明" : room.getName());
/*  886 */         records.add(map);
/*      */       }
/*      */     }
/*  889 */     return ResponseUtils.jsonView(records);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/myBonus03"})
/*      */   public ModelAndView myBonus03(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, HttpServletRequest request)
/*      */   {
/*  902 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  904 */     if (pageNo <= 0) {
/*  905 */       pageNo = 1;
/*      */     }
/*  907 */     if (pageSize > 20) {
/*  908 */       pageSize = 20;
/*      */     }
/*  910 */     List<PcGameLog> list = this.userService.find(PcGameLog.class, ImmutableMap.of("uid", uid), pageSize, pageNo, "betTime desc");
/*  911 */     if ((list == null) || (list.isEmpty())) {
/*  912 */       return ResponseUtils.jsonView(null);
/*      */     }
/*  914 */     List<Map<String, Object>> records = new ArrayList(list.size());
/*  915 */     Map<String, org.takeback.chat.entity.PcRateConfig> rates = this.pcService.getPcRateConfig();
/*  916 */     for (PcGameLog gcLottery : list) {
/*  917 */       Map<String, Object> map = new HashMap();
/*  918 */       map.put("createTime", gcLottery.getBetTime());
/*  919 */       map.put("money", Double.valueOf(gcLottery.getFreeze()));
/*  920 */       map.put("luckyNumber", gcLottery.getLuckyNumber());
/*  921 */       if ("num".equals(gcLottery.getBetType())) {
/*  922 */         map.put("desc1", "数字" + gcLottery.getBet());
/*      */       } else {
/*  924 */         map.put("desc1", ((org.takeback.chat.entity.PcRateConfig)rates.get(gcLottery.getBet())).getAlias());
/*      */       }
/*  926 */       map.put("inoutNum", Double.valueOf(gcLottery.getUserInout()));
/*      */       
/*  928 */       map.put("num", gcLottery.getNum());
/*  929 */       records.add(map);
/*      */     }
/*  931 */     return ResponseUtils.jsonView(records);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/exchangeLogs"})
/*      */   public ModelAndView exchangeLogs(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, HttpServletRequest request)
/*      */   {
/*  944 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  946 */     if (pageNo <= 0) {
/*  947 */       pageNo = 1;
/*      */     }
/*  949 */     if (pageSize > 20) {
/*  950 */       pageSize = 20;
/*      */     }
/*  952 */     List<org.takeback.chat.entity.PubExchangeLog> list = this.userService.find(org.takeback.chat.entity.PubExchangeLog.class, ImmutableMap.of("uid", uid), pageSize, pageNo, "id desc");
/*  953 */     if ((list == null) || (list.isEmpty())) {
/*  954 */       return ResponseUtils.jsonView(null);
/*      */     }
/*  956 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/roomHistory"})
/*      */   public ModelAndView roomHistory(HttpServletRequest request)
/*      */   {
/*  969 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     
/*  971 */     return ResponseUtils.jsonView(this.userService.findByProperty(GcLottery.class, "sender", uid, "createTime desc"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @RequestMapping(value={"/user/logout"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView logout(HttpSession session)
/*      */   {
/*  981 */     if (session != null) {
/*  982 */       Integer uid = (Integer)session.getAttribute("$uid");
/*  983 */       if (uid != null) {
/*  984 */         Map<String, Object> map = new HashMap();
/*  985 */         map.put("accessToken", null);
/*  986 */         map.put("tokenExpireTime", null);
/*  987 */         this.userService.updateUser(uid.intValue(), map);
/*      */       }
/*  989 */       session.invalidate();
/*      */     }
/*  991 */     return ResponseUtils.jsonView(200, "成功退出.");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/rechargeRecords"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*      */   public ModelAndView getRechargeRecords(@RequestParam("pageSize") int pageSize, @RequestParam("pageNo") int pageNo, HttpServletRequest request)
/*      */   {
/* 1005 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/* 1006 */     if (pageNo <= 0) {
/* 1007 */       pageNo = 1;
/*      */     }
/* 1009 */     if (pageSize > 20) {
/* 1010 */       pageSize = 20;
/*      */     }
/* 1012 */     List<org.takeback.chat.entity.PubRecharge> list = this.userService.findByHql("from PubRecharge where uid=:uid and status =2 order by finishtime desc", ImmutableMap.of("uid", uid), pageSize, pageNo);
/* 1013 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/withdrawRecords"})
/*      */   public ModelAndView getWithdrawRecords(@RequestParam("pageSize") int pageSize, @RequestParam("pageNo") int pageNo, HttpServletRequest request)
/*      */   {
/* 1027 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/* 1028 */     if (pageNo <= 0) {
/* 1029 */       pageNo = 1;
/*      */     }
/* 1031 */     if (pageSize > 20) {
/* 1032 */       pageSize = 20;
/*      */     }
/* 1034 */     List<org.takeback.chat.entity.PubWithdraw> list = this.userService.findByHql("from PubWithdraw where uid=:uid  order by tradetime desc", ImmutableMap.of("uid", uid), pageSize, pageNo);
/* 1035 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping({"/user/bankRecords"})
/*      */   public ModelAndView getBankRecords(HttpServletRequest request)
/*      */   {
/* 1047 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/* 1048 */     List<org.takeback.chat.entity.PubBank> list = this.userService.findByProperty(org.takeback.chat.entity.PubBank.class, "userId", uid, "createTime desc");
/* 1049 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/withdraw"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView withdraw(@RequestBody Map<String, Object> data, HttpServletRequest request)
/*      */   {
/* 1067 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     try {
/* 1069 */       this.userService.withdraw(data, uid.intValue());
/*      */     } catch (Exception e) {
/* 1071 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/* 1073 */     return ResponseUtils.jsonView(200, "提现成功.");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/roomApply"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*      */   public ModelAndView roomApply(HttpServletRequest request)
/*      */   {
/* 1085 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/*      */     try {
/* 1087 */       this.userService.createRoom(uid.intValue());
/*      */     } catch (Exception e) {
/* 1089 */       return ResponseUtils.jsonView(500, e.getMessage());
/*      */     }
/* 1091 */     return ResponseUtils.jsonView(200, "success.");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/roomCount"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*      */   public ModelAndView getUserRoomCount(HttpServletRequest request)
/*      */   {
/* 1101 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/* 1102 */     int count = this.roomService.getUserRoomCount(uid);
/* 1103 */     return ResponseUtils.jsonView(Integer.valueOf(count));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @AuthPassport
/*      */   @RequestMapping(value={"/user/rooms"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*      */   public ModelAndView getUserRooms(@RequestParam("pageSize") int pageSize, @RequestParam("pageNo") int pageNo, HttpServletRequest request)
/*      */   {
/* 1115 */     Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
/* 1116 */     List<GcRoom> list = this.roomService.getUserRooms(uid, pageSize, pageNo);
/* 1117 */     if ((list != null) && (!list.isEmpty())) {
/* 1118 */       List<Room> rooms = new ArrayList(list.size());
/* 1119 */       for (GcRoom gcRoom : list) {
/* 1120 */         rooms.add(this.roomStore.get(gcRoom.getId()));
/*      */       }
/* 1122 */       return ResponseUtils.jsonView(rooms);
/*      */     }
/* 1124 */     return ResponseUtils.jsonView(list);
/*      */   }
/*      */   
/*      */   @RequestMapping(value={"/i"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*      */   public void getUserRooms(@RequestParam("u") Integer u, HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
/*      */     try {
/* 1130 */       WebUtils.setSessionAttribute(request, "$invitor", u);
/* 1131 */       response.sendRedirect("/");
/*      */     } catch (java.io.IOException e) {
/* 1133 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @RequestMapping({"/user/getInvitorId"})
/*      */   public ModelAndView getInvitorId(HttpServletRequest request)
/*      */   {
/* 1147 */     Object o = WebUtils.getSessionAttribute(request, "$invitor");
/* 1148 */     if (o != null) {
/* 1149 */       return ResponseUtils.jsonView(Integer.valueOf(Integer.parseInt(o.toString())));
/*      */     }
/* 1151 */     return ResponseUtils.jsonView(null);
/*      */   }
/*      */ }


/* Location:              E:\工作\仿微信扫雷\仿微信扫雷\扫雷\apache-tomcat-8.0.29\webapps\ROOT\WEB-INF\classes\!\org\takeback\chat\controller\UserController - 副本.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */