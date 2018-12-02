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

public class ProjectIdGenerator
{
    private static String ORGANIZATION_PREFIX;
    private static final FieldPosition HELPER_POSITION;
    private static final Format dateFormat;
    private static final NumberFormat numberFormat;
    private static int seq;
    private static final int MAX = 99;
    
    public static synchronized String generateSequenceNo() {
        final Calendar rightNow = Calendar.getInstance();
        final StringBuffer sb = new StringBuffer(ProjectIdGenerator.ORGANIZATION_PREFIX);
        ProjectIdGenerator.dateFormat.format(rightNow.getTime(), sb, ProjectIdGenerator.HELPER_POSITION);
        ProjectIdGenerator.numberFormat.format(ProjectIdGenerator.seq, sb, ProjectIdGenerator.HELPER_POSITION);
        if (ProjectIdGenerator.seq == 99) {
            ProjectIdGenerator.seq = 0;
        }
        else {
            ++ProjectIdGenerator.seq;
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) {
        for (int i = 0; i < 111; ++i) {
            System.out.println(generateSequenceNo());
        }
    }
    
    static {
        ProjectIdGenerator.ORGANIZATION_PREFIX = "SQS";
        HELPER_POSITION = new FieldPosition(0);
        dateFormat = new SimpleDateFormat("YYMMddHHmm");
        numberFormat = new DecimalFormat("00");
        ProjectIdGenerator.seq = 0;
    }
}
