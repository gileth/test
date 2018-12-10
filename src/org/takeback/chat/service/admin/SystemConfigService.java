// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import java.util.Iterator;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import org.takeback.chat.entity.PubConfig;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContextAware;
import org.takeback.core.service.MyListServiceInt;

@Component
@Service("configService")
public class SystemConfigService extends MyListServiceInt implements ApplicationContextAware
{
    public static final String CTRL_FLAG = "control_flag";
    public static final String CTRL_DEFAULT_RATE = "control_default_rate";
    public static final String CTRL_KILL = "control_kill";
    public static final String CTRL_SAVE = "control_save";
    public static final String CTRL_INIT_MONEY = "conf_init_money";
    public static final String CTRL_TALK = "conf_talk";
    public static final String CTRL_TRANSFER = "conf_transfer";
    public static final String CTRL_PROXY_RECHARGE = "conf_proxyRecharge";
    public static final String CTRL_PROXY_WITHDRAW = "conf_proxyWithdraw";
    private List<PubConfig> cache;
    private static SystemConfigService instance;
    
    public static SystemConfigService getInstance() {
        return SystemConfigService.instance;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    @Override
    public void save(final Map<String, Object> req) {
        super.save(req);
        this.reload();
    }
    
    @Transactional(readOnly = true)
    public String getValue(final String key) {
        if (this.cache == null) {
            this.reload();
        }
        for (final PubConfig c : this.cache) {
            if (c.getParam().equals(key)) {
                return c.getVal();
            }
        }
        return null;
    }
    
    private void reload() {
        this.cache = this.dao.findByHql("from PubConfig");
    }
    
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        SystemConfigService.instance = (SystemConfigService)applicationContext.getBean(SystemConfigService.class);
    }
}
