// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContextAware;

@Component
public class RoomInitor implements ApplicationContextAware
{
    @Autowired
    RoomService service;
    
    @Transactional(rollbackFor = { Throwable.class })
    public void setApplicationContext(final ApplicationContext var1) {
        this.service.initRoomStatus();
    }
}
