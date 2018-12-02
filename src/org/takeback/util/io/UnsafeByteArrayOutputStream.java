// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.io;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.OutputStream;

public class UnsafeByteArrayOutputStream extends OutputStream
{
    protected byte[] mBuffer;
    protected int mCount;
    
    public UnsafeByteArrayOutputStream() {
        this(32);
    }
    
    public UnsafeByteArrayOutputStream(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        this.mBuffer = new byte[size];
    }
    
    @Override
    public void write(final int b) {
        final int newcount = this.mCount + 1;
        if (newcount > this.mBuffer.length) {
            this.mBuffer = Bytes.copyOf(this.mBuffer, Math.max(this.mBuffer.length << 1, newcount));
        }
        this.mBuffer[this.mCount] = (byte)b;
        this.mCount = newcount;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        final int newcount = this.mCount + len;
        if (newcount > this.mBuffer.length) {
            this.mBuffer = Bytes.copyOf(this.mBuffer, Math.max(this.mBuffer.length << 1, newcount));
        }
        System.arraycopy(b, off, this.mBuffer, this.mCount, len);
        this.mCount = newcount;
    }
    
    public int size() {
        return this.mCount;
    }
    
    public void reset() {
        this.mCount = 0;
    }
    
    public byte[] toByteArray() {
        return Bytes.copyOf(this.mBuffer, this.mCount);
    }
    
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(this.mBuffer, 0, this.mCount);
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        out.write(this.mBuffer, 0, this.mCount);
    }
    
    @Override
    public String toString() {
        return new String(this.mBuffer, 0, this.mCount);
    }
    
    public String toString(final String charset) throws UnsupportedEncodingException {
        return new String(this.mBuffer, 0, this.mCount, charset);
    }
    
    @Override
    public void close() throws IOException {
    }
}
