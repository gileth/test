// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.Date;
import org.takeback.chat.entity.PubRecharge;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
@Transactional(readOnly = true)
public class PubRechargeService extends BaseService
{
    @Transactional(rollbackFor = { Throwable.class })
    public void addRechargeRecord(final PubRecharge pubRecharge) {
        this.save(PubRecharge.class, pubRecharge);
    }
    
    public PubRecharge getRechargeRecordByTradeNo(final String tradeNo) {
        return this.getUnique(PubRecharge.class, "tradeno", tradeNo);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void setRechargeFinished(final PubRecharge pubRecharge) {
        final String sql1 = "update PubRecharge a set a.status = '2',realfee=:realfee,finishtime=:finishtime where tradeno=:tradeno and status='1'";
        final int effected = this.dao.executeUpdate(sql1, (Map<String, Object>)ImmutableMap.of("realfee", (Object)pubRecharge.getRealfee(), "tradeno", (Object)pubRecharge.getTradeno(), "finishtime", (Object)new Date()));
        if (effected == 1) {
            final String sql2 = "update PubUser a set a.money = COALESCE(a.money,0)+:money where id=:id";
            this.dao.executeUpdate(sql2, (Map<String, Object>)ImmutableMap.of("money", (Object)pubRecharge.getRealfee(), "id", (Object)pubRecharge.getUid()));
        }
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int setRechargeFailed(final PubRecharge pubRecharge) {
        final String sql = "update PubRecharge a set a.status = '3',finishtime=:finishtime where tradeno=:tradeno and status='1'";
        return this.dao.executeUpdate(sql, (Map<String, Object>)ImmutableMap.of("tradeno", (Object)pubRecharge.getTradeno(), "finishtime", (Object)new Date()));
    }
}
