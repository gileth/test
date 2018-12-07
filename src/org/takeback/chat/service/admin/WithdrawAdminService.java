// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import org.takeback.chat.utils.SmsUtil;
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
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final Integer id = Integer.valueOf((String) data.get("id"));
        final String hql1 = new StringBuffer("select status from PubWithdraw where id=").append(id).toString();
        final String preStatus = this.dao.getUnique(hql1, new Object[0]);
        if (!"1".equals(preStatus)) {
            throw new CodedBaseRuntimeException("申请处理已经完成,无法修改保存！");
        }
        final String status = (String) data.get("status");
        final Integer uid = Integer.valueOf(data.get("uid").toString());
        final PubUser user = this.dao.get(PubUser.class, uid);
        final Double fee = Double.valueOf(data.get("fee").toString());
        if ("2".equals(status) && user.getMobile() != null && !"".equals(user.getMobile())) {
            SmsUtil.send(user.getMobile(), "您的提现申请已经处理成功,请注意查收!");
        }
        if ("9".equals(status)) {
            final String hql2 = "update PubUser set chargeAmount=chargeAmount + :water , money=money+ :money where id = :id";
            final Map<String, Object> param = new HashMap<String, Object>();
            param.put("water", fee);
            param.put("money", fee);
            param.put("id", uid);
            this.dao.executeUpdate(hql2, param);
            if (user.getMobile() != null && !"".equals(user.getMobile())) {
                SmsUtil.send(user.getMobile(), "您的提现申请没有通过审核,请尽快处理并重新提交申请!");
            }
        }
        super.save(req);
    }
}