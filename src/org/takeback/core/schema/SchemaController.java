// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.schema;

import org.takeback.core.controller.ConfigurableLoader;
import org.takeback.core.controller.support.AbstractController;

public class SchemaController extends AbstractController<Schema>
{
    private static SchemaController instance;
    
    public SchemaController() {
        this.setLoader(new SchemaLocalLoader());
        SchemaController.instance = this;
    }
    
    public static SchemaController instance() {
        if (SchemaController.instance == null) {
            SchemaController.instance = new SchemaController();
        }
        return SchemaController.instance;
    }
}
