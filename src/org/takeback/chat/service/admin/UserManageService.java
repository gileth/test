// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import org.apache.commons.lang3.StringUtils;
import org.takeback.util.encrypt.CryptoUtils;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("userManageService")
public class UserManageService extends MyListServiceInt
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = req.get("data");
        final String cmd = req.get(UserManageService.CMD);
        if ("create".equals(cmd)) {
            try {
                final String salt = CryptoUtils.getSalt();
                final String psw = data.get("pwd");
                data.put("salt", salt);
                final String pwd = CryptoUtils.getHash(psw, StringUtils.reverse(salt));
                data.put("pwd", pwd);
                data.put("moneyCode", pwd);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            final PubUser user = this.dao.get(PubUser.class, Integer.valueOf(data.get("id").toString()));
            final String oldPwd = user.getPwd();
            if (!oldPwd.equals(data.get("pwd"))) {
                final String salt2 = CryptoUtils.getSalt();
                final String psw2 = data.get("pwd");
                data.put("salt", salt2);
                final String pwd2 = CryptoUtils.getHash(psw2, StringUtils.reverse(salt2));
                data.put("pwd", pwd2);
                data.put("moneyCode", pwd2);
            }
        }
        super.save(req);
    }
}
