// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exp.exception;

import org.takeback.util.exception.CodedBaseRuntimeException;

public class ExprException extends CodedBaseRuntimeException
{
    private static final long serialVersionUID = -3712765640188038285L;
    
    public ExprException(final String msg) {
        super(msg);
    }
    
    public ExprException(final Throwable e) {
        super(e);
    }
}
