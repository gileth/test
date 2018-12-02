// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContextAware;

@Component
public class ApplicationContextHolder implements ApplicationContextAware
{
    private static ApplicationContext applicationContext;
    private static boolean devMode;
    private static String name;
    
    public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        ApplicationContextHolder.applicationContext = ctx;
    }
    
    public static boolean containBean(final String beanName) {
        return ApplicationContextHolder.applicationContext.containsBean(beanName);
    }
    
    public static Object getBean(final String beanName) {
        return ApplicationContextHolder.applicationContext.getBean(beanName);
    }
    
    public static <T> T getBean(final String beanName, final Class<T> type) {
        return (T)ApplicationContextHolder.applicationContext.getBean(beanName, (Class)type);
    }
    
    public static <T> T getBean(final Class<T> cls) {
        return (T)ApplicationContextHolder.applicationContext.getBean((Class)cls);
    }
    
    public static boolean isDevMode() {
        return ApplicationContextHolder.devMode;
    }
    
    public static void setDevMode(final boolean devMode) {
        ApplicationContextHolder.devMode = devMode;
    }
    
    public static String getName() {
        return ApplicationContextHolder.name;
    }
    
    public static void setName(final String name) {
        ApplicationContextHolder.name = name;
    }
    
    static {
        ApplicationContextHolder.devMode = true;
    }
}
