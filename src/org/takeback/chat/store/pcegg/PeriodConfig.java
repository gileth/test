// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.pcegg;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.LocalTime;

public class PeriodConfig
{
    private LocalTime beginTime;
    private LocalTime endTime;
    private int periodSeconds;
    private String dataSourceUrl;
    private String lastNo;
    
    public PeriodConfig(final String config) {
        String[] tmp = config.split("->");
        this.setPeriodSeconds(tmp[1]);
        this.dataSourceUrl = tmp[2];
        tmp = tmp[0].split("~");
        this.beginTime = this.parseTime(tmp[0]);
        this.endTime = this.parseTime(tmp[1]);
    }
    
    public boolean match(final LocalTime time) {
        final DateTime dateTime = time.toDateTimeToday();
        DateTime beginDateTime = this.beginTime.toDateTimeToday();
        DateTime endDateTime = this.endTime.toDateTimeToday();
        if (dateTime.isAfter((ReadableInstant)endDateTime) && endDateTime.isBefore((ReadableInstant)beginDateTime)) {
            endDateTime = endDateTime.plusDays(1);
        }
        else if (dateTime.isBefore((ReadableInstant)endDateTime) && endDateTime.isBefore((ReadableInstant)beginDateTime)) {
            beginDateTime = beginDateTime.plusDays(-1);
        }
        return dateTime.compareTo((ReadableInstant)beginDateTime) >= 0 && dateTime.compareTo((ReadableInstant)endDateTime) < 0;
    }
    
    public int getPeriodSeconds() {
        return this.periodSeconds;
    }
    
    public String getDataSourceUrl() {
        return this.dataSourceUrl;
    }
    
    private void setPeriodSeconds(final String periodConfig) {
        final char periodUnit = periodConfig.charAt(periodConfig.length() - 1);
        final String periodString = periodConfig.substring(0, periodConfig.length() - 1);
        switch (periodUnit) {
            case 'h': {
                this.periodSeconds = Integer.parseInt(periodString) * 60 * 60;
                break;
            }
            case 'm': {
                this.periodSeconds = Integer.parseInt(periodString) * 60;
                break;
            }
            case 's': {
                this.periodSeconds = Integer.parseInt(periodString);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid unit of period: " + periodUnit);
            }
        }
    }
    
    private LocalTime parseTime(final String timeConfig) {
        final String[] tmp = timeConfig.split(":");
        LocalTime time;
        if (tmp.length == 2) {
            time = new LocalTime(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), 0);
        }
        else {
            if (tmp.length != 3) {
                throw new IllegalArgumentException("Invalid time configuration: " + timeConfig);
            }
            time = new LocalTime(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]));
        }
        return time;
    }
    
    public String getLastNo() {
        return this.lastNo;
    }
    
    public void setLastNo(final String lastNo) {
        this.lastNo = lastNo;
    }
}
