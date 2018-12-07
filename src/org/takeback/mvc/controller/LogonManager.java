// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller;

import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
import org.takeback.mvc.filter.AdminboardFilter;
import org.takeback.util.MD5StringUtil;
import org.takeback.util.exception.CodedBaseRuntimeException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.takeback.core.app.ApplicationNode;
import org.takeback.core.accredit.result.AuthorizeResult;
import org.takeback.core.app.Application;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.takeback.core.role.Role;
import com.google.common.collect.Maps;
import org.takeback.core.app.ApplicationController;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.takeback.core.organ.Organization;
import java.util.Collection;
import org.joda.time.DateTime;
import org.takeback.core.user.User;
import org.takeback.mvc.ServletUtils;
import java.sql.Timestamp;
import java.util.Date;
import org.takeback.core.organ.OrganController;
import org.takeback.util.converter.ConversionUtils;
import java.util.HashMap;
import org.takeback.core.user.UserRoleToken;
import org.takeback.core.user.AccountCenter;
import org.springframework.web.util.WebUtils;
import org.takeback.mvc.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.takeback.core.service.InitializeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.service.BaseService;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController("mvcLogonManager")
public class LogonManager
{
    private static final Logger log;
    @Autowired
    private BaseService baseService;
    @Autowired
    private InitializeService initializeService;
    
    @RequestMapping(value = { "/logon/loadRoles" }, method = { RequestMethod.POST })
    public Map<String, Object> logon(@RequestBody final Map<String, String> req, final HttpServletRequest request) {
        final String uid = req.get("account");
        final String psw = req.get("password");
        if (StringUtils.isEmpty((CharSequence)uid) || StringUtils.isEmpty((CharSequence)psw)) {
            return ResponseUtils.createBody(501, "用户名或者密码不能为空！");
        }
        final String verifycode = req.get("verifycode");
        if (StringUtils.isEmpty((CharSequence)verifycode) || !verifycode.equalsIgnoreCase((String)WebUtils.getSessionAttribute(request, "verifycode"))) {
            return ResponseUtils.createBody(504, "验证码不正确");
        }
        WebUtils.setSessionAttribute(request, "verifycode", null);
        final User user = AccountCenter.getUser(uid);
        if (user == null) {
            return ResponseUtils.createBody(503, "用户不存在");
        }
        if (!user.validatePassword(psw)) {
            return ResponseUtils.createBody(502, "密码不正确");
        }
        final Collection<UserRoleToken> urts = user.getUserRoleTokens();
        if (urts.size() < 1) {
            return ResponseUtils.createBody(505, "没有设置角色");
        }
        final UserRoleToken urt = urts.iterator().next();
        if (urt.getRole() == null) {
            return ResponseUtils.createBody(506, "所属角色不存在");
        }
        if (urt.getOrgan() == null) {
            return ResponseUtils.createBody(507, "所属机构不存在");
        }
        final HashMap urtMap = ConversionUtils.convert(urt, HashMap.class);
        final Organization organ = OrganController.getRoot();
        urtMap.put("company", organ.getName());
        urtMap.put("system", organ.getProperties().get("system"));
        urtMap.put("version", organ.getProperties().get("version"));
        urtMap.put("telephone", organ.getProperties().get("telephone"));
        urtMap.put("email", organ.getProperties().get("email"));
        urtMap.put("address", organ.getProperties().get("address"));
        AccountCenter.reloadUser(uid);
        AccountCenter.getUser(uid);
        final Timestamp logintime = new Timestamp(new Date().getTime());
        user.setLastsignintime(logintime);
        user.setLastsigninip(ServletUtils.getClientIP(request));
        this.baseService.update(User.class, user);
        WebUtils.setSessionAttribute(request, "$uid", user.getId());
        WebUtils.setSessionAttribute(request, "$urt", urt.getId());
        LogonManager.log.info(uid + " logon with role " + urt.getRoleid() + " at " + new DateTime().toString("yyyy-MM-dd HH:mm:ss") + ",IP:" + ServletUtils.getClientIP(request));
        return ResponseUtils.createBody(urtMap);
    }
    
