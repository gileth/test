// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import org.takeback.util.exception.CodedBaseRuntimeException;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.entity.PubExchangeLog;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("exchangeAdminService")
public class ExchangeAdminService extends MyListServiceInt
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String status = (String) data.get("status");
        final Double money = Double.valueOf(data.get("money").toString());
        final String hql = "from PubExchangeLog where id=:id";
        final Integer id = Integer.valueOf(data.get("id").toString());
        final PubExchangeLog log = (PubExchangeLog) this.dao.findByHql(hql, ImmutableMap.of( "id",  id) ).get(0);
        if (!status.equals(log.getStatus()) && !"1".equals(log.getStatus())) {
            throw new CodedBaseRuntimeException(500, "处理状态已更新，不能重复修改");
        }
        if ("2".equals(status) && !"2".equals(log.getStatus())) {
            final Integer uid = Integer.valueOf(data.get("uid").toString());
            final String upd = "update PubUser set money =coalesce(money,0) + :money where id=:uid";
            this.dao.executeUpdate(upd,  ImmutableMap.of( "money", money, "uid",  uid) );
        }
        if ("1".equals(status) && "2".equals(log.getStatus())) {
            final Integer uid = Integer.valueOf(data.get("uid").toString());
            final String upd = "update PubUser set money =coalesce(money,0) - :money where id=:uid";
            this.dao.executeUpdate(upd, ImmutableMap.of( "money",  money,  "uid",  uid) );
        }
        super.save(req);
    }
}