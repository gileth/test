// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.PubUser;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.takeback.mvc.listener.SessionListener;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("onlineUserService")
public class OnlineUserService extends MyListServiceInt
{
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> list(final Map<String, Object> req) {
        final String entityName = (String) req.get(OnlineUserService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final int limit = (Integer) req.get(OnlineUserService.LIMIT);
        final int page = (Integer) req.get(OnlineUserService.PAGE);
        final Map<Integer, List<String>> users = SessionListener.getUsers();
        final Set<Integer> keyset = users.keySet();
        final Integer start = (page - 1) * limit;
        final Integer end = start + limit;
        final Iterator itr = keyset.iterator();
        Integer idx = -1;
        final StringBuffer keys = new StringBuffer();
        while (itr.hasNext()) {
            ++idx;
            if (idx < start) {
                itr.next();
            }
            else {
                keys.append(itr.next()).append(",");
                if (idx + 1 >= end) {
                    break;
                }
                continue;
            }
        }
        final Map<String, Object> result = new HashMap<String, Object>();
        if (keys.length() < 1) {
            result.put("totalSize", 0);
            result.put("body", new ArrayList());
            return result;
        }
        final String key = keys.substring(0, keys.length() - 1);
        final String hql = new StringBuffer("from PubUser where id in(").append(key).append(")").toString();
        final List<PubUser> ls = this.dao.findByHql(hql);
        final long count = users.size();
        result.put("totalSize", count);
        result.put("body", ls);
        return result;
    }
}
