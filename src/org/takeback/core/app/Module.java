// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.app;

import org.takeback.util.converter.ConversionUtils;
import java.util.List;

public class Module extends ApplicationNode
{
    private static final long serialVersionUID = -5866496056614584396L;
    private String script;
    private String implement;
    private Boolean runAsWindow;
    
    public List<Action> getActions() {
        if (this.deep >= this.getRequestDeep()) {
            return null;
        }
        return super.getItems();
    }
    
    public String getScript() {
        return this.script;
    }
    
    public void setScript(final String script) {
        this.script = script;
    }
    
    public <T> T getProperty(final String nm, final Class<T> targetType) {
        return ConversionUtils.convert(this.getProperty(nm), targetType);
    }
    
    public String getImplement() {
        return this.implement;
    }
    
    public void setImplement(final String implement) {
        this.implement = implement;
    }
    
    public Boolean isRunAsWindow() {
        return this.runAsWindow;
    }
    
    public void setRunAsWindow(final boolean runAsWindow) {
        this.runAsWindow = runAsWindow;
    }
}
