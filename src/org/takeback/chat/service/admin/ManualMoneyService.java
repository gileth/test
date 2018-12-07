// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;
import org.takeback.util.context.ContextUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.takeback.chat.entity.PubUser;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("manualMoneyService")
public class ManualMoneyService extends MyListServiceInt
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String userIdText = (String) data.get("userIdText");
        final PubUser user = this.dao.getUnique(PubUser.class, "userId", userIdText);
        if (user == null) {
            throw new CodedBaseRuntimeException("用户不存在!");
        }
        Double money;
        try {
            money = Double.valueOf(data.get("money").toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new CodedBaseRuntimeException("金额不正确");
        }
        final String hql = "update PubUser a set a.money = Coalesce(a.money,0) + :money where a.id=:uid";
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("money", money);
        param.put("uid", user.getId());
        final int i = this.dao.executeUpdate(hql, param);
        if (i == 1) {
            data.put("userId", user.getId());
            data.put("createTime", new Date());
            final HttpServletRequest request = (HttpServletRequest)ContextUtils.get("$httpRequest");
            data.put("operator", WebUtils.getSessionAttribute(request, "$uid"));
            super.save(req);
            return;
        }
        throw new CodedBaseRuntimeException("余额增加失败!");
    }
}