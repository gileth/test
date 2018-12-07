// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.util.Date;
import org.takeback.chat.entity.PubUser;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.entity.PubExchangeLog;
import java.io.Serializable;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.PubShop;
import java.util.List;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
public class ShopService extends BaseService
{
    @Transactional
    public List<PubShop> list(final Integer pageNo, final Integer pageSize) {
        return this.dao.findByHqlPaging("from PubShop order by sortNum desc ", pageSize, pageNo);
    }
    
    @Transactional
    public PubShop get(final Integer id) {
        return this.dao.get(PubShop.class, id);
    }
    
    @Transactional
    public PubExchangeLog getContactInfo(final Integer id) {
        final List<PubExchangeLog> list = this.dao.findByHql("from PubExchangeLog where uid =:uid order by id desc", ImmutableMap.of( "uid", id));
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void doExchage(final Integer uid, final Integer shopId, final String name, final String address, final String mobile) {
        final PubShop s = this.dao.get(PubShop.class, shopId);
        if (s == null) {
            throw new CodedBaseRuntimeException("商品信息丢失");
        }
        final String hql = "update PubUser set money = coalesce(money,0) - :money where money > :money and id = :uid";
        final int effected = this.dao.executeUpdate(hql,  ImmutableMap.of( "money", s.getMoney(),  "uid",  uid));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("账户金额不足!");
        }
        final PubUser u = this.dao.get(PubUser.class, uid);
        final PubExchangeLog pel = new PubExchangeLog();
        pel.setUid(uid);
        pel.setNickName(u.getUserId());
        pel.setExchangeTime(new Date());
        pel.setMobile(mobile);
        pel.setMoney(s.getMoney());
        pel.setName(name);
        pel.setAddress(address);
        pel.setShopId(shopId.toString());
        pel.setShopName(s.getName());
        pel.setStatus("0");
        this.dao.save(PubExchangeLog.class, pel);
    }
}
