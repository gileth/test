// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import org.takeback.util.exception.CodedBaseException;

public class GameException extends CodedBaseException
{
    public GameException(final String code) {
        super(code);
    }
    
    public GameException(final int code, final String msg) {
        super(code, msg);
    }
    
    public GameException(final int code, final Throwable e) {
        super(e, code);
    }
    
    public GameException(final int code, final String msg, final Throwable e) {
        super(e, code, msg);
    }
}
