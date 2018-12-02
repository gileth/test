// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.schema;

import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.dictionary.Dictionary;
import org.takeback.util.StringValueParser;
import org.takeback.util.converter.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;

public class SchemaItem implements Serializable
{
    private static final long serialVersionUID = 4557229734515290036L;
    private String id;
    private String name;
    private boolean pkey;
    private String strategy;
    private String type;
    private DictionaryIndicator dic;
    private Object defaultValue;
    private Integer length;
    private Integer precision;
    private Object maxValue;
    private Object minValue;
    private boolean allowBlank;
    private List<?> exp;
    private int displayMode;
    private boolean hidden;
    private boolean update;
    private HashMap<String, Object> properties;
    
    public SchemaItem() {
        this.strategy = "identity";
        this.allowBlank = true;
        this.displayMode = 3;
        this.update = true;
    }
    
    public SchemaItem(final String id) {
        this.strategy = "identity";
        this.allowBlank = true;
        this.displayMode = 3;
        this.update = true;
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Boolean isCodedValue() {
        return this.dic != null;
    }
    
    public Boolean isEvalValue() {
        return this.exp != null;
    }
    
    public boolean isAllowBlank() {
        return this.allowBlank;
    }
    
    public void setAllowBlank(final boolean allowBlank) {
        this.allowBlank = allowBlank;
    }
    
    public String getType() {
        if (StringUtils.isEmpty((CharSequence)this.type)) {
            return "string";
        }
        return this.type;
    }
    
    private Class<?> getTypeClass() {
        return DataTypes.getTypeClass(this.getType());
    }
    
    public void setType(final String type) {
        if (!DataTypes.isSupportType(type)) {
            throw new IllegalArgumentException("type[" + type + "] is unsupported");
        }
        this.type = StringUtils.uncapitalize(type);
    }
    
    public void setExp(final List<Object> exp) {
        this.exp = exp;
    }
    
    public Integer getDisplayMode() {
        return this.displayMode;
    }
    
    public void setDisplayMode(final Integer displayMode) {
        this.displayMode = displayMode;
    }
    
    public void setDisplay(final Integer displayMode) {
        this.setDisplayMode(displayMode);
    }
    
    public Integer getDisplay() {
        return this.getDisplayMode();
    }
    
    public DictionaryIndicator getDic() {
        return this.dic;
    }
    
    public void setDic(final DictionaryIndicator dic) {
        this.dic = dic;
    }
    
    public boolean isPkey() {
        return this.pkey;
    }
    
    public void setPkey(final boolean pkey) {
        this.pkey = pkey;
    }
    
    public String getStrategy() {
        return this.strategy;
    }
    
    public void setStrategy(final String strategy) {
        this.strategy = strategy;
    }
    
    public Integer getLength() {
        return this.length;
    }
    
    public void setLength(final Integer length) {
        this.length = length;
    }
    
    public Integer getPrecision() {
        return this.precision;
    }
    
    public void setPrecision(final Integer precision) {
        this.precision = precision;
    }
    
    public Object getDefaultValue() {
        if (this.defaultValue == null) {
            return this.defaultValue;
        }
        if (this.isCodedValue()) {
            final HashMap<String, String> obj = new HashMap<String, String>();
            final String key = ConversionUtils.convert(this.parseConfigValue(this.defaultValue), String.class);
            final String text = this.toDisplayValue(key);
            obj.put("key", key);
            obj.put("text", text);
            return obj;
        }
        return this.parseConfigValue(this.defaultValue);
    }
    
    public void setDefaultValue(final Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public Object getMaxValue() {
        return this.parseConfigValue(this.maxValue);
    }
    
    public void setMaxValue(final Object maxValue) {
        this.maxValue = maxValue;
    }
    
    public Object getMinValue() {
        return this.parseConfigValue(this.minValue);
    }
    
    public void setMinValue(final Object minValue) {
        this.minValue = minValue;
    }
    
    private Object parseConfigValue(final Object v) {
        Object val;
        if (v instanceof String) {
            val = StringValueParser.parse((String)v, this.getTypeClass());
        }
        else {
            val = ConversionUtils.convert(v, this.getTypeClass());
        }
        return val;
    }
    
    public String toDisplayValue(final Object v) {
        final String key = ConversionUtils.convert(v, String.class);
        if (this.isCodedValue() && !StringUtils.isEmpty((CharSequence)key)) {
            final Dictionary d = DictionaryController.instance().get(this.dic.getId());
            String text = "";
            if (key.indexOf(",") == -1) {
                text = d.getText(key);
            }
            else {
                final String[] keys = key.split(",");
                final StringBuffer sb = new StringBuffer();
                for (final String s : keys) {
                    sb.append(",").append(d.getText(s));
                }
                text = sb.substring(1);
            }
            return text;
        }
        return key;
    }
    
    public Object toPersistValue(final Object source) {
        return DataTypes.toTypeValue(this.getType(), source);
    }
    
    public Object eval() {
        if (!this.isEvalValue()) {
            return null;
        }
        return this.toPersistValue(ExpressionProcessor.instance().run(this.exp));
    }
    
    public Object eval(final String lang) {
        if (this.exp != null) {
            return this.toPersistValue(ExpressionProcessor.instance(lang).run(this.exp));
        }
        return null;
    }
    
    public void setProperty(final String nm, final Object v) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        this.properties.put(nm, v);
    }
    
    public Object getProperty(final String nm) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(nm);
    }
    
    public HashMap<String, Object> getProperties() {
        if (this.properties != null && this.properties.isEmpty()) {
            return null;
        }
        return this.properties;
    }
    
    public boolean hasProperty(final String nm) {
        return this.properties != null && this.properties.containsKey(nm);
    }
    
    public void setUpdate(final boolean canUpdate) {
        this.update = canUpdate;
    }
    
    public boolean isUpdate() {
        return !this.pkey && this.update;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }
}
