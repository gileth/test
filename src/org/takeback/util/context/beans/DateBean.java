// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.context.beans;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.takeback.util.converter.ConversionUtils;
import java.util.Date;

public class DateBean
{
    public Date parse(final String source) {
        return ConversionUtils.convert(source, Date.class);
    }
    
    public Date getToday() {
        final LocalDate dt = new LocalDate();
        return dt.toDate();
    }
    
    public Date getDatetime() {
        return new Date();
    }
    
    public Date getNow() {
        return this.getDatetime();
    }
    
    public Date getTomorrow() {
        final LocalDate dt = new LocalDate();
        return dt.plusDays(1).toDate();
    }
    
    public Date getDatetimeOfNextDay() {
        final DateTime dt = new DateTime();
        return dt.plusDays(1).toDate();
    }
    
    public Date getDateOfNextMonth() {
        final LocalDate dt = new LocalDate();
        return dt.plusMonths(1).toDate();
    }
    
    public Date getDatetimeOfNextMonth() {
        final DateTime dt = new DateTime();
        return dt.plusMonths(1).toDate();
    }
    
    public Date getDateOfNextYear() {
        final LocalDate dt = new LocalDate();
        return dt.plusYears(1).toDate();
    }
    
    public Date getDatetimeOfNextYear() {
        final DateTime dt = new DateTime();
        return dt.plusYears(1).toDate();
    }
    
    public Date getYesterday() {
        final LocalDate dt = new LocalDate();
        return dt.minusDays(1).toDate();
    }
    
    public Date getDatetimeOfLastDay() {
        final DateTime dt = new DateTime();
        return dt.minusDays(1).toDate();
    }
    
    public Date getDateOfLastMonth() {
        final LocalDate dt = new LocalDate();
        return dt.minusMonths(1).toDate();
    }
    
    public Date getDatetimeOfLastMonth() {
        final DateTime dt = new DateTime();
        return dt.minusMonths(1).toDate();
    }
    
    public Date getDateOfLastYear() {
        final LocalDate dt = new LocalDate();
        return dt.minusYears(1).toDate();
    }
    
    public Date getDatetimeOfLastYear() {
        final DateTime dt = new DateTime();
        return dt.minusYears(1).toDate();
    }
    
    public Date getDateOfLastWeek() {
        final LocalDate dt = new LocalDate();
        return dt.minusWeeks(1).toDate();
    }
    
    public Date getDatetimeOfLastWeek() {
        final DateTime dt = new DateTime();
        return dt.minusWeeks(1).toDate();
    }
    
    public int getYear() {
        final LocalDate dt = new LocalDate();
        return dt.getYear();
    }
    
    public int getMonth() {
        final LocalDate dt = new LocalDate();
        return dt.getMonthOfYear();
    }
    
    public int getDay() {
        final LocalDate dt = new LocalDate();
        return dt.getDayOfMonth();
    }
    
    public int getWeekDay() {
        final LocalDate dt = new LocalDate();
        return dt.getDayOfWeek();
    }
}
