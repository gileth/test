// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.verification.message;

import java.util.Map;

public interface MessageProcessor
{
    String sendCode(final String p0);
    
    String sendCode(final String p0, final String p1);
    
    String sendSMS(final String p0, final String p1, final Map<String, String> p2);
    
    String sendSMS(final String p0, final String p1);
    
    void setDefaultCodeLength(final int p0);
}
