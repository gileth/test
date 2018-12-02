// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.dom4j.Element;
import org.takeback.util.converter.ConversionUtils;
import org.dom4j.DocumentHelper;
import org.takeback.util.BeanUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

public class ObjectToElement implements GenericConverter
{
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        try {
            final Map<String, Object> map = BeanUtils.map(source, (Class<Map<String, Object>>)HashMap.class);
            final Element beanEl = DocumentHelper.createElement(source.getClass().getSimpleName());
            final Set<String> fields = map.keySet();
            for (final String field : fields) {
                final Element fieldEl = DocumentHelper.createElement(field);
                final Object val = map.get(field);
                if (val != null) {
                    fieldEl.setText((String)ConversionUtils.convert(val, String.class));
                }
                beanEl.add(fieldEl);
            }
            return beanEl;
        }
        catch (Exception e) {
            throw new IllegalStateException("falied to convert bean to element", e);
        }
    }
    
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        final Set<GenericConverter.ConvertiblePair> set = new HashSet<GenericConverter.ConvertiblePair>();
        set.add(new GenericConverter.ConvertiblePair((Class)Object.class, (Class)Element.class));
        return set;
    }
}
