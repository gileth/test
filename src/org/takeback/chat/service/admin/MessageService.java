// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;
import org.takeback.util.context.ContextUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.takeback.chat.entity.PubUser;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("messageService")
public class MessageService extends MyListServiceInt
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String userName = (String) data.get("userIdText");
        String status = (data.get("status") == null) ? "0" : data.get("status").toString();
        if ("".equals(status)) {
            status = "0";
        }
        final PubUser user = this.dao.getUnique(PubUser.class, "userId", userName);
        if (user == null) {
            throw new CodedBaseRuntimeException("用户不存在!");
        }
        data.put("userId", user.getId());
        data.put("createTime", new Date());
        data.put("status", status);
        final HttpServletRequest request = (HttpServletRequest)ContextUtils.get("$httpRequest");
        data.put("createUser", WebUtils.getSessionAttribute(request, "$uid"));
        super.save(req);
    }
}