// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.dom4j.Document;
import org.springframework.core.convert.converter.Converter;

public class DocumentToString implements Converter<Document, String>
{
    public String convert(final Document source) {
        return source.asXML();
    }
}
