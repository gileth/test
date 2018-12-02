// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.context;

public class ContextUtils
{
    private static ThreadLocal<Context> threadContext;
    
    public static void setContext(final Context ctx) {
        ContextUtils.threadContext.set(ctx);
    }
    
    public static Context getContext() {
        Context ctx = ContextUtils.threadContext.get();
        if (ctx == null) {
            ctx = new Context();
            setContext(ctx);
        }
        return ctx;
    }
    
    public static boolean hasKey(final String key) {
        final Context ctx = ContextUtils.threadContext.get();
        return ctx != null && ctx.containsKey(key);
    }
    
    public static Object get(final String key) {
        Context ctx = ContextUtils.threadContext.get();
        if (ctx == null) {
            ctx = new Context();
            setContext(ctx);
        }
        return ctx.get(key);
    }
    
    public static <T> T get(final String key, final Class<T> type) {
        Context ctx = ContextUtils.threadContext.get();
        if (ctx == null) {
            ctx = new Context();
            setContext(ctx);
        }
        return ctx.get(key, type);
    }
    
    public static void remove(final String key) {
        final Context ctx = ContextUtils.threadContext.get();
        if (ctx != null) {
            ctx.remove(key);
        }
    }
    
    public static void put(final String key, final Object v) {
        Context ctx = ContextUtils.threadContext.get();
        if (ctx == null) {
            ctx = new Context();
            setContext(ctx);
        }
        ctx.put(key, v);
    }
    
    public static void clear() {
        ContextUtils.threadContext.remove();
    }
    
    static {
        ContextUtils.threadContext = new ThreadLocal<Context>();
    }
}
