// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import org.takeback.chat.utils.SmsUtil2;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListService;

@Service("withdrawAdminService")
public class WithdrawAdminService extends MyListService
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = req.get("data");
        final Integer id = Integer.valueOf(data.get("id"));
        final String hql1 = new StringBuffer("select status from PubWithdraw where id=").append(id).toString();
        final String preStatus = this.dao.getUnique(hql1, new Object[0]);
        if (!"1".equals(preStatus)) {
            throw new CodedBaseRuntimeException("\u7533\u8bf7\u5904\u7406\u5df2\u7ecf\u5b8c\u6210,\u65e0\u6cd5\u4fee\u6539\u4fdd\u5b58\uff01");
        }
        final String status = data.get("status");
        final Integer uid = Integer.valueOf(data.get("uid").toString());
        final PubUser user = this.dao.get(PubUser.class, uid);
        final Double fee = Double.valueOf(data.get("fee").toString());
        if ("2".equals(status) && user.getMobile() != null && !"".equals(user.getMobile())) {
            SmsUtil2.send(user.getMobile(), "\u60a8\u7684\u63d0\u73b0\u7533\u8bf7\u5df2\u7ecf\u5904\u7406\u6210\u529f,\u8bf7\u6ce8\u610f\u67e5\u6536!");
        }
        if ("9".equals(status)) {
            final String hql2 = "update PubUser set chargeAmount=chargeAmount + :water , money=money+ :money where id = :id";
            final Map<String, Object> param = new HashMap<String, Object>();
            param.put("water", fee);
            param.put("money", fee);
            param.put("id", uid);
            this.dao.executeUpdate(hql2, param);
            if (user.getMobile() != null && !"".equals(user.getMobile())) {
                SmsUtil2.send(user.getMobile(), "\u60a8\u7684\u63d0\u73b0\u7533\u8bf7\u6ca1\u6709\u901a\u8fc7\u5ba1\u6838,\u8bf7\u5c3d\u5feb\u5904\u7406\u5e76\u91cd\u65b0\u63d0\u4ea4\u7533\u8bf7!");
            }
        }
        super.save(req);
    }
}
