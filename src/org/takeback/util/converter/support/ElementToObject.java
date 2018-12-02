// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import org.takeback.util.BeanUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

public class ElementToObject implements GenericConverter
{
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (Element.class.isInstance(source)) {
            try {
                final Element el = (Element)source;
                final Object dest = targetType.getType().newInstance();
                final List<Attribute> attrs = (List<Attribute>)el.attributes();
                for (final Attribute attr : attrs) {
                    try {
                        BeanUtils.setProperty(dest, attr.getName(), attr.getValue());
                    }
                    catch (Exception e2) {
                        try {
                            BeanUtils.setPropertyInMap(dest, attr.getName(), attr.getValue());
                        }
                        catch (Exception ex) {}
                    }
                }
                return dest;
            }
            catch (Exception e) {
                throw new IllegalStateException("failed to convert element to bean", e);
            }
        }
        throw new IllegalStateException("source object must be a Element");
    }
    
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        final Set<GenericConverter.ConvertiblePair> set = new HashSet<GenericConverter.ConvertiblePair>();
        set.add(new GenericConverter.ConvertiblePair((Class)Element.class, (Class)Object.class));
        return set;
    }
}
