// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.service;

import org.springframework.transaction.annotation.Transactional;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("bannerService")
public class BannerService extends MyListService
{
    private String bannerPath;
    
    public BannerService() {
        this.bannerPath = "/content/images/banner";
    }
    
    @Transactional
    public Object save1(final Map<String, Object> req) {
        final String entityName = req.get(BannerService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final Map<String, Object> data = req.get("data");
        this.beforeProcessSaveData(data);
        try {
            final Class<?> cls = Class.forName(entityName);
            final Object obj = ConversionUtils.convert(data, cls);
            this.beforeSave(obj);
            this.dao.getSession().saveOrUpdate(obj);
            return obj;
        }
        catch (ClassNotFoundException e) {
            throw new CodedBaseRuntimeException(510, "parse class[" + entityName + "] failed");
        }
    }
}
