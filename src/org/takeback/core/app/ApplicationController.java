// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.app;

import java.util.concurrent.ConcurrentHashMap;
import org.takeback.core.controller.Configurable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.controller.ConfigurableLoader;
import java.util.Map;
import org.takeback.core.controller.support.AbstractController;

public class ApplicationController extends AbstractController<Application>
{
    private static ApplicationController instance;
    private static Map<String, Map<String, String>> ref;
    
    public ApplicationController() {
        this.setLoader(new ApplicationLocalLoader());
        ApplicationController.instance = this;
    }
    
    public static ApplicationController instance() {
        if (ApplicationController.instance == null) {
            ApplicationController.instance = new ApplicationController();
        }
        return ApplicationController.instance;
    }
    
    @Override
    public void reload(final String id) {
        super.reload(id);
        ApplicationController.ref.remove(id);
    }
    
    @Override
    public void reloadAll() {
        this.store.clear();
        ApplicationController.ref.clear();
    }
    
    @Override
    public Application get(final String id) {
        final Application app = super.get(id);
        final Map<String, String> r = app.getRefItems();
        if (r.size() > 0) {
            ApplicationController.ref.put(app.getId(), app.getRefItems());
        }
        return app;
    }
    
    public ApplicationNode lookupModuleNode(final String id) {
        ApplicationNode node = null;
        if (!StringUtils.isEmpty((CharSequence)id)) {
            final String[] nodes = id.split("/");
            if (nodes.length <= 1) {
                return null;
            }
            final Application app = instance().get(nodes[0]);
            if (app == null) {
                return null;
            }
            node = app;
            for (int i = 1; i < nodes.length; ++i) {
                node = node.getChild(nodes[i]);
                if (node == null) {
                    break;
                }
            }
        }
        if (node == null) {
            return null;
        }
        return node;
    }
    
    private List<String> findMappingId(final String id) {
        final List<String> n = new ArrayList<String>();
        for (final Map<String, String> m : ApplicationController.ref.values()) {
            if (m.containsKey(id)) {
                n.add(m.get(id));
            }
        }
        n.add(id);
        return n;
    }
    
    static {
        ApplicationController.ref = new ConcurrentHashMap<String, Map<String, String>>();
    }
}
