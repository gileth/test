// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.service;

import org.takeback.core.user.UserRoleToken;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import org.takeback.core.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.dao.BaseDAO;
import org.springframework.stereotype.Service;

@Service("initializeService")
public class InitializeService
{
    @Autowired
    protected BaseDAO dao;
    
    @Transactional(readOnly = true)
    public Boolean queryInitialized() {
        final long count = this.dao.count(User.class, null);
        return count > 0L;
    }
    
    @Transactional
    public void initUser(final User user, final UserRoleToken role) {
        this.dao.save(User.class, user);
        this.dao.save(UserRoleToken.class, role);
    }
}
