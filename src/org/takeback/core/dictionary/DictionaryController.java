// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary;

import org.takeback.core.controller.ConfigurableLoader;
import org.takeback.core.controller.support.AbstractController;

public class DictionaryController extends AbstractController<Dictionary>
{
    private static DictionaryController instance;
    
    private DictionaryController() {
        this.setLoader(new DictionaryLocalLoader());
        DictionaryController.instance = this;
    }
    
    public static DictionaryController instance() {
        if (DictionaryController.instance == null) {
            DictionaryController.instance = new DictionaryController();
        }
        return DictionaryController.instance;
    }
}
