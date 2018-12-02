// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import org.takeback.util.converter.ConversionUtils;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class ConvertUtil
{
    public static String toFixed(final double f, final int digists) {
        return String.format("%." + digists + "f", f);
    }
    
    public static String toMoney(final double f) {
        final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.CHINA);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        return formatter.format(f);
    }
    
    public static String dateToString(final Object source) {
        return ConversionUtils.convert(source, String.class);
    }
}
