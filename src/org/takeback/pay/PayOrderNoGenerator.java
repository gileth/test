// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.pay;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class PayOrderNoGenerator
{
    private static long orderNum;
    private static String date;
    private static final byte[] sync;
    
    public static String generator(final boolean useUUID) {
        if (useUUID) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        synchronized (PayOrderNoGenerator.sync) {
            final String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            if (PayOrderNoGenerator.date == null || !PayOrderNoGenerator.date.equals(str)) {
                PayOrderNoGenerator.date = str;
                PayOrderNoGenerator.orderNum = 0L;
            }
            ++PayOrderNoGenerator.orderNum;
            long orderNo = Long.parseLong(PayOrderNoGenerator.date) * 10000L;
            orderNo += PayOrderNoGenerator.orderNum;
            return orderNo + "";
        }
    }
    
    static {
        PayOrderNoGenerator.orderNum = 0L;
        sync = new byte[0];
    }
}
