// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store;

import java.io.Serializable;

public interface Store<T extends Item>
{
    T get(final Serializable p0);
    
    void reload(final Serializable p0);
    
    void init();
}
