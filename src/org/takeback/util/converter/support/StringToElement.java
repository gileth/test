// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.core.convert.converter.Converter;

public class StringToElement implements Converter<String, Element>
{
    public Element convert(final String source) {
        try {
            return DocumentHelper.parseText(source).getRootElement();
        }
        catch (DocumentException e) {
            throw new IllegalArgumentException("Failed to parse xml " + source + ", cause: " + e.getMessage(), (Throwable)e);
        }
    }
}
