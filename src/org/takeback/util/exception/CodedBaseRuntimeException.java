// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.exception;

public class CodedBaseRuntimeException extends RuntimeException implements CodedBase
{
    private static final long serialVersionUID = -466214696181211521L;
    protected int code;
    
    public CodedBaseRuntimeException() {
        this.code = 500;
    }
    
    public CodedBaseRuntimeException(final int code) {
        this.code = 500;
        this.code = code;
    }
    
    public CodedBaseRuntimeException(final int code, final Throwable t) {
        super(t);
        this.code = 500;
        this.code = code;
    }
    
    public CodedBaseRuntimeException(final int code, final String msg) {
        super(msg);
        this.code = 500;
        this.code = code;
    }
    
    public CodedBaseRuntimeException(final int code, final String msg, final Throwable t) {
        super(msg, t);
        this.code = 500;
        this.code = code;
    }
    
    public CodedBaseRuntimeException(final Throwable t) {
        super(t);
        this.code = 500;
    }
    
    public CodedBaseRuntimeException(final String msg) {
        super(msg);
        this.code = 500;
    }
    
    public CodedBaseRuntimeException(final String msg, final Throwable t) {
        super(msg, t);
        this.code = 500;
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
