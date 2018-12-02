// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.param;

import org.takeback.util.params.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.service.BaseService;
import org.takeback.util.params.support.MemeryParamLoader;

public class DBStoreParamLoader extends MemeryParamLoader
{
    @Autowired
    private BaseService baseService;
    
    @Override
    public String getParam(final String parName, final String defaultValue, final String paramalias) {
        Param p = this.params.get(parName);
        if (p != null) {
            return p.getParamvalue();
        }
        p = this.baseService.getUnique(Param.class, "paramname", parName);
        if (p != null) {
            this.params.put(parName, new Param(parName, p.getParamvalue(), p.getParamalias()));
            return p.getParamvalue();
        }
        if (defaultValue == null) {
            return null;
        }
        p = new Param(parName, defaultValue, paramalias);
        this.params.put(parName, p);
        this.baseService.save(Param.class, p);
        return defaultValue;
    }
    
    @Override
    public void setParam(final String parName, final String value) {
        super.setParam(parName, value);
        final Param param = this.baseService.getUnique(Param.class, "paramname", parName);
        if (null == param) {
            this.baseService.save(Param.class, new Param(parName, value));
        }
        else {
            param.setParamvalue(value);
            this.baseService.update(Param.class, param);
        }
    }
    
    @Override
    public void removeParam(final String parName) {
        super.removeParam(parName);
        final Param p = this.baseService.getUnique(Param.class, "paramname", parName);
        if (p != null) {
            this.baseService.delete(Param.class, p);
        }
    }
}
