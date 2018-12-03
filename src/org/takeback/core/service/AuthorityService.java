// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.service;

import java.util.Iterator;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.core.user.UserController;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.util.BeanUtils;
import java.io.Serializable;
import org.takeback.util.MD5StringUtil;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.core.user.User;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("authorityService")
public class AuthorityService extends MyListService
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final String entityName = (String) req.get(AuthorityService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String cmd = (String) req.get(AuthorityService.CMD);
        this.beforeProcessSaveData(data);
        try {
            final Class<?> cls = Class.forName(entityName);
            final User user = (User) ConversionUtils.convert(data, cls);
            final String id = user.getId();
            final String password = user.getPassword();
            if (!"******".equals(password)) {
                user.setPassword(MD5StringUtil.MD5Encode(password));
            }
            if ("update".equals(cmd)) {
                final User oUser = this.dao.get(User.class, id);
                if ("******".equals(user.getPassword())) {
                    user.setPassword(oUser.getPassword());
                }
                BeanUtils.copy(user, oUser);
                this.beforeSave(oUser);
                this.dao.getSession().update(oUser);
                DictionaryController.instance().reload("dic.users");
                UserController.instance().reload(id);
                return;
            }
            this.beforeSave(user);
            this.dao.getSession().save(user);
            DictionaryController.instance().reload("dic.users");
        }
        catch (ClassNotFoundException e) {
            throw new CodedBaseRuntimeException(510, "parse class[" + entityName + "] failed");
        }
    }
    
    @Override
    protected void afterList(final List<?> ls) {
        for (final Object user : ls) {
            ((User)user).setPassword("******");
        }
    }
    
    @Override
    protected void afterLoad(final Object entity) {
        ((User)entity).setPassword("******");
    }
}
