// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.params;

public class Param
{
    private Long id;
    private String paramname;
    private String paramvalue;
    private String paramalias;
    private String remark;
    
    public Param() {
    }
    
    public Param(final String name, final String value) {
        this.paramname = name;
        this.paramvalue = value;
    }
    
    public Param(final String name, final String value, final String description) {
        this.paramname = name;
        this.paramvalue = value;
        this.remark = description;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public String getParamname() {
        return this.paramname;
    }
    
    public void setParamname(final String paramname) {
        this.paramname = paramname;
    }
    
    public String getParamvalue() {
        return this.paramvalue;
    }
    
    public void setParamvalue(final String paramvalue) {
        this.paramvalue = paramvalue;
    }
    
    public String getParamalias() {
        return this.paramalias;
    }
    
    public void setParamalias(final String paramalias) {
        this.paramalias = paramalias;
    }
    
    public String getRemark() {
        return this.remark;
    }
    
    public void setRemark(final String remark) {
        this.remark = remark;
    }
}
