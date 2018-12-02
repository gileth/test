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
            errorInfo = "\u8eab\u4efd\u8bc1\u53f7\u7801\u957f\u5ea6\u5e94\u8be5\u4e3a15\u4f4d\u621618\u4f4d\u3002";
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
            errorInfo = "\u8eab\u4efd\u8bc115\u4f4d\u53f7\u7801\u90fd\u5e94\u4e3a\u6570\u5b57 ; 18\u4f4d\u53f7\u7801\u9664\u6700\u540e\u4e00\u4f4d\u5916\uff0c\u90fd\u5e94\u4e3a\u6570\u5b57\u3002";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        final String strYear = Ai.substring(6, 10);
        final String strMonth = Ai.substring(10, 12);
        final String strDay = Ai.substring(12, 14);
        if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
            errorInfo = "\u8eab\u4efd\u8bc1\u751f\u65e5\u65e0\u6548\u3002";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        final GregorianCalendar gc = new GregorianCalendar();
        final SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (gc.get(1) - Integer.parseInt(strYear) > 150 || gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime() < 0L) {
                errorInfo = "\u8eab\u4efd\u8bc1\u751f\u65e5\u4e0d\u5728\u6709\u6548\u8303\u56f4\u3002";
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
            errorInfo = "\u8eab\u4efd\u8bc1\u6708\u4efd\u65e0\u6548";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "\u8eab\u4efd\u8bc1\u65e5\u671f\u65e0\u6548";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        final Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "\u8eab\u4efd\u8bc1\u5730\u533a\u7f16\u7801\u9519\u8bef\u3002";
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
            errorInfo = "\u8eab\u4efd\u8bc1\u65e0\u6548\uff0c\u4e0d\u662f\u5408\u6cd5\u7684\u8eab\u4efd\u8bc1\u53f7\u7801";
            System.out.println("IDStr " + errorInfo);
            return false;
        }
        return true;
    }
    
    private static Hashtable GetAreaCode() {
        final Hashtable hashtable = new Hashtable();
        hashtable.put("11", "\u5317\u4eac");
        hashtable.put("12", "\u5929\u6d25");
        hashtable.put("13", "\u6cb3\u5317");
        hashtable.put("14", "\u5c71\u897f");
        hashtable.put("15", "\u5185\u8499\u53e4");
        hashtable.put("21", "\u8fbd\u5b81");
        hashtable.put("22", "\u5409\u6797");
        hashtable.put("23", "\u9ed1\u9f99\u6c5f");
        hashtable.put("31", "\u4e0a\u6d77");
        hashtable.put("32", "\u6c5f\u82cf");
        hashtable.put("33", "\u6d59\u6c5f");
        hashtable.put("34", "\u5b89\u5fbd");
        hashtable.put("35", "\u798f\u5efa");
        hashtable.put("36", "\u6c5f\u897f");
        hashtable.put("37", "\u5c71\u4e1c");
        hashtable.put("41", "\u6cb3\u5357");
        hashtable.put("42", "\u6e56\u5317");
        hashtable.put("43", "\u6e56\u5357");
        hashtable.put("44", "\u5e7f\u4e1c");
        hashtable.put("45", "\u5e7f\u897f");
        hashtable.put("46", "\u6d77\u5357");
        hashtable.put("50", "\u91cd\u5e86");
        hashtable.put("51", "\u56db\u5ddd");
        hashtable.put("52", "\u8d35\u5dde");
        hashtable.put("53", "\u4e91\u5357");
        hashtable.put("54", "\u897f\u85cf");
        hashtable.put("61", "\u9655\u897f");
        hashtable.put("62", "\u7518\u8083");
        hashtable.put("63", "\u9752\u6d77");
        hashtable.put("64", "\u5b81\u590f");
        hashtable.put("65", "\u65b0\u7586");
        hashtable.put("71", "\u53f0\u6e7e");
        hashtable.put("81", "\u9999\u6e2f");
        hashtable.put("82", "\u6fb3\u95e8");
        hashtable.put("91", "\u56fd\u5916");
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
