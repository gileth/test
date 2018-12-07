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
        final GcRoomProperty p1 = new GcRoomProperty("conf_looser", "输赢规则", "min", "金额最小者输");
        final GcRoomProperty p2 = new GcRoomProperty("conf_size", "红包个数", "5", "红包个数");
        final GcRoomProperty p3 = new GcRoomProperty("conf_money", "红包金额", "30", "红包金额");
        final GcRoomProperty p4 = new GcRoomProperty("conf_expired", "过期时间", "60", "游戏包失效时间，单位秒");
        final GcRoomProperty p5 = new GcRoomProperty("conf_title", "红包标题", "恭喜发财", "红包的标题，最多6个字");
        final GcRoomProperty p6 = new GcRoomProperty("conf_money_start", "开始包金额", "1", "开始包金额");
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
        final GcRoomProperty p1 = new GcRoomProperty("conf_looser", "输赢规则", "min", "金额最小者输");
        final GcRoomProperty p2 = new GcRoomProperty("conf_size", "红包个数", "5", "红包个数");
        final GcRoomProperty p3 = new GcRoomProperty("conf_rest_time", "发包间隔(秒)", "5", "抢包完毕后间隔几秒发出红包");
        final GcRoomProperty p4 = new GcRoomProperty("conf_money", "红包金额", "30", "红包金额");
        final GcRoomProperty p5 = new GcRoomProperty("conf_expired", "过期时间", "60", "游戏包失效时间，单位秒");
        final GcRoomProperty p6 = new GcRoomProperty("conf_title", "红包标题", "恭喜发财", "红包的标题，最多6个字");
        final GcRoomProperty p7 = new GcRoomProperty("conf_money_start", "开始包金额", "1", "开始包金额");
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
        final GcRoomProperty p1 = new GcRoomProperty("conf_looser", "输赢规则", "min", "金额最小者输");
        final GcRoomProperty p2 = new GcRoomProperty("conf_size", "红包个数", "5", "红包个数");
        final GcRoomProperty p3 = new GcRoomProperty("conf_rest_time", "发包间隔(秒)", "5", "抢包完毕后间隔几秒发出红包");
        final GcRoomProperty p4 = new GcRoomProperty("conf_money", "红包金额", "30", "红包金额");
        final GcRoomProperty p5 = new GcRoomProperty("conf_expired", "过期时间", "60", "游戏包失效时间，单位秒");
        final GcRoomProperty p6 = new GcRoomProperty("conf_title", "红包标题", "恭喜发财", "红包的标题，最多6个字");
        final GcRoomProperty p7 = new GcRoomProperty("conf_money_start", "开始包金额", "1", "开始包金额");
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
        final GcRoomProperty p1 = new GcRoomProperty("conf_money", "单倍金额", "5", "单倍输赢金额");
        final GcRoomProperty p2 = new GcRoomProperty("conf_n5", "牛5倍数", "1", "牛5倍数");
        final GcRoomProperty p3 = new GcRoomProperty("conf_n6", "牛6倍数", "1", "牛6倍数");
        final GcRoomProperty p4 = new GcRoomProperty("conf_n7", "牛7倍数", "2", "牛7倍数");
        final GcRoomProperty p5 = new GcRoomProperty("conf_n8", "牛8倍数", "3", "牛8倍数");
        final GcRoomProperty p6 = new GcRoomProperty("conf_n9", "牛9倍数", "4", "牛9倍数");
        final GcRoomProperty p7 = new GcRoomProperty("conf_n10", "牛牛倍数", "5", "牛牛倍数");
        final GcRoomProperty p8 = new GcRoomProperty("conf_expired", "过期时间", "60", "游戏包失效时间，单位秒");
        final GcRoomProperty p9 = new GcRoomProperty("conf_size", "红包个数", "5", "红包个数");
        final GcRoomProperty p10 = new GcRoomProperty("conf_money_game", "游戏包金额", "1", "游戏包金额");
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
        final GcRoomProperty p1 = new GcRoomProperty("conf_money", "单倍金额", "5", "单倍输赢金额");
        final GcRoomProperty p2 = new GcRoomProperty("conf_n5", "牛8倍数", "1", "牛5倍数");
        final GcRoomProperty p3 = new GcRoomProperty("conf_n6", "牛9倍数", "1", "牛6倍数");
        final GcRoomProperty p4 = new GcRoomProperty("conf_n7", "牛7倍数", "2", "牛7倍数");
        final GcRoomProperty p5 = new GcRoomProperty("conf_n8", "牛8倍数", "3", "牛8倍数");
        final GcRoomProperty p6 = new GcRoomProperty("conf_n9", "牛9倍数", "4", "牛9倍数");
        final GcRoomProperty p7 = new GcRoomProperty("conf_n10", "牛牛倍数", "5", "牛牛倍数");
        final GcRoomProperty p8 = new GcRoomProperty("conf_expired", "过期时间", "60", "游戏包失效时间，单位秒");
        final GcRoomProperty p9 = new GcRoomProperty("conf_size", "红包个数", "5", "红包个数");
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
        final GcRoomProperty p1 = new GcRoomProperty("conf_money", "单倍金额", "5", "单倍输赢金额");
        final GcRoomProperty p2 = new GcRoomProperty("conf_n5", "牛5倍数", "5", "牛5倍数");
        final GcRoomProperty p3 = new GcRoomProperty("conf_n6", "牛6倍数", "6", "牛6倍数");
        final GcRoomProperty p4 = new GcRoomProperty("conf_n7", "牛7倍数", "7", "牛7倍数");
        final GcRoomProperty p5 = new GcRoomProperty("conf_n8", "牛8倍数", "8", "牛8倍数");
        final GcRoomProperty p6 = new GcRoomProperty("conf_n9", "牛9倍数", "9", "牛9倍数");
        final GcRoomProperty p7 = new GcRoomProperty("conf_n10", "牛牛倍数", "10", "牛牛倍数");
        final GcRoomProperty p8 = new GcRoomProperty("conf_expired", "过期时间", "60", "游戏包失效时间，单位秒");
        final GcRoomProperty p9 = new GcRoomProperty("conf_size", "初始红包个数", "5", "红包个数");
        final GcRoomProperty p10 = new GcRoomProperty("conf_lose", "N点以下闲输", "2", "这个点数以下算闲家输");
        final GcRoomProperty p11 = new GcRoomProperty("conf_n1", "牛1倍数", "1", "牛1倍数");
        final GcRoomProperty p12 = new GcRoomProperty("conf_n2", "牛2倍数", "2", "牛2倍数");
        final GcRoomProperty p13 = new GcRoomProperty("conf_n3", "牛3倍数", "3", "牛3倍数");
        final GcRoomProperty p14 = new GcRoomProperty("conf_n4", "牛4倍数", "4", "牛4倍数");
        final GcRoomProperty p15 = new GcRoomProperty("conf_max_size", "最大红包个数", "20", "最大红包个数");
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
        final GcRoomProperty p16 = new GcRoomProperty("conf_n11", "牛11倍数", "11", "对子倍数");
        final GcRoomProperty p17 = new GcRoomProperty("conf_n12", "牛12倍数", "12", "0对倍数");
        final GcRoomProperty p18 = new GcRoomProperty("conf_n13", "牛13倍数", "13", "豹子倍数");
        g02Prop.add(p16);
        g02Prop.add(p17);
        g02Prop.add(p18);
    }
    
    public static void G04() {
        final List<GcRoomProperty> g01Prop = new ArrayList<GcRoomProperty>();
        RoomTemplate.templates.put("G04", g01Prop);
        final GcRoomProperty p1 = new GcRoomProperty("conf_max_size", "最大个数", "10", "个数");
        final GcRoomProperty p2 = new GcRoomProperty("conf_min_size", "最小个数", "10", "个数");
        final GcRoomProperty p3 = new GcRoomProperty("conf_max_money", "最大金额", "30", "最大金额");
        final GcRoomProperty p4 = new GcRoomProperty("conf_min_money", "最小金额", "30", "最小金额");
        final GcRoomProperty p5 = new GcRoomProperty("conf_expired", "过期时间", "60", "游戏包失效时间，单位秒");
        final GcRoomProperty p6 = new GcRoomProperty("conf_rate", "赔付倍数", "1", "中雷者赔付的倍数");
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