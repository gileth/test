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

public class BalanceIdGenerator
{
    private static String ORGANIZATION_PREFIX;
    private static final FieldPosition HELPER_POSITION;
    private static final Format dateFormat;
    private static final NumberFormat numberFormat;
    private static int seq;
    private static final int MAX = 9999;
    
    public static synchronized String generateSequenceNo() {
        final Calendar rightNow = Calendar.getInstance();
        final StringBuffer sb = new StringBuffer(BalanceIdGenerator.ORGANIZATION_PREFIX);
        BalanceIdGenerator.dateFormat.format(rightNow.getTime(), sb, BalanceIdGenerator.HELPER_POSITION);
        BalanceIdGenerator.numberFormat.format(BalanceIdGenerator.seq, sb, BalanceIdGenerator.HELPER_POSITION);
        if (BalanceIdGenerator.seq == 9999) {
            BalanceIdGenerator.seq = 0;
        }
        else {
            ++BalanceIdGenerator.seq;
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) throws InterruptedException {
        for (int i = 0; i < 10; ++i) {
            Thread.sleep(50L);
            System.out.println(generateSequenceNo());
        }
    }
    
    static {
        BalanceIdGenerator.ORGANIZATION_PREFIX = "CZ";
        HELPER_POSITION = new FieldPosition(0);
        dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        numberFormat = new DecimalFormat("0000");
        BalanceIdGenerator.seq = 0;
    }
}
