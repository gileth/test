// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.takeback.mvc.ResponseUtils;
import org.takeback.util.exp.ExpressionProcessor;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintWriter;
import org.takeback.mvc.controller.core.SessionListener;
import javax.servlet.http.HttpServletResponse;
import org.takeback.core.schema.SchemaController;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.organ.OrganController;
import org.takeback.core.role.RoleController;
import org.takeback.core.user.UserController;
import org.takeback.core.app.ApplicationController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "test" })
public class TestController
{
    @RequestMapping({ "reload" })
    public void reload() {
        ApplicationController.instance().reloadAll();
        UserController.instance().reloadAll();
        RoleController.instance().reloadAll();
        OrganController.instance().reloadAll();
        DictionaryController.instance().reloadAll();
        SchemaController.instance().reloadAll();
        System.out.println("reload all done.");
    }
    
    @RequestMapping({ "onlines" })
    public void createSession(final HttpServletResponse response) {
        try {
            final PrintWriter printWriter = response.getWriter();
            final Map<String, Object> users = SessionListener.getUsers();
            final int onlines = users.size();
            int loginUsers = 0;
            for (final Object obj : users.values()) {
                if (!"Anonymous".equals(obj)) {
                    ++loginUsers;
                }
            }
            printWriter.println("onlines: " + onlines + "\tloginOnlines: " + loginUsers + "\tanonymous: " + (onlines - loginUsers));
            printWriter.println(users.toString());
        }
        catch (Exception ex) {}
    }
    
    @RequestMapping(value = { "exp" }, method = { RequestMethod.POST }, headers = { "content-type=application/json" })
    public Map<String, Object> test1(@RequestBody final Map<String, ?> body, final String sign) {
        final List<?> exp = (List<?>)body.get("cnd");
        System.out.println(sign);
        final String a = ExpressionProcessor.instance().toString(exp);
        return ResponseUtils.createBody(a);
    }
}
