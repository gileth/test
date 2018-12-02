// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.task.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.quartz.Scheduler;

public class ScheduleExecutor
{
    @Autowired
    private static Scheduler scheduler;
}
