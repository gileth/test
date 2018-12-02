// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.accredit;

import java.io.Serializable;

public class Mode implements Serializable
{
    private static final long serialVersionUID = 8406591497388596746L;
    public static final int ACCESSIBLE_FLAG = 1;
    public static final int CREATEABLE_FLAG = 2;
    public static final int UPDATEABLE_FLAG = 4;
    public static final int REMOVEABLE_FLAG = 8;
    public static final Mode FullAccessMode;
    public static final Mode NoneAccessMode;
    private int value;
    private boolean accessible;
    private boolean creatable;
    private boolean updatable;
    private boolean removable;
    
    public static Mode parseFromInt(final int v) {
        if (v == 0) {
            return Mode.NoneAccessMode;
        }
        if ((v & 0xF) == 0xF) {
            return Mode.FullAccessMode;
        }
        final Mode mode = new Mode();
        mode.setValue(v);
        return mode;
    }
    
    public Mode() {
    }
    
    public Mode(final int v) {
        this.setValue(v);
    }
    
    public Mode(final String v) {
        this.setValue(Integer.valueOf(v, 2));
    }
    
    public Integer getValue() {
        return this.value;
    }
    
    public void setValue(final Integer v) {
        this.value = v;
        this.accessible = ((this.value & 0x1) == 0x1);
        this.creatable = ((this.value & 0x2) == 0x2);
        this.updatable = ((this.value & 0x4) == 0x4);
        this.removable = ((this.value & 0x8) == 0x8);
    }
    
    public boolean isAccessible() {
        return this.accessible;
    }
    
    public boolean isCreatable() {
        return this.creatable;
    }
    
    public boolean isUpdatable() {
        return this.updatable;
    }
    
    public boolean isRemovable() {
        return this.removable;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o.getClass().equals(this.getClass()) && ((Mode)o).getValue().equals(this.value);
    }
    
    public static void main(final String[] args) {
        System.out.println(Mode.FullAccessMode.getValue());
    }
    
    static {
        FullAccessMode = new Mode(15);
        NoneAccessMode = new Mode(0);
    }
}
