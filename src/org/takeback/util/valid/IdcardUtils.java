// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.valid;

import java.util.regex.Matcher;
import java.util.Hashtable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

public class IdcardUtils
{
    private static final String regexPassport = "^(P\\d{7}|G\\d{8}|S\\d{7,8}|D\\d+|1[4,5]\\d{7})$";
    private static Pattern pPassport;
    
    public static boolean IDCardValidate(final String IDStr) throws ParseException {
        String errorInfo = "";
        final String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
        final String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        }
        else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (!isNumeric(Ai)) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        final String strYear = Ai.substring(6, 10);
        final String strMonth = Ai.substring(10, 12);
        final String strDay = Ai.substring(12, 14);
        if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
            errorInfo = "身份证生日无效。";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        final GregorianCalendar gc = new GregorianCalendar();
        final SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (gc.get(1) - Integer.parseInt(strYear) > 150 || gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime() < 0L) {
                errorInfo = "身份证生日不在有效范围。";
                System.out.println("IDStr " + errorInfo);
                return false;
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        catch (ParseException e2) {
            e2.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        final Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; ++i) {
            TotalmulAiWi += Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        final int modValue = TotalmulAiWi % 11;
        final String strVerifyCode = ValCodeArr[modValue];
        Ai += strVerifyCode;
        if (IDStr.length() != 18) {
            return true;
        }
        if (!Ai.equals(IDStr)) {
            errorInfo = "身份证无效，不是合法的身份证号码";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        return true;
    }
    
    private static Hashtable GetAreaCode() {
        final Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }
    
    private static boolean isNumeric(final String str) {
        final Pattern pattern = Pattern.compile("[0-9]*");
        final Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
    
    public static boolean isDate(final String strDate) {
        final Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        final Matcher m = pattern.matcher(strDate);
        return m.matches();
    }
    
    public static boolean PassportValidate(final String passportNo) {
        final Matcher m = IdcardUtils.pPassport.matcher(passportNo);
        return m.matches();
    }
    
    public static void main(final String[] args) throws ParseException {
        final String IDCardNum = "340822198811180532";
        final String PassportNum = "P1234555";
        final IdcardUtils cc = new IdcardUtils();
        System.out.println(IDCardValidate(IDCardNum));
        System.out.println(PassportValidate(PassportNum));
    }
    
    static {
        IdcardUtils.pPassport = Pattern.compile("^(P\\d{7}|G\\d{8}|S\\d{7,8}|D\\d+|1[4,5]\\d{7})$");
    }
}