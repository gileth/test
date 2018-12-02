// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collections;
import org.takeback.util.converter.ConversionUtils;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.takeback.core.controller.Configurable;

public class Application extends ApplicationNode implements Configurable
{
    private static final long serialVersionUID = 3549131476086910545L;
    protected Long lastModi;
    private Integer pageCount;
    private Map<String, String> refMap;
    
    public Application() {
        this.refMap = new HashMap<String, String>();
    }
    
    @Override
    public List<ApplicationNode> getItems() {
        if (this.deep >= this.getRequestDeep()) {
            return null;
        }
        return super.getItems();
    }
    
    @Override
    public <T> T getProperty(final String nm, final Class<T> targetType) {
        return ConversionUtils.convert(this.getProperty(nm), targetType);
    }
    
    @Override
    public Long getlastModify() {
        return this.lastModi;
    }
    
    @Override
    public void setLastModify(final Long lastModi) {
        this.lastModi = lastModi;
    }
    
    public Integer getPageCount() {
        if (this.pageCount == null) {
            return 0;
        }
        return this.pageCount;
    }
    
    public void setPageCount(final Integer pageCount) {
        this.pageCount = pageCount;
    }
    
    @JsonIgnore
    public Map<String, String> getRefItems() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.refMap);
    }
    
    public void addRefItem(final ApplicationNode node) {
        this.refMap.put(node.getRef(), node.getFullId());
    }
}
