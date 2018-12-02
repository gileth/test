// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.app;

import java.util.List;

public class Category extends ApplicationNode
{
    private static final long serialVersionUID = 6735979041711571147L;
    
    public List<Module> getModules() {
        if (this.deep >= this.getRequestDeep()) {
            return null;
        }
        return super.getItems();
    }
}
