// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.user;

import org.takeback.util.exception.CodedBaseException;
import org.takeback.util.BeanUtils;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import com.google.common.cache.CacheLoader;

class DefaultUserStore$1 extends CacheLoader<Integer, User> {
    public User load(final Integer uid) throws Exception {
        final PubUser user = DefaultUserStore.access$000(DefaultUserStore.this).get(PubUser.class, uid);
        if (user != null) {
            return BeanUtils.map(user, User.class);
        }
        throw new CodedBaseException(530, "user " + uid + " not exists");
    }
}