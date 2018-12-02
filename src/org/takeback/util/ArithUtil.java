// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.math.BigDecimal;

public class ArithUtil
{
    private static final int DEF_DIV_SCALE = 10;
    
    public static double add(final double v1, final double v2) {
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
    
    public static double adds(final double... params) {
        BigDecimal b1 = new BigDecimal(Double.toString(params[0]));
        for (int i = 1; i < params.length; ++i) {
            b1 = b1.add(new BigDecimal(Double.toString(params[i])));
        }
        return b1.doubleValue();
    }
    
    public static double sub(final double v1, final double v2) {
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
    
    public static double subs(final double... params) {
        BigDecimal b1 = new BigDecimal(Double.toString(params[0]));
        for (int i = 1; i < params.length; ++i) {
            b1 = b1.subtract(new BigDecimal(Double.toString(params[i])));
        }
        return b1.doubleValue();
    }
    
    public static double mul(final double v1, final double v2) {
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
    
    public static double div(final double v1, final double v2) {
        return div(v1, v2, 10);
    }
    
    public static double div(final double v1, final double v2, final int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        final BigDecimal b1 = new BigDecimal(Double.toString(v1));
        final BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, 4).doubleValue();
    }
    
    public static double round(final double v, final int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        final BigDecimal b = new BigDecimal(Double.toString(v));
        final BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, 4).doubleValue();
    }
}
