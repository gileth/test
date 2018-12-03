// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.user;

import org.takeback.chat.store.Item;
import java.util.concurrent.ExecutionException;
import org.takeback.util.exception.CodedBaseException;
import org.takeback.util.BeanUtils;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.UserService;

public class DefaultUserStore implements UserStore
{
    @Autowired
    private UserService userService;
    private LoadingCache<Integer, User> store;
    
    @Override
    public void init() {
        this.store = (LoadingCache<Integer, User>)CacheBuilder.newBuilder().expireAfterAccess(4L, TimeUnit.HOURS).build((CacheLoader)new CacheLoader<Integer, User>() {
            public User load(final Integer uid) throws Exception {
                final PubUser user = DefaultUserStore.this.userService.get(PubUser.class, uid);
                if (user != null) {
                    return BeanUtils.map(user, User.class);
                }
                throw new CodedBaseException(530, "user " + uid + " not exists");
            }
        });
    }
    
    @Override
    public long size() {
        return this.store.size();
    }
    
    @Override
    public User get(final Serializable uid) {
        try {
            return (User)this.store.get((Integer) uid);
        }
        catch (ExecutionException e) {
            return null;
        }
    }
    
    @Override
    public void reload(final Serializable uid) {
        this.store.invalidate(uid);
    }
}
