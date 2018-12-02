// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.organ;

import org.takeback.core.controller.ConfigurableLoader;
import org.takeback.core.controller.support.AbstractController;

public class OrganController extends AbstractController<Organization>
{
    private static OrganController instance;
    
    public OrganController() {
        this.setLoader(new OrganLocalLoader());
        OrganController.instance = this;
    }
    
    public static OrganController instance() {
        if (OrganController.instance == null) {
            OrganController.instance = new OrganController();
        }
        return OrganController.instance;
    }
    
    public static Organization getRoot() {
        return instance().get("unit");
    }
}
