// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.util.regex.Pattern;
import java.util.Map;

public class HqlHelper
{
    public static String getWhere(final String value, final String key, final Map<String, Object> map) {
        final StringBuilder where = new StringBuilder();
        final String[] t = value.split(",");
        if (t.length != 3) {
            return "506,Illegal arguments !";
        }
        final Pattern ipattern = Pattern.compile("^\\d+$|-\\d+$");
        final Pattern fpattern = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");
        if (!fpattern.matcher(t[0]).matches() && !ipattern.matcher(t[0]).matches()) {
            return "507,Illegal arguments !";
        }
        final String s = t[2];
        switch (s) {
            case "gt": {
                where.append(" ").append(key).append(" > :").append(key);
                map.put(key, Float.parseFloat(t[0]));
                break;
            }
            case "lt": {
                where.append(" ").append(key).append(" < :").append(key);
                map.put(key, Float.parseFloat(t[0]));
                break;
            }
            default: {
                if (!fpattern.matcher(t[1]).matches() && !ipattern.matcher(t[1]).matches()) {
                    return "508,Illegal arguments !";
                }
                if (Float.parseFloat(t[0]) > Float.parseFloat(t[1])) {
                    return "509,Illegal arguments !";
                }
                where.append(" ").append(key).append(" >= ").append(t[0]).append(" and ");
                where.append(key).append(" <= ").append(t[1]);
                break;
            }
        }
        return where.toString();
    }
}
