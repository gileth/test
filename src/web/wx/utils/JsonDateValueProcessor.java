// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

class JsonDateValueProcessor implements JsonValueProcessor
{
    private String format;
    
    public JsonDateValueProcessor() {
        this.format = "yyyy-MM-dd HH:mm:ss";
    }
    
    public JsonDateValueProcessor(final String format) {
        this.format = "yyyy-MM-dd HH:mm:ss";
        this.format = format;
    }
    
    public Object processArrayValue(final Object value, final JsonConfig jsonConfig) {
        return this.process(value, jsonConfig);
    }
    
    public Object processObjectValue(final String key, final Object value, final JsonConfig jsonConfig) {
        return this.process(value, jsonConfig);
    }
    
    private Object process(final Object value, final JsonConfig jsonConfig) {
        if (value instanceof Date) {
            final String str = new SimpleDateFormat(this.format).format((Date)value);
            return str;
        }
        return (value == null) ? null : value.toString();
    }
    
    public String getFormat() {
        return this.format;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
}
