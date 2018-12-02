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

public class OrderIdGenerator
{
    private static final FieldPosition HELPER_POSITION;
    private static final Format dateFormat;
    private static final NumberFormat numberFormat;
    private static int seq;
    private static final int MAX = 9999;
    
    public static synchronized String generateSequenceNo() {
        final Calendar rightNow = Calendar.getInstance();
        final StringBuffer sb = new StringBuffer();
        OrderIdGenerator.dateFormat.format(rightNow.getTime(), sb, OrderIdGenerator.HELPER_POSITION);
        OrderIdGenerator.numberFormat.format(OrderIdGenerator.seq, sb, OrderIdGenerator.HELPER_POSITION);
        if (OrderIdGenerator.seq == 9999) {
            OrderIdGenerator.seq = 0;
        }
        else {
            ++OrderIdGenerator.seq;
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) {
        for (int i = 0; i < 10; ++i) {
            System.out.println(generateSequenceNo());
        }
    }
    
    static {
        HELPER_POSITION = new FieldPosition(0);
        dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        numberFormat = new DecimalFormat("0000");
        OrderIdGenerator.seq = 0;
    }
}
