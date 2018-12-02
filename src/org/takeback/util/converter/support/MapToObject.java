// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import java.util.HashSet;
import java.util.Set;
import org.takeback.util.BeanUtils;
import java.util.Map;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

public class MapToObject implements GenericConverter
{
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (sourceType.isMap()) {
            try {
                final Object target = targetType.getType().newInstance();
                final Map<String, Object> map = (Map<String, Object>)source;
                BeanUtils.copy(map, target);
                return target;
            }
            catch (Exception e) {
                throw new IllegalStateException("falied to convert map to bean", e);
            }
        }
        throw new IllegalStateException("source object must be a map");
    }
    
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        final Set<GenericConverter.ConvertiblePair> set = new HashSet<GenericConverter.ConvertiblePair>();
        set.add(new GenericConverter.ConvertiblePair((Class)Map.class, (Class)Object.class));
        return set;
    }
}
