// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.takeback.util.NetUtils;
import java.net.InetSocketAddress;
import org.springframework.core.convert.converter.Converter;

public class StringToInetSocketAddress implements Converter<String, InetSocketAddress>
{
    public InetSocketAddress convert(final String source) {
        return NetUtils.toAddress(source);
    }
}
