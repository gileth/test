// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil
{
    public static final String SHORT = "yyy-MM-dd";
    public static final String LONG = "yyy-MM-dd HH:mm:ss";
    public static final String VERY_SHORT = "MM-dd HH:mm";
    
    public static String toShortTime(final Date date) {
        final SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(date);
    }
    
    public static Date toDate(final String dateStr) {
        final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Date getStartOfToday() {
        final Date d = new Date();
        final Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(14, 0);
        c.set(13, 0);
        c.set(12, 0);
        c.set(11, 0);
        return c.getTime();
    }
    
    public static Date getEndOfToday() {
        final Date d = new Date();
        final Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(13, 59);
        c.set(12, 59);
        c.set(11, 23);
        return c.getTime();
    }
    
    public static Date getStartOfTheDay(final String dateStr) {
        final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
        try {
            final Date d = format.parse(dateStr);
            return d;
        }
        catch (Exception e) {
            throw new CodedBaseRuntimeException("\u9519\u8bef\u7684\u65f6\u95f4\u683c\u5f0f!");
        }
    }
    
    public static Date getStartOfTheDay(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        return c.getTime();
    }
    
    public static Date getEndOfTheDay(final String dateStr) {
        final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
        try {
            final Date d = format.parse(dateStr);
            final Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(5, 1);
            c.add(14, -1);
            return c.getTime();
        }
        catch (Exception e) {
            throw new CodedBaseRuntimeException("\u9519\u8bef\u7684\u65f6\u95f4\u683c\u5f0f!");
        }
    }
    
    public static Date getEndOfTheDay(final Date date) {
        try {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(5, 1);
            c.add(14, -1);
            return c.getTime();
        }
        catch (Exception e) {
            throw new CodedBaseRuntimeException("\u9519\u8bef\u7684\u65f6\u95f4\u683c\u5f0f!");
        }
    }
    
    public static void main(final String... ags) {
        final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        System.out.println(format.format(getStartOfToday()));
        System.out.println(format.format(getEndOfToday()));
    }
}
