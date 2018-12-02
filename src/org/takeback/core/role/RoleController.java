// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.role;

import org.takeback.core.controller.ConfigurableLoader;
import org.takeback.core.controller.support.AbstractController;

public class RoleController extends AbstractController<Role>
{
    private static RoleController instance;
    
    public RoleController() {
        this.setLoader(new RoleLocalLoader());
        RoleController.instance = this;
    }
    
    public static RoleController instance() {
        if (RoleController.instance == null) {
            RoleController.instance = new RoleController();
        }
        return RoleController.instance;
    }
}
