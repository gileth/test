// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.utils;

import java.util.HashMap;
import java.util.ArrayList;
import org.takeback.chat.entity.GcRoomProperty;
import java.util.List;
import java.util.Map;

public class RoomTemplate
{
    public static Map<String, List<GcRoomProperty>> templates;
    
    public static List<GcRoomProperty> get(final String gameType) {
        return RoomTemplate.templates.get(gameType);
    }
    
    public static void G01() {
        final List<GcRoomProperty> g01Prop = new ArrayList<GcRoomProperty>();
        RoomTemplate.templates.put("G01", g01Prop);
        final GcRoomProperty p1 = new GcRoomProperty("conf_looser", "\u8f93\u8d62\u89c4\u5219", "min", "\u91d1\u989d\u6700\u5c0f\u8005\u8f93");
        final GcRoomProperty p2 = new GcRoomProperty("conf_size", "\u7ea2\u5305\u4e2a\u6570", "5", "\u7ea2\u5305\u4e2a\u6570");
        final GcRoomProperty p3 = new GcRoomProperty("conf_money", "\u7ea2\u5305\u91d1\u989d", "30", "\u7ea2\u5305\u91d1\u989d");
        final GcRoomProperty p4 = new GcRoomProperty("conf_expired", "\u8fc7\u671f\u65f6\u95f4", "60", "\u6e38\u620f\u5305\u5931\u6548\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2");
        final GcRoomProperty p5 = new GcRoomProperty("conf_title", "\u7ea2\u5305\u6807\u9898", "\u606d\u559c\u53d1\u8d22", "\u7ea2\u5305\u7684\u6807\u9898\uff0c\u6700\u591a6\u4e2a\u5b57");
        final GcRoomProperty p6 = new GcRoomProperty("conf_money_start", "\u5f00\u59cb\u5305\u91d1\u989d", "1", "\u5f00\u59cb\u5305\u91d1\u989d");
        g01Prop.add(p1);
        g01Prop.add(p2);
        g01Prop.add(p3);
        g01Prop.add(p4);
        g01Prop.add(p5);
        g01Prop.add(p6);
    }
    
    public static void G011() {
        final List<GcRoomProperty> g01Prop = new ArrayList<GcRoomProperty>();
        RoomTemplate.templates.put("G011", g01Prop);
        final GcRoomProperty p1 = new GcRoomProperty("conf_looser", "\u8f93\u8d62\u89c4\u5219", "min", "\u91d1\u989d\u6700\u5c0f\u8005\u8f93");
        final GcRoomProperty p2 = new GcRoomProperty("conf_size", "\u7ea2\u5305\u4e2a\u6570", "5", "\u7ea2\u5305\u4e2a\u6570");
        final GcRoomProperty p3 = new GcRoomProperty("conf_rest_time", "\u53d1\u5305\u95f4\u9694(\u79d2)", "5", "\u62a2\u5305\u5b8c\u6bd5\u540e\u95f4\u9694\u51e0\u79d2\u53d1\u51fa\u7ea2\u5305");
        final GcRoomProperty p4 = new GcRoomProperty("conf_money", "\u7ea2\u5305\u91d1\u989d", "30", "\u7ea2\u5305\u91d1\u989d");
        final GcRoomProperty p5 = new GcRoomProperty("conf_expired", "\u8fc7\u671f\u65f6\u95f4", "60", "\u6e38\u620f\u5305\u5931\u6548\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2");
        final GcRoomProperty p6 = new GcRoomProperty("conf_title", "\u7ea2\u5305\u6807\u9898", "\u606d\u559c\u53d1\u8d22", "\u7ea2\u5305\u7684\u6807\u9898\uff0c\u6700\u591a6\u4e2a\u5b57");
        final GcRoomProperty p7 = new GcRoomProperty("conf_money_start", "\u5f00\u59cb\u5305\u91d1\u989d", "1", "\u5f00\u59cb\u5305\u91d1\u989d");
        g01Prop.add(p1);
        g01Prop.add(p2);
        g01Prop.add(p3);
        g01Prop.add(p4);
        g01Prop.add(p5);
        g01Prop.add(p6);
        g01Prop.add(p7);
    }
    
