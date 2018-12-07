// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller.core;

import org.dom4j.Element;
import org.takeback.core.app.Category;
import org.dom4j.DocumentHelper;
import org.dom4j.Document;
import java.io.File;
import org.springframework.core.io.Resource;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.resource.ResourceCenter;
import java.util.ArrayList;
import org.takeback.core.app.Application;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.takeback.core.accredit.result.AuthorizeResult;
import java.util.Iterator;
import java.util.List;
import org.takeback.core.role.Role;
import org.takeback.core.user.UserRoleToken;
import org.takeback.core.app.ApplicationNode;
import org.takeback.core.app.Action;
import org.takeback.util.BeanUtils;
import org.takeback.core.app.ApplicationController;
import org.takeback.core.app.Module;
import org.takeback.core.user.AccountCenter;
import org.springframework.web.util.WebUtils;
import org.takeback.mvc.ResponseUtils;
import org.takeback.mvc.ServletUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("appModuleLocator")
public class AppModuleLocator
{
    @RequestMapping(value = { "/**/*.app" }, method = { RequestMethod.GET })
    public Object getModule(@RequestParam final String moduleId, final HttpServletRequest request) {
        if (ServletUtils.isLogonExpired(request)) {
            return ResponseUtils.jsonView(403, "notLogon");
        }
        final String uid = (String)WebUtils.getSessionAttribute(request, "$uid");
        final Long urtid = (Long)WebUtils.getSessionAttribute(request, "$urt");
        final UserRoleToken urt = AccountCenter.getUser(uid).getUserRoleToken(urtid);
        final Role role = urt.getRole();
        final Module module = (Module)ApplicationController.instance().lookupModuleNode(moduleId);
        final Module m = new Module();
        BeanUtils.copy(module, m);
        m.setProperties(module.getProperties());
        final List<Action> actions = module.getActions();
        if (actions != null) {
            for (final Action action : actions) {
                final AuthorizeResult result = role.authorize("apps", action.getFullId());
                if (result != null && result.getResult() == 0) {
                    continue;
                }
                final Action ac = new Action();
                BeanUtils.copy(action, ac);
                ac.setProperties(action.getProperties());
                m.appendChild(ac);
            }
        }
        return m;
    }
    
    public List<Application> getApps() {
        final List<Application> apps = new ArrayList<Application>();
        try {
            final Resource r = ResourceCenter.load("app");
            if (r.exists()) {
                final File file = r.getFile();
                final String[] list;
                final String[] appNames = list = file.list();
                for (final String appName : list) {
                    final Application app = ApplicationController.instance().get("app." + StringUtils.substringBeforeLast(appName, ".app"));
                    apps.add(app);
                }
            }
        }
        catch (IOException ex) {}
        return apps;
    }
    
    public Document toDic() {
        final Document doc = DocumentHelper.createDocument();
        final Element root = doc.addElement("dic").addAttribute("name", "应用菜单");
        final List<Application> apps = this.getApps();
        for (final Application app : apps) {
            final Element eleA = root.addElement("item");
            eleA.addAttribute("key", app.getFullId()).addAttribute("text", app.getName());
            final List<ApplicationNode> itemsC = app.getItems();
            if (itemsC != null) {
                for (final ApplicationNode an : itemsC) {
                    final Category c = (Category)an;
                    final Element eleC = eleA.addElement("item").addAttribute("key", c.getFullId()).addAttribute("text", c.getName());
                    final List<Module> itemsM = c.getModules();
                    if (itemsM != null) {
                        for (final Module m : itemsM) {
                            this.process(eleC.addElement("item"), m);
                        }
                    }
                }
            }
        }
        return doc;
    }
    
    private void process(final Element ele, final Module m) {
        ele.addAttribute("key", m.getFullId()).addAttribute("text", m.getName()).addAttribute("script", m.getScript());
    }
}
