// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadUtil
{
    private static final int DEFAULT_SIZE = 50;
    private static final int DEFAULT_MAX_SIZE = 150;
    private static ThreadPoolExecutor threadPoolExecutor;
    
    public static void execute(final Runnable task) {
        ThreadUtil.threadPoolExecutor.execute(task);
    }
    
    static {
        ThreadUtil.threadPoolExecutor = new ThreadPoolExecutor(50, 150, 5L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.AbortPolicy());
    }
}
