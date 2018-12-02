// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import java.util.Date;
import org.takeback.chat.utils.DateUtil;
import java.util.Calendar;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListService;

@Service("redDetailService")
public class RedDetailService extends MyListService
{
    @Transactional(rollbackFor = { Throwable.class })
    public void clear(final Map<String, Object> req) {
    	
        this.dao.executeUpdate("delete from GcLottery", ImmutableMap.of());
        this.dao.executeUpdate("delete from GcLotteryDetail", ImmutableMap.of());
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void clear2(final Map<String, Object> req) {
        final Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.getStartOfToday());
        c.add(5, -2);
        final Date d = c.getTime();
        this.dao.executeUpdate("delete from GcLottery where createTime <=:time", ImmutableMap.of( "time", (Object)c.getTime()));
        this.dao.executeUpdate("delete from GcLotteryDetail where createDate<=:time",  ImmutableMap.of( "time", (Object)c.getTime()));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void clear5(final Map<String, Object> req) {
        final Calendar c = Calendar.getInstance();
        c.setTime(DateUtil.getStartOfToday());
        c.add(5, -5);
        this.dao.executeUpdate("delete from GcLottery where createTime <=:time", ImmutableMap.of("time", (Object)c.getTime()));
        this.dao.executeUpdate("delete from GcLotteryDetail where createDate<=:time", ImmutableMap.of("time", (Object)c.getTime()));
    }
}
