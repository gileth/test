// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Document;
import org.springframework.core.convert.converter.Converter;

public class StringToDocument implements Converter<String, Document>
{
    public Document convert(final String source) {
        try {
            return DocumentHelper.parseText(source);
        }
        catch (DocumentException e) {
            throw new IllegalArgumentException("Failed to parse xml " + source + ", cause: " + e.getMessage(), (Throwable)e);
        }
    }
}