    public static void G012() {
        final List<GcRoomProperty> g01Prop = new ArrayList<GcRoomProperty>();
        RoomTemplate.templates.put("G012", g01Prop);
        final GcRoomProperty p1 = new GcRoomProperty("conf_looser", "\u8f93\u8d62\u89c4\u5219", "min", "\u91d1\u989d\u6700\u5c0f\u8005\u8f93");
        final GcRoomProperty p2 = new GcRoomProperty("conf_size", "\u7ea2\u5305\u4e2a\u6570", "5", "\u7ea2\u5305\u4e2a\u6570");
        final GcRoomProperty p3 = new GcRoomProperty("conf_rest_time", "\u53d1\u5305\u95f4\u9694(\u79d2)", "5", "\u62a2\u5305\u5b8c\u6bd5\u540e\u95f4\u9694\u51e0\u79d2\u53d1\u51fa\u7ea2\u5305");
        final GcRoomProperty p4 = new GcRoomProperty("conf_money", "\u7ea2\u5305\u91d1\u989d", "30", "\u7ea2\u5305\u91d1\u989d");
        final GcRoomProperty p5 = new GcRoomProperty("conf_expired", "\u8fc7\u671f\u65f6\u95f4", "60", "\u6e38\u620f\u5305\u5931\u6548\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2");
        final GcRoomProperty p6 = new GcRoomProperty("conf_title", "\u7ea2\u5305\u6807\u9898", "\u606d\u559c\u53d1\u8d22", "\u7ea2\u5305\u7684\u6807\u9898\uff0c\u6700\u591a6\u4e2a\u5b57");
        final GcRoomProperty p7 = new GcRoomProperty("conf_money_start", "\u5f00\u59cb\u5305\u91d1\u989d", "1", "\u5f00\u59cb\u5305\u91d1\u989d");
        g01Prop.add(p1);
        g01Prop.add(p2);
        g01Prop.add(p3);
        g01Prop.add(p4);
        g01Prop.add(p5);
        g01Prop.add(p6);
        g01Prop.add(p7);
    }
    
    public static void G02() {
        final List<GcRoomProperty> g02Prop = new ArrayList<GcRoomProperty>();
        RoomTemplate.templates.put("G02", g02Prop);
        final GcRoomProperty p1 = new GcRoomProperty("conf_money", "\u5355\u500d\u91d1\u989d", "5", "\u5355\u500d\u8f93\u8d62\u91d1\u989d");
        final GcRoomProperty p2 = new GcRoomProperty("conf_n5", "\u725b5\u500d\u6570", "1", "\u725b5\u500d\u6570");
        final GcRoomProperty p3 = new GcRoomProperty("conf_n6", "\u725b6\u500d\u6570", "1", "\u725b6\u500d\u6570");
        final GcRoomProperty p4 = new GcRoomProperty("conf_n7", "\u725b7\u500d\u6570", "2", "\u725b7\u500d\u6570");
        final GcRoomProperty p5 = new GcRoomProperty("conf_n8", "\u725b8\u500d\u6570", "3", "\u725b8\u500d\u6570");
        final GcRoomProperty p6 = new GcRoomProperty("conf_n9", "\u725b9\u500d\u6570", "4", "\u725b9\u500d\u6570");
        final GcRoomProperty p7 = new GcRoomProperty("conf_n10", "\u725b\u725b\u500d\u6570", "5", "\u725b\u725b\u500d\u6570");
        final GcRoomProperty p8 = new GcRoomProperty("conf_expired", "\u8fc7\u671f\u65f6\u95f4", "60", "\u6e38\u620f\u5305\u5931\u6548\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2");
        final GcRoomProperty p9 = new GcRoomProperty("conf_size", "\u7ea2\u5305\u4e2a\u6570", "5", "\u7ea2\u5305\u4e2a\u6570");
        final GcRoomProperty p10 = new GcRoomProperty("conf_money_game", "\u6e38\u620f\u5305\u91d1\u989d", "1", "\u6e38\u620f\u5305\u91d1\u989d");
        g02Prop.add(p1);
        g02Prop.add(p2);
        g02Prop.add(p3);
        g02Prop.add(p4);
        g02Prop.add(p5);
        g02Prop.add(p6);
        g02Prop.add(p7);
        g02Prop.add(p8);
        g02Prop.add(p9);
        g02Prop.add(p10);
    }
    
