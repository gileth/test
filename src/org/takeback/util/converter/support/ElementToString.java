// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.dom4j.Element;
import org.springframework.core.convert.converter.Converter;

public class ElementToString implements Converter<Element, String>
{
    public String convert(final Element source) {
        return source.asXML();
    }
}
