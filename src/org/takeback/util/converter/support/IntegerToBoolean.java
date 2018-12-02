// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.springframework.core.convert.converter.Converter;

public class IntegerToBoolean implements Converter<Integer, Boolean>
{
    public Boolean convert(final Integer source) {
        if (source == 0) {
            return false;
        }
        return true;
    }
}
