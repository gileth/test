// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.io;

import java.io.IOException;
import java.io.InputStream;

public class UnsafeByteArrayInputStream extends InputStream
{
    protected byte[] mData;
    protected int mPosition;
    protected int mLimit;
    protected int mMark;
    
    public UnsafeByteArrayInputStream(final byte[] buf) {
        this(buf, 0, buf.length);
    }
    
    public UnsafeByteArrayInputStream(final byte[] buf, final int offset) {
        this(buf, offset, buf.length - offset);
    }
    
    public UnsafeByteArrayInputStream(final byte[] buf, final int offset, final int length) {
        this.mMark = 0;
        this.mData = buf;
        this.mMark = offset;
        this.mPosition = offset;
        this.mLimit = Math.min(offset + length, buf.length);
    }
    
    @Override
    public int read() {
        return (this.mPosition < this.mLimit) ? (this.mData[this.mPosition++] & 0xFF) : -1;
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (this.mPosition >= this.mLimit) {
            return -1;
        }
        if (this.mPosition + len > this.mLimit) {
            len = this.mLimit - this.mPosition;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(this.mData, this.mPosition, b, off, len);
        this.mPosition += len;
        return len;
    }
    
    @Override
    public long skip(long len) {
        if (this.mPosition + len > this.mLimit) {
            len = this.mLimit - this.mPosition;
        }
        if (len <= 0L) {
            return 0L;
        }
        this.mPosition += (int)len;
        return len;
    }
    
    @Override
    public int available() {
        return this.mLimit - this.mPosition;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public void mark(final int readAheadLimit) {
        this.mMark = this.mPosition;
    }
    
    @Override
    public void reset() {
        this.mPosition = this.mMark;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    public int position() {
        return this.mPosition;
    }
    
    public void position(final int newPosition) {
        this.mPosition = newPosition;
    }
    
    public int size() {
        return (this.mData == null) ? 0 : this.mData.length;
    }
}
