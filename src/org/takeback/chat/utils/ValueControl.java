// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.util.Vector;
import java.util.Map;

public class ValueControl
{
    private static Map<String, Map<Integer, Vector<BigDecimal>>> store;
    private static final int MAX_SIZE = 50;
    
    public static BigDecimal getValue(final String roomId, final Integer uid) {
        final Map<Integer, Vector<BigDecimal>> room = ValueControl.store.get(roomId);
        if (room == null || room.size() == 0) {
            return null;
        }
        final Vector<BigDecimal> userValues = room.get(uid);
        if (userValues == null || userValues.size() == 0) {
            return null;
        }
        final BigDecimal bd = userValues.get(0);
        userValues.remove(0);
        return bd;
    }
    
    public static void clean(final String roomId, final Integer uid) {
        final Map<Integer, Vector<BigDecimal>> room = ValueControl.store.get(roomId);
        if (room == null || room.size() == 0) {
            return;
        }
        final Vector<BigDecimal> userValues = room.get(uid);
        if (userValues == null || userValues.size() == 0) {
            return;
        }
        room.remove(uid);
    }
    
    public static void setValue(final String roomId, final Integer uid, final BigDecimal value) {
        Map<Integer, Vector<BigDecimal>> room = ValueControl.store.get(roomId);
        if (room == null) {
            room = new HashMap<Integer, Vector<BigDecimal>>();
            ValueControl.store.put(roomId, room);
        }
        Vector<BigDecimal> userQueue = room.get(uid);
        if (userQueue == null) {
            userQueue = new Vector<BigDecimal>();
            room.put(uid, userQueue);
        }
        userQueue.add(value);
    }
    
    public static Map<String, Map<Integer, Vector<BigDecimal>>> getStore() {
        return ValueControl.store;
    }
    
    public static Map<Integer, Vector<BigDecimal>> getByRoomId(final String roomId) {
        return ValueControl.store.get(roomId);
    }
    
    public static List<Map<String, Object>> query() {
        final List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        for (final String roomId : ValueControl.store.keySet()) {
            final Map<Integer, Vector<BigDecimal>> room = ValueControl.store.get(roomId);
            for (final Integer uid : room.keySet()) {
                final Vector v = room.get(uid);
                for (int i = 0; i < v.size(); ++i) {
                    final Map<String, Object> rec = new HashMap<String, Object>();
                    rec.put("roomId", roomId);
                    rec.put("uid", uid);
                    rec.put("value", v.get(i));
                    res.add(rec);
                }
            }
        }
        return res;
    }
    
    public static void main(final String... args) {
        setValue("room1", 0, new BigDecimal(0.55));
        setValue("room1", 1, new BigDecimal(0.22));
        setValue("room1", 1, new BigDecimal(0.15));
        setValue("room1", 2, new BigDecimal(0.35));
        setValue("room1", 3, new BigDecimal(0.14));
        System.out.println(getValue("room1", 1));
        System.out.println(query());
    }
    
    static {
        ValueControl.store = new HashMap<String, Map<Integer, Vector<BigDecimal>>>();
    }
}
