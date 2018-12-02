// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import org.springframework.web.util.WebUtils;
import org.takeback.util.context.ContextUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListService;

@Service("manualChargeService")
public class ManualChargeService extends MyListService
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final Integer id = Integer.valueOf((String) data.get("id"));
        final String hql1 = new StringBuffer("select status from PubRecharge where id=").append(id).toString();
        final String status = this.dao.getUnique(hql1, new Object[0]);
        if ("1".equals(status)) {
            data.put("status", "2");
            final HttpServletRequest request = (HttpServletRequest)ContextUtils.get("$httpRequest");
            data.put("operator", WebUtils.getSessionAttribute(request, "$uid"));
            final Integer uid = Integer.valueOf((String) data.get("uid"));
            final String tradeno = (String) data.get("tradeno");
            final Double fee = (data.get("fee") == null) ? 0.0 : Double.valueOf((String) data.get("fee"));
            final Double gift = (data.get("gift") == null) ? 0.0 : Double.valueOf((String) data.get("gift"));
            final String hql2 = "update PubUser set money = COALESCE(money,0) + :add where id=:uid";
            final Map<String, Object> param = new HashMap<String, Object>();
            param.put("add", fee + gift);
            param.put("uid", uid);
            this.dao.executeUpdate(hql2, param);
            super.save(req);
        }
    }
}
