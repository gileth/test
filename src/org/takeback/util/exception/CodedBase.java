// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exception;

public interface CodedBase
{
    int getCode();
    
    String getMessage();
    
    Throwable getCause();
    
    StackTraceElement[] getStackTrace();
    
    void throwThis() throws Exception;
}