    @RequestMapping(value = { "/logon/loadApps" }, method = { RequestMethod.POST })
    public Map<String, Object> loadApps(final HttpServletRequest request) {
        if (ServletUtils.isLogonExpired(request)) {
            return ResponseUtils.createBody(403, "notLogon");
        }
        final String uid = (String)WebUtils.getSessionAttribute(request, "$uid");
        final Long urtid = (Long)WebUtils.getSessionAttribute(request, "$urt");
        final UserRoleToken urt = AccountCenter.getUser(uid).getUserRoleToken(urtid);
        final Role role = urt.getRole();
        final Set<String> installApps = OrganController.getRoot().installedApps();
        final List<Object> apps = (List<Object>)Lists.newArrayList();
        for (final String app : installApps) {
            final Application application = ApplicationController.instance().get(app);
            if (application == null) {
                continue;
            }
            final AuthorizeResult result = role.authorize("apps", application.getFullId());
            if (result != null && result.getResult() == 0) {
                continue;
            }
            final Map<String, Object> levelApp = new HashMap<String, Object>();
            levelApp.put("id", application.getFullId());
            levelApp.put("text", application.getName());
            levelApp.put("iconCls", application.getIconCls());
            levelApp.put("level", "app");
            this.filterCata(levelApp, application, role);
            apps.add(levelApp);
        }
        return ResponseUtils.createBody(apps);
    }
    
    private void filterCata(final Map<String, Object> m, final Application a, final Role r) {
        final List<Object> catas = (List<Object>)Lists.newArrayList();
        m.put("children", catas);
        final List<ApplicationNode> ls = a.getItems();
        for (final ApplicationNode c : ls) {
            final AuthorizeResult result = r.authorize("apps", c.getFullId());
            if (result != null && result.getResult() == 0) {
                continue;
            }
            final Map<String, Object> levelCata = new HashMap<String, Object>();
            levelCata.put("id", c.getFullId());
            levelCata.put("text", c.getName());
            levelCata.put("iconCls", c.getIconCls());
            levelCata.put("level", "category");
            this.filterModule(levelCata, c, r);
            catas.add(levelCata);
        }
    }
    
    private void filterModule(final Map<String, Object> m, final ApplicationNode a, final Role r) {
        final List<Object> modules = (List<Object>)Lists.newArrayList();
        m.put("children", modules);
        final List<ApplicationNode> ls = a.getItems();
        for (final ApplicationNode c : ls) {
            final AuthorizeResult result = r.authorize("apps", c.getFullId());
            if (result != null && result.getResult() == 0) {
                continue;
            }
            final Map<String, Object> levelModule = new HashMap<String, Object>();
            levelModule.put("id", c.getFullId());
            levelModule.put("text", c.getName());
            levelModule.put("iconCls", c.getIconCls());
            levelModule.put("level", "module");
            levelModule.put("leaf", "true");
            modules.add(levelModule);
        }
    }
    
    @RequestMapping(value = { "/adminboard/initSystem" }, method = { RequestMethod.POST })
    public void initAdminboard(@RequestParam final String username, @RequestParam final String passwd, @RequestParam final String repasswd, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=gbk");
            final PrintWriter pw = response.getWriter();
            if (this.initializeService.queryInitialized()) {
                throw new CodedBaseRuntimeException("Illegal operate ！");
            }
            if (!passwd.equals(repasswd)) {
                pw.println("<html><body>");
                pw.println("<h2>错误:密码不一致</h2>");
                pw.println("<body/><html/>");
            }
            else if (passwd.length() < 6) {
                pw.println("<html><body>");
                pw.println("<h2>错误:密码长度不合法</h2>");
                pw.println("<body/><html/>");
            }
            else if (username.trim().length() < 4) {
                pw.println("<html><body>");
                pw.println("<h2>错误:用户名长度不合法,长度应大于4！</h2>");
                pw.println("<body/><html/>");
            }
            else {
                final User user = new User();
                user.setId(username.trim());
                user.setPassword(MD5StringUtil.MD5Encode(passwd.trim()));
                final UserRoleToken roleToken = new UserRoleToken();
                roleToken.setUserid(username.trim());
                roleToken.setRoleid("roles.admin");
                roleToken.setOrganid("rdcenter");
                this.initializeService.initUser(user, roleToken);
                AdminboardFilter.systemInitialized = true;
                response.sendRedirect("/adminboard");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        log = LoggerFactory.getLogger((Class)LogonManager.class);
    }
}