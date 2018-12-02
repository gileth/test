// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import org.takeback.util.encrypt.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.takeback.chat.entity.PubUser;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("changePasswordService")
public class ChangePasswordService extends MyListServiceInt
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = req.get("data");
        final Integer id = Integer.valueOf(data.get("id").toString());
        final String pwd = data.get("pwd");
        req.put("id", id);
        final PubUser prev = (PubUser)this.load(req);
        final String prevPwd = prev.getPwd();
        final String salt = prev.getSalt();
        if (!prevPwd.equals(pwd)) {
            final String newPwd = CryptoUtils.getHash(pwd, StringUtils.reverse(salt));
            prev.setPwd(newPwd);
            prev.setMoneyCode(newPwd);
            this.dao.saveOrUpdate(PubUser.class, prev);
        }
    }
}
