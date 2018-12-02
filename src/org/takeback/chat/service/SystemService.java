// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.apache.commons.collections.map.HashedMap;
import org.takeback.chat.service.admin.SystemConfigService;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
public class SystemService extends BaseService
{
    @Transactional(rollbackFor = { Throwable.class })
    public Long getWidthdraw() {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", "1");
        final Long count = this.dao.count("select count(*) from PubWithdraw where status =:status", param);
        return count;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public Map<String, Object> getProxyConfig() {
        final Double money = Double.valueOf(SystemConfigService.getInstance().getValue("conf_proxy_money"));
        final Double exp = Double.valueOf(SystemConfigService.getInstance().getValue("conf_proxy_exp"));
        final Map body = (Map)new HashedMap();
        body.put("money", money);
        body.put("exp", exp);
        return (Map<String, Object>)body;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public Long getRecharge() {
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", "1");
        final Long count = this.dao.count("select count(*) from PubRecharge where status=:status", param);
        return count;
    }
}
