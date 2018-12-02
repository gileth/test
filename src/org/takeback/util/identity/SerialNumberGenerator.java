// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.identity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.NumberFormat;
import java.text.Format;
import java.text.FieldPosition;

public class SerialNumberGenerator
{
    private static final FieldPosition HELPER_POSITION;
    private static final Format dateFormat;
    private static final NumberFormat numberFormat;
    private static int seq;
    private static final int MAX = 9999;
    
    public static synchronized String generateSequenceNo() {
        return generateSequenceNo(null);
    }
    
    public static synchronized String generateSequenceNo(final String prefix) {
        final Calendar rightNow = Calendar.getInstance();
        final StringBuffer sb = (prefix == null) ? new StringBuffer() : new StringBuffer(prefix);
        SerialNumberGenerator.dateFormat.format(rightNow.getTime(), sb, SerialNumberGenerator.HELPER_POSITION);
        SerialNumberGenerator.numberFormat.format(SerialNumberGenerator.seq, sb, SerialNumberGenerator.HELPER_POSITION);
        if (SerialNumberGenerator.seq == 9999) {
            SerialNumberGenerator.seq = 0;
        }
        else {
            ++SerialNumberGenerator.seq;
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) throws InterruptedException {
        for (int i = 0; i < 10; ++i) {
            Thread.sleep(50L);
            System.out.println(generateSequenceNo());
            System.out.println(generateSequenceNo("GO"));
        }
    }
    
    static {
        HELPER_POSITION = new FieldPosition(0);
        dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        numberFormat = new DecimalFormat("0000");
        SerialNumberGenerator.seq = 0;
    }
}
