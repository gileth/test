// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.converter.support;

import org.joda.time.LocalDate;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.takeback.util.converter.ConversionUtils;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class StringToDate implements Converter<String, Date>
{
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME1_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATETIME2_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATETIME3_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    public static void main(final String[] args) {
        String s = "2015-08-02 11:20:33";
        System.out.println(ConversionUtils.convert(s, Date.class));
        s = "2015-08-02T11:20:33";
        System.out.println(ConversionUtils.convert(s, Date.class));
    }
    
    public static Date toDate(final String s) {
        return DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC().parseLocalDate(s).toDate();
    }
    
    public static Date toDatetime(final String s) {
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC().parseDateTime(s).toDate();
    }
    
    public Date convert(final String source) {
        if (StringUtils.isEmpty((CharSequence)source)) {
            return null;
        }
        if (StringUtils.contains((CharSequence)source, (CharSequence)"T")) {
            if (!StringUtils.contains((CharSequence)source, (CharSequence)"Z")) {
                return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC().parseDateTime(source).toDate();
            }
            if (source.length() == 20) {
                return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC().parseDateTime(source).toDate();
            }
            return ISODateTimeFormat.dateTime().parseDateTime(source).toDate();
        }
        else {
            if (StringUtils.contains((CharSequence)source, (CharSequence)":")) {
                return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(source).toDate();
            }
            if (StringUtils.contains((CharSequence)source, (CharSequence)"-")) {
                return DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(source).toDate();
            }
            if (StringUtils.equals((CharSequence)source.toLowerCase(), (CharSequence)"now")) {
                return new Date();
            }
            if (StringUtils.equals((CharSequence)source.toLowerCase(), (CharSequence)"today")) {
                return new DateTime().withTimeAtStartOfDay().toDate();
            }
            if (StringUtils.equals((CharSequence)source.toLowerCase(), (CharSequence)"yesterday")) {
                return new LocalDate().minusDays(1).toDate();
            }
            if (StringUtils.equals((CharSequence)source.toLowerCase(), (CharSequence)"tomorrow")) {
                return new LocalDate().plusDays(1).toDate();
            }
            throw new IllegalArgumentException("Invalid date string value '" + source + "'");
        }
    }
}
