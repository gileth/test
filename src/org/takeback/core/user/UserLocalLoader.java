// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.user;

import org.slf4j.LoggerFactory;
import org.takeback.core.controller.Configurable;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.takeback.util.ApplicationContextHolder;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.takeback.core.controller.ConfigurableLoader;

public class UserLocalLoader implements ConfigurableLoader<User>
{
    private static final Logger log;
    private static final String UserQueryHQL = "from User a where a.id = :id and (a.status is null or a.status = '1')";
    private static final String UserRolesQueryHQL = "from UserRoleToken a where a.userid = :id";
    
    @Override
    public User load(final String id) {
        final SessionFactory sf = ApplicationContextHolder.getBean("sessionFactory", SessionFactory.class);
        Session ss = null;
        try {
            ss = sf.openSession();
            Query q = ss.createQuery("from User a where a.id = :id and (a.status is null or a.status = '1')");
            q.setString("id", id);
            final User user = (User)q.uniqueResult();
            if (user == null) {
                return null;
            }
            q = ss.createQuery("from UserRoleToken a where a.userid = :id");
            q.setString("id", id);
            final List<UserRoleToken> urs = (List<UserRoleToken>)q.list();
            for (final UserRoleToken ur : urs) {
                user.addUserRoleToken(ur);
            }
            return user;
        }
        catch (Exception e) {
            UserLocalLoader.log.error("load user " + id + " failed", (Throwable)e);
            return null;
        }
        finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    static {
        log = LoggerFactory.getLogger((Class)UserLocalLoader.class);
    }
}
