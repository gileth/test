// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exception;

public class CodedBaseException extends Exception implements CodedBase
{
    protected static final long serialVersionUID = -8481811634176212223L;
    protected int code;
    
    public CodedBaseException() {
        this.code = 500;
    }
    
    public CodedBaseException(final int code) {
        this.code = 500;
        this.code = code;
    }
    
    public CodedBaseException(final String msg) {
        super(msg);
        this.code = 500;
    }
    
    public CodedBaseException(final int code, final String msg) {
        super(msg);
        this.code = 500;
        this.code = code;
    }
    
    public CodedBaseException(final Throwable e) {
        super(e);
        this.code = 500;
    }
    
    public CodedBaseException(final Throwable e, final int code) {
        super(e);
        this.code = 500;
        this.code = code;
    }
    
    public CodedBaseException(final Throwable e, final String msg) {
        super(msg, e);
        this.code = 500;
    }
    
    public CodedBaseException(final Throwable e, final int code, final String msg) {
        super(msg, e);
        this.code = 500;
        this.code = code;
    }
    
    @Override
    public int getCode() {
        return this.code;
    }
    
    @Override
    public void throwThis() throws Exception {
        throw this;
    }
}
