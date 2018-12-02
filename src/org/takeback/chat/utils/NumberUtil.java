// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import java.text.DecimalFormat;
import java.math.BigDecimal;

public class NumberUtil
{
    public static BigDecimal getDecimalPart(final BigDecimal d) {
        return d.subtract(d.setScale(0, 1));
    }
    
    public static Integer getDecimalPartSum(final BigDecimal d) {
        final DecimalFormat df = new DecimalFormat("#.00");
        final String str = df.format(d);
        final String str2 = new StringBuffer("").append(str.charAt(str.length() - 1)).toString();
        final String str3 = new StringBuffer("").append(str.charAt(str.length() - 2)).toString();
        final Integer i1 = Integer.valueOf(str2);
        final Integer i2 = Integer.valueOf(str3);
        return i1 + i2;
    }
    
    public static Integer getDecimalPartSum4G22(final BigDecimal d) {
        final DecimalFormat df = new DecimalFormat("0.00");
        final String str = df.format(d);
        final String str2 = new StringBuffer("").append(str.charAt(str.length() - 1)).toString();
        final String str3 = new StringBuffer("").append(str.charAt(str.length() - 2)).toString();
        final String bzstr = str.replace(".", "");
        String mod = "";
        for (int i = 0; i < bzstr.length(); ++i) {
            mod += "1";
        }
        if (Integer.valueOf(bzstr) % Integer.valueOf(mod) == 0) {
            return 13;
        }
        if (str2.equals(str3) && "0".equals(str2)) {
            return 12;
        }
        if (str2.equals(str3)) {
            return 11;
        }
        final Integer i2 = Integer.valueOf(str2);
        final Integer i3 = Integer.valueOf(str3);
        int point = i2 + i3;
        if (point > 10) {
            point %= 10;
        }
        return point;
    }
    
    public static Integer getTailPoint(final BigDecimal d) {
        final DecimalFormat df = new DecimalFormat("0.00");
        final String str = df.format(d);
        final String str2 = new StringBuffer("").append(str.charAt(str.length() - 1)).toString();
        return Integer.valueOf(str2);
    }
    
    public static Integer getPoint(final BigDecimal d) {
        Integer n = getDecimalPartSum(d);
        n %= 10;
        if (n.equals(0)) {
            return 10;
        }
        return n;
    }
    
    public static BigDecimal round(final BigDecimal d) {
        return d.setScale(2, 4);
    }
    
    public static double round(final double d) {
        final BigDecimal b = new BigDecimal(d);
        return b.setScale(2, 4).doubleValue();
    }
    
    public static String format(final double d) {
        final BigDecimal b = new BigDecimal(d);
        return b.setScale(2, 4).toString();
    }
    
    public static String format(final BigDecimal d) {
        return d.setScale(2, 4).toString();
    }
    
    public static void main(final String... args) {
        System.out.println(getTailPoint(new BigDecimal(0.53)));
    }
}
