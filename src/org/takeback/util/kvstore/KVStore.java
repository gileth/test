// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.kvstore;

import java.util.List;
import java.util.Map;

public interface KVStore
{
    void start();
    
    void close();
    
    void put(final String p0, final String p1);
    
    void put(final byte[] p0, final byte[] p1);
    
    String get(final String p0);
    
    byte[] get(final byte[] p0);
    
    Map.Entry<byte[], byte[]> seekFirst();
    
    void remove(final String p0);
    
    void remove(final byte[] p0);
    
    void removes(final List<String> p0);
    
    void removesByByte(final List<byte[]> p0);
    
    void puts(final Map<String, String> p0);
    
    void putsByByte(final Map<byte[], byte[]> p0);
    
    Map<String, String> gets();
    
    Map<byte[], byte[]> getsByByte();
    
    boolean containsKey(final String p0);
    
    boolean containsKey(final byte[] p0);
}
