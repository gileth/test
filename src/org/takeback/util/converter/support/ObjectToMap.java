// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.takeback.util.BeanUtils;
import java.util.HashMap;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

public class ObjectToMap implements GenericConverter
{
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (targetType.isMap()) {
            try {
                return BeanUtils.map(source, HashMap.class);
            }
            catch (Exception e) {
                throw new IllegalStateException("falied to convert map to bean", e);
            }
        }
        throw new IllegalStateException("source object must be a map");
    }
    
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        final Set<GenericConverter.ConvertiblePair> set = new HashSet<GenericConverter.ConvertiblePair>();
        set.add(new GenericConverter.ConvertiblePair((Class)Object.class, (Class)Map.class));
        return set;
    }
}