    public static void G021() {
        final List<GcRoomProperty> g02Prop = new ArrayList<GcRoomProperty>();
        RoomTemplate.templates.put("G021", g02Prop);
        final GcRoomProperty p1 = new GcRoomProperty("conf_money", "\u5355\u500d\u91d1\u989d", "5", "\u5355\u500d\u8f93\u8d62\u91d1\u989d");
        final GcRoomProperty p2 = new GcRoomProperty("conf_n5", "\u725b8\u500d\u6570", "1", "\u725b5\u500d\u6570");
        final GcRoomProperty p3 = new GcRoomProperty("conf_n6", "\u725b9\u500d\u6570", "1", "\u725b6\u500d\u6570");
        final GcRoomProperty p4 = new GcRoomProperty("conf_n7", "\u725b7\u500d\u6570", "2", "\u725b7\u500d\u6570");
        final GcRoomProperty p5 = new GcRoomProperty("conf_n8", "\u725b8\u500d\u6570", "3", "\u725b8\u500d\u6570");
        final GcRoomProperty p6 = new GcRoomProperty("conf_n9", "\u725b9\u500d\u6570", "4", "\u725b9\u500d\u6570");
        final GcRoomProperty p7 = new GcRoomProperty("conf_n10", "\u725b\u725b\u500d\u6570", "5", "\u725b\u725b\u500d\u6570");
        final GcRoomProperty p8 = new GcRoomProperty("conf_expired", "\u8fc7\u671f\u65f6\u95f4", "60", "\u6e38\u620f\u5305\u5931\u6548\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2");
        final GcRoomProperty p9 = new GcRoomProperty("conf_size", "\u7ea2\u5305\u4e2a\u6570", "5", "\u7ea2\u5305\u4e2a\u6570");
        g02Prop.add(p1);
        g02Prop.add(p2);
        g02Prop.add(p3);
        g02Prop.add(p4);
        g02Prop.add(p5);
        g02Prop.add(p6);
        g02Prop.add(p7);
        g02Prop.add(p8);
        g02Prop.add(p9);
    }
    
