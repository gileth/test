// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.pay;

public class PaymentException extends Exception
{
    public PaymentException() {
    }
    
    public PaymentException(final String message) {
        super(message);
    }
    
    public PaymentException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
    
    public PaymentException(final Throwable throwable) {
        super(throwable);
    }
}
