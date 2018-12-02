// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.dao.exception;

import org.takeback.util.exception.CodedBaseRuntimeException;

public class DaoException extends CodedBaseRuntimeException
{
    public DaoException() {
    }
    
    public DaoException(final int code) {
        super(code);
    }
    
    public DaoException(final int code, final String msg) {
        super(code, msg);
    }
    
    public DaoException(final String msg) {
        super(msg);
    }
    
    public DaoException(final int code, final Throwable t) {
        super(code, t);
    }
    
    public DaoException(final String msg, final Throwable t) {
        super(msg, t);
    }
    
    public DaoException(final Throwable t) {
        super(t);
    }
    
    public DaoException(final int code, final String msg, final Throwable t) {
        super(msg, t);
        this.code = code;
    }
}