    public static void G022() {
        final List<GcRoomProperty> g02Prop = new ArrayList<GcRoomProperty>();
        RoomTemplate.templates.put("G022", g02Prop);
        final GcRoomProperty p1 = new GcRoomProperty("conf_money", "\u5355\u500d\u91d1\u989d", "5", "\u5355\u500d\u8f93\u8d62\u91d1\u989d");
        final GcRoomProperty p2 = new GcRoomProperty("conf_n5", "\u725b5\u500d\u6570", "5", "\u725b5\u500d\u6570");
        final GcRoomProperty p3 = new GcRoomProperty("conf_n6", "\u725b6\u500d\u6570", "6", "\u725b6\u500d\u6570");
        final GcRoomProperty p4 = new GcRoomProperty("conf_n7", "\u725b7\u500d\u6570", "7", "\u725b7\u500d\u6570");
        final GcRoomProperty p5 = new GcRoomProperty("conf_n8", "\u725b8\u500d\u6570", "8", "\u725b8\u500d\u6570");
        final GcRoomProperty p6 = new GcRoomProperty("conf_n9", "\u725b9\u500d\u6570", "9", "\u725b9\u500d\u6570");
        final GcRoomProperty p7 = new GcRoomProperty("conf_n10", "\u725b\u725b\u500d\u6570", "10", "\u725b\u725b\u500d\u6570");
        final GcRoomProperty p8 = new GcRoomProperty("conf_expired", "\u8fc7\u671f\u65f6\u95f4", "60", "\u6e38\u620f\u5305\u5931\u6548\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2");
        final GcRoomProperty p9 = new GcRoomProperty("conf_size", "\u521d\u59cb\u7ea2\u5305\u4e2a\u6570", "5", "\u7ea2\u5305\u4e2a\u6570");
        final GcRoomProperty p10 = new GcRoomProperty("conf_lose", "N\u70b9\u4ee5\u4e0b\u95f2\u8f93", "2", "\u8fd9\u4e2a\u70b9\u6570\u4ee5\u4e0b\u7b97\u95f2\u5bb6\u8f93");
        final GcRoomProperty p11 = new GcRoomProperty("conf_n1", "\u725b1\u500d\u6570", "1", "\u725b1\u500d\u6570");
        final GcRoomProperty p12 = new GcRoomProperty("conf_n2", "\u725b2\u500d\u6570", "2", "\u725b2\u500d\u6570");
        final GcRoomProperty p13 = new GcRoomProperty("conf_n3", "\u725b3\u500d\u6570", "3", "\u725b3\u500d\u6570");
        final GcRoomProperty p14 = new GcRoomProperty("conf_n4", "\u725b4\u500d\u6570", "4", "\u725b4\u500d\u6570");
        final GcRoomProperty p15 = new GcRoomProperty("conf_max_size", "\u6700\u5927\u7ea2\u5305\u4e2a\u6570", "20", "\u6700\u5927\u7ea2\u5305\u4e2a\u6570");
        g02Prop.add(p1);
        g02Prop.add(p2);
        g02Prop.add(p3);
        g02Prop.add(p4);
        g02Prop.add(p5);
        g02Prop.add(p6);
        g02Prop.add(p7);
        g02Prop.add(p8);
        g02Prop.add(p9);
        g02Prop.add(p10);
        g02Prop.add(p11);
        g02Prop.add(p12);
        g02Prop.add(p13);
        g02Prop.add(p14);
        g02Prop.add(p15);
        final GcRoomProperty p16 = new GcRoomProperty("conf_n11", "\u725b11\u500d\u6570", "11", "\u5bf9\u5b50\u500d\u6570");
        final GcRoomProperty p17 = new GcRoomProperty("conf_n12", "\u725b12\u500d\u6570", "12", "0\u5bf9\u500d\u6570");
        final GcRoomProperty p18 = new GcRoomProperty("conf_n13", "\u725b13\u500d\u6570", "13", "\u8c79\u5b50\u500d\u6570");
        g02Prop.add(p16);
        g02Prop.add(p17);
        g02Prop.add(p18);
    }
    
    public static void G04() {
        final List<GcRoomProperty> g01Prop = new ArrayList<GcRoomProperty>();
        RoomTemplate.templates.put("G04", g01Prop);
        final GcRoomProperty p1 = new GcRoomProperty("conf_max_size", "\u6700\u5927\u4e2a\u6570", "10", "\u4e2a\u6570");
        final GcRoomProperty p2 = new GcRoomProperty("conf_min_size", "\u6700\u5c0f\u4e2a\u6570", "10", "\u4e2a\u6570");
        final GcRoomProperty p3 = new GcRoomProperty("conf_max_money", "\u6700\u5927\u91d1\u989d", "30", "\u6700\u5927\u91d1\u989d");
        final GcRoomProperty p4 = new GcRoomProperty("conf_min_money", "\u6700\u5c0f\u91d1\u989d", "30", "\u6700\u5c0f\u91d1\u989d");
        final GcRoomProperty p5 = new GcRoomProperty("conf_expired", "\u8fc7\u671f\u65f6\u95f4", "60", "\u6e38\u620f\u5305\u5931\u6548\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2");
        final GcRoomProperty p6 = new GcRoomProperty("conf_rate", "\u8d54\u4ed8\u500d\u6570", "1", "\u4e2d\u96f7\u8005\u8d54\u4ed8\u7684\u500d\u6570");
        g01Prop.add(p1);
        g01Prop.add(p3);
        g01Prop.add(p4);
        g01Prop.add(p2);
        g01Prop.add(p5);
        g01Prop.add(p6);
    }
    
    static {
        RoomTemplate.templates = new HashMap<String, List<GcRoomProperty>>();
        G01();
        G011();
        G012();
        G02();
        G021();
        G022();
        G04();
    }
}
