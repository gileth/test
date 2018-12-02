// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.user;

import org.takeback.core.organ.OrganController;
import org.takeback.core.organ.Organization;
import org.takeback.core.role.RoleController;
import org.takeback.core.role.Role;

public class AccountCenter
{
    public static User getUser(final String id) {
        return UserController.instance().get(id);
    }
    
    public static Role getRole(final String id) {
        return RoleController.instance().get(id);
    }
    
    public static Organization getOrgan(final String id) {
        return OrganController.instance().get(id);
    }
    
    public static void reloadUser(final String id) {
        UserController.instance().reload(id);
    }
}
