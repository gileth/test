// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.service;

import java.io.Serializable;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.takeback.util.params.ParamUtils;
import org.takeback.util.params.Param;
import org.springframework.stereotype.Service;

@Service("systemParamsService")
public class SystemParamsService extends MyListService
{
    @Override
    protected void beforeSave(final Object obj) {
        final Param p = (Param)obj;
        ParamUtils.reload(p.getParamname());
    }
    
    @Override
    public void delete(final Map<String, Object> req) {
        final String entityName = req.get(SystemParamsService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final Object pkey = req.get("id");
        Serializable id = null;
        if (pkey instanceof Integer) {
            id = ConversionUtils.convert(pkey, Long.class);
        }
        else {
            id = ConversionUtils.convert(pkey, String.class);
        }
        final Param p = this.dao.get(entityName, id);
        if (p != null) {
            ParamUtils.reload(p.getParamname());
            this.dao.delete(entityName, id);
        }
    }
}
