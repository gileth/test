// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.user;

import org.takeback.core.controller.ConfigurableLoader;
import org.takeback.core.controller.support.AbstractController;

public class UserController extends AbstractController<User>
{
    private static UserController instance;
    
    public UserController() {
        this.setLoader(new UserLocalLoader());
        UserController.instance = this;
    }
    
    public static UserController instance() {
        if (UserController.instance == null) {
            UserController.instance = new UserController();
        }
        return UserController.instance;
    }
}
