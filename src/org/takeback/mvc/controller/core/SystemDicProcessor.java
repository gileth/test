// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.mvc.controller.core;

import java.util.Collection;
import org.takeback.core.organ.Organization;
import org.takeback.core.organ.OrganController;
import java.io.File;
import org.springframework.core.io.Resource;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.role.RoleController;
import org.takeback.core.role.Role;
import org.takeback.core.resource.ResourceCenter;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.dom4j.Element;
import org.takeback.core.user.User;
import org.takeback.util.ApplicationContextHolder;
import org.hibernate.SessionFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Document;
import org.springframework.stereotype.Component;

@Component("systemDicProcessor")
public class SystemDicProcessor
{
    public Document getUsers() {
        final Document doc = DocumentHelper.createDocument();
        final Element root = doc.addElement("dic").addAttribute("name", "用户列表");
        final SessionFactory sf = ApplicationContextHolder.getBean("sessionFactory", SessionFactory.class);
        Session ss = null;
        try {
            ss = sf.openSession();
            final Query q = ss.createQuery("from User a where a.status is null or a.status = '1'");
            final List<User> users = (List<User>)q.list();
            for (final User user : users) {
                root.addElement("item").addAttribute("key", user.getId()).addAttribute("text", user.getName());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
        return doc;
    }
    
    public Document getRoles() {
        final Document doc = DocumentHelper.createDocument();
        final Element root = doc.addElement("dic").addAttribute("name", "角色列表");
        try {
            final Resource r = ResourceCenter.load("roles");
            if (r.exists()) {
                final File file = r.getFile();
                final String[] list;
                final String[] rolesName = list = file.list();
                for (final String roleName : list) {
                    final Role role = RoleController.instance().get("roles." + StringUtils.substringBeforeLast(roleName, ".r"));
                    root.addElement("item").addAttribute("key", role.getId()).addAttribute("text", role.getName());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }
    
    public Document getUnits() {
        final Document doc = DocumentHelper.createDocument();
        final Element root = doc.addElement("dic").addAttribute("name", "机构列表");
        final Organization unit = OrganController.getRoot();
        final Collection<Organization> units = unit.getChildren();
        for (final Organization u : units) {
            root.addElement("item").addAttribute("key", u.getId()).addAttribute("text", u.getName());
        }
        return doc;
    }
}
