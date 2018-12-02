// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class IdcardUtils
{
    public static final int CHINA_ID_MIN_LENGTH = 15;
    public static final int CHINA_ID_MAX_LENGTH = 18;
    public static final String[] cityCode;
    public static final int[] power;
    public static final String[] verifyCode;
    public static final int MIN = 1930;
    public static Map<String, String> cityCodes;
    public static Map<String, Integer> twFirstCode;
    public static Map<String, Integer> hkFirstCode;
    
    public static String conver15CardTo18(final String idCard) {
        String idCard2 = "";
        if (idCard.length() != 15) {
            return null;
        }
        if (isNum(idCard)) {
            final String birthday = idCard.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            final Calendar cal = Calendar.getInstance();
            if (birthDate != null) {
                cal.setTime(birthDate);
            }
            final String sYear = String.valueOf(cal.get(1));
            idCard2 = idCard.substring(0, 6) + sYear + idCard.substring(8);
            final char[] cArr = idCard2.toCharArray();
            if (cArr != null) {
                final int[] iCard = converCharToInt(cArr);
                final int iSum17 = getPowerSum(iCard);
                final String sVal = getCheckCode18(iSum17);
                if (sVal.length() <= 0) {
                    return null;
                }
                idCard2 += sVal;
            }
            return idCard2;
        }
        return null;
    }
    
    public static boolean validateCard(final String idCard) {
        final String card = idCard.trim();
        if (validateIdCard18(card)) {
            return true;
        }
        if (validateIdCard15(card)) {
            return true;
        }
        final String[] cardval = validateIdCard10(card);
        return cardval != null && cardval[2].equals("true");
    }
    
    public static boolean validateIdCard18(final String idCard) {
        boolean bTrue = false;
        if (idCard.length() == 18) {
            final String code17 = idCard.substring(0, 17);
            final String code18 = idCard.substring(17, 18);
            if (isNum(code17)) {
                final char[] cArr = code17.toCharArray();
                if (cArr != null) {
                    final int[] iCard = converCharToInt(cArr);
                    final int iSum17 = getPowerSum(iCard);
                    final String val = getCheckCode18(iSum17);
                    if (val.length() > 0 && val.equalsIgnoreCase(code18)) {
                        bTrue = true;
                    }
                }
            }
        }
        return bTrue;
    }
    
    public static boolean validateIdCard15(final String idCard) {
        if (idCard.length() != 15) {
            return false;
        }
        if (!isNum(idCard)) {
            return false;
        }
        final String proCode = idCard.substring(0, 2);
        if (IdcardUtils.cityCodes.get(proCode) == null) {
            return false;
        }
        final String birthCode = idCard.substring(6, 12);
        Date birthDate = null;
        try {
            birthDate = new SimpleDateFormat("yy").parse(birthCode.substring(0, 2));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        final Calendar cal = Calendar.getInstance();
        if (birthDate != null) {
            cal.setTime(birthDate);
        }
        return valiDate(cal.get(1), Integer.valueOf(birthCode.substring(2, 4)), Integer.valueOf(birthCode.substring(4, 6)));
    }
    
    public static String[] validateIdCard10(final String idCard) {
        final String[] info = new String[3];
        final String card = idCard.replaceAll("[\\(|\\)]", "");
        if (card.length() != 8 && card.length() != 9 && idCard.length() != 10) {
            return null;
        }
        if (idCard.matches("^[a-zA-Z][0-9]{9}$")) {
            info[0] = "\u53f0\u6e7e";
            System.out.println("11111");
            final String char2 = idCard.substring(1, 2);
            if (char2.equals("1")) {
                info[1] = "M";
                System.out.println("MMMMMMM");
            }
            else {
                if (!char2.equals("2")) {
                    info[1] = "N";
                    info[2] = "false";
                    System.out.println("NNNN");
                    return info;
                }
                info[1] = "F";
                System.out.println("FFFFFFF");
            }
            info[2] = (validateTWCard(idCard) ? "true" : "false");
        }
        else if (idCard.matches("^[1|5|7][0-9]{6}\\(?[0-9A-Z]\\)?$")) {
            info[0] = "\u6fb3\u95e8";
            info[1] = "N";
        }
        else {
            if (!idCard.matches("^[A-Z]{1,2}[0-9]{6}\\(?[0-9A]\\)?$")) {
                return null;
            }
            info[0] = "\u9999\u6e2f";
            info[1] = "N";
            info[2] = (validateHKCard(idCard) ? "true" : "false");
        }
        return info;
    }
    
    public static boolean validateTWCard(final String idCard) {
        final String start = idCard.substring(0, 1);
        final String mid = idCard.substring(1, 9);
        final String end = idCard.substring(9, 10);
        final Integer iStart = IdcardUtils.twFirstCode.get(start);
        Integer sum = iStart / 10 + iStart % 10 * 9;
        final char[] chars = mid.toCharArray();
        Integer iflag = 8;
        for (final char c : chars) {
            sum += Integer.valueOf(c + "") * iflag;
            --iflag;
        }
        return ((sum % 10 == 0) ? 0 : (10 - sum % 10)) == Integer.valueOf(end);
    }
    
    public static boolean validateHKCard(final String idCard) {
        String card = idCard.replaceAll("[\\(|\\)]", "");
        Integer sum = 0;
        if (card.length() == 9) {
            sum = (Integer.valueOf(card.substring(0, 1).toUpperCase().toCharArray()[0]) - 55) * 9 + (Integer.valueOf(card.substring(1, 2).toUpperCase().toCharArray()[0]) - 55) * 8;
            card = card.substring(1, 9);
        }
        else {
            sum = 522 + (Integer.valueOf(card.substring(0, 1).toUpperCase().toCharArray()[0]) - 55) * 8;
        }
        final String mid = card.substring(1, 7);
        final String end = card.substring(7, 8);
        final char[] chars = mid.toCharArray();
        Integer iflag = 7;
        for (final char c : chars) {
            sum += Integer.valueOf(c + "") * iflag;
            --iflag;
        }
        if (end.toUpperCase().equals("A")) {
            sum += 10;
        }
        else {
            sum += Integer.valueOf(end);
        }
        return sum % 11 == 0;
    }
    
    public static int[] converCharToInt(final char[] ca) {
        final int len = ca.length;
        final int[] iArr = new int[len];
        try {
            for (int i = 0; i < len; ++i) {
                iArr[i] = Integer.parseInt(String.valueOf(ca[i]));
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return iArr;
    }
    
    public static int getPowerSum(final int[] iArr) {
        int iSum = 0;
        if (IdcardUtils.power.length == iArr.length) {
            for (int i = 0; i < iArr.length; ++i) {
                for (int j = 0; j < IdcardUtils.power.length; ++j) {
                    if (i == j) {
                        iSum += iArr[i] * IdcardUtils.power[j];
                    }
                }
            }
        }
        return iSum;
    }
    
    public static String getCheckCode18(final int iSum) {
        String sCode = "";
        switch (iSum % 11) {
            case 10: {
                sCode = "2";
                break;
            }
            case 9: {
                sCode = "3";
                break;
            }
            case 8: {
                sCode = "4";
                break;
            }
            case 7: {
                sCode = "5";
                break;
            }
            case 6: {
                sCode = "6";
                break;
            }
            case 5: {
                sCode = "7";
                break;
            }
            case 4: {
                sCode = "8";
                break;
            }
            case 3: {
                sCode = "9";
                break;
            }
            case 2: {
                sCode = "x";
                break;
            }
            case 1: {
                sCode = "0";
                break;
            }
            case 0: {
                sCode = "1";
                break;
            }
        }
        return sCode;
    }
    
    public static int getAgeByIdCard(String idCard) {
        int iAge = 0;
        if (idCard.length() == 15) {
            idCard = conver15CardTo18(idCard);
        }
        final String year = idCard.substring(6, 10);
        final Calendar cal = Calendar.getInstance();
        final int iCurrYear = cal.get(1);
        iAge = iCurrYear - Integer.valueOf(year);
        return iAge;
    }
    
    public static String getBirthByIdCard(String idCard) {
        final Integer len = idCard.length();
        if (len < 15) {
            return null;
        }
        if (len == 15) {
            idCard = conver15CardTo18(idCard);
        }
        return idCard.substring(6, 14);
    }
    
    public static Short getYearByIdCard(String idCard) {
        final Integer len = idCard.length();
        if (len < 15) {
            return null;
        }
        if (len == 15) {
            idCard = conver15CardTo18(idCard);
        }
        return Short.valueOf(idCard.substring(6, 10));
    }
    
    public static Short getMonthByIdCard(String idCard) {
        final Integer len = idCard.length();
        if (len < 15) {
            return null;
        }
        if (len == 15) {
            idCard = conver15CardTo18(idCard);
        }
        return Short.valueOf(idCard.substring(10, 12));
    }
    
    public static Short getDateByIdCard(String idCard) {
        final Integer len = idCard.length();
        if (len < 15) {
            return null;
        }
        if (len == 15) {
            idCard = conver15CardTo18(idCard);
        }
        return Short.valueOf(idCard.substring(12, 14));
    }
    
    public static String getGenderByIdCard(String idCard) {
        String sGender = "N";
        if (idCard.length() == 15) {
            idCard = conver15CardTo18(idCard);
        }
        final String sCardNum = idCard.substring(16, 17);
        if (Integer.parseInt(sCardNum) % 2 != 0) {
            sGender = "M";
        }
        else {
            sGender = "F";
        }
        return sGender;
    }
    
    public static String getProvinceByIdCard(final String idCard) {
        final int len = idCard.length();
        String sProvince = null;
        String sProvinNum = "";
        if (len == 15 || len == 18) {
            sProvinNum = idCard.substring(0, 2);
        }
        sProvince = IdcardUtils.cityCodes.get(sProvinNum);
        return sProvince;
    }
    
    public static boolean isNum(final String val) {
        return val != null && !"".equals(val) && val.matches("^[0-9]*$");
    }
    
    public static boolean valiDate(final int iYear, final int iMonth, final int iDate) {
        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(1);
        if (iYear < 1930 || iYear >= year) {
            return false;
        }
        if (iMonth < 1 || iMonth > 12) {
            return false;
        }
        int datePerMonth = 0;
        switch (iMonth) {
            case 4:
            case 6:
            case 9:
            case 11: {
                datePerMonth = 30;
                break;
            }
            case 2: {
                final boolean dm = ((iYear % 4 == 0 && iYear % 100 != 0) || iYear % 400 == 0) && iYear > 1930 && iYear < year;
                datePerMonth = (dm ? 29 : 28);
                break;
            }
            default: {
                datePerMonth = 31;
                break;
            }
        }
        return iDate >= 1 && iDate <= datePerMonth;
    }
    
    static {
        cityCode = new String[] { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71", "81", "82", "91" };
        power = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
        verifyCode = new String[] { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
        IdcardUtils.cityCodes = new HashMap<String, String>();
        IdcardUtils.twFirstCode = new HashMap<String, Integer>();
        IdcardUtils.hkFirstCode = new HashMap<String, Integer>();
        IdcardUtils.cityCodes.put("11", "\u5317\u4eac");
        IdcardUtils.cityCodes.put("12", "\u5929\u6d25");
        IdcardUtils.cityCodes.put("13", "\u6cb3\u5317");
        IdcardUtils.cityCodes.put("14", "\u5c71\u897f");
        IdcardUtils.cityCodes.put("15", "\u5185\u8499\u53e4");
        IdcardUtils.cityCodes.put("21", "\u8fbd\u5b81");
        IdcardUtils.cityCodes.put("22", "\u5409\u6797");
        IdcardUtils.cityCodes.put("23", "\u9ed1\u9f99\u6c5f");
        IdcardUtils.cityCodes.put("31", "\u4e0a\u6d77");
        IdcardUtils.cityCodes.put("32", "\u6c5f\u82cf");
        IdcardUtils.cityCodes.put("33", "\u6d59\u6c5f");
        IdcardUtils.cityCodes.put("34", "\u5b89\u5fbd");
        IdcardUtils.cityCodes.put("35", "\u798f\u5efa");
        IdcardUtils.cityCodes.put("36", "\u6c5f\u897f");
        IdcardUtils.cityCodes.put("37", "\u5c71\u4e1c");
        IdcardUtils.cityCodes.put("41", "\u6cb3\u5357");
        IdcardUtils.cityCodes.put("42", "\u6e56\u5317");
        IdcardUtils.cityCodes.put("43", "\u6e56\u5357");
        IdcardUtils.cityCodes.put("44", "\u5e7f\u4e1c");
        IdcardUtils.cityCodes.put("45", "\u5e7f\u897f");
        IdcardUtils.cityCodes.put("46", "\u6d77\u5357");
        IdcardUtils.cityCodes.put("50", "\u91cd\u5e86");
        IdcardUtils.cityCodes.put("51", "\u56db\u5ddd");
        IdcardUtils.cityCodes.put("52", "\u8d35\u5dde");
        IdcardUtils.cityCodes.put("53", "\u4e91\u5357");
        IdcardUtils.cityCodes.put("54", "\u897f\u85cf");
        IdcardUtils.cityCodes.put("61", "\u9655\u897f");
        IdcardUtils.cityCodes.put("62", "\u7518\u8083");
        IdcardUtils.cityCodes.put("63", "\u9752\u6d77");
        IdcardUtils.cityCodes.put("64", "\u5b81\u590f");
        IdcardUtils.cityCodes.put("65", "\u65b0\u7586");
        IdcardUtils.cityCodes.put("71", "\u53f0\u6e7e");
        IdcardUtils.cityCodes.put("81", "\u9999\u6e2f");
        IdcardUtils.cityCodes.put("82", "\u6fb3\u95e8");
        IdcardUtils.cityCodes.put("91", "\u56fd\u5916");
        IdcardUtils.twFirstCode.put("A", 10);
        IdcardUtils.twFirstCode.put("B", 11);
        IdcardUtils.twFirstCode.put("C", 12);
        IdcardUtils.twFirstCode.put("D", 13);
        IdcardUtils.twFirstCode.put("E", 14);
        IdcardUtils.twFirstCode.put("F", 15);
        IdcardUtils.twFirstCode.put("G", 16);
        IdcardUtils.twFirstCode.put("H", 17);
        IdcardUtils.twFirstCode.put("J", 18);
        IdcardUtils.twFirstCode.put("K", 19);
        IdcardUtils.twFirstCode.put("L", 20);
        IdcardUtils.twFirstCode.put("M", 21);
        IdcardUtils.twFirstCode.put("N", 22);
        IdcardUtils.twFirstCode.put("P", 23);
        IdcardUtils.twFirstCode.put("Q", 24);
        IdcardUtils.twFirstCode.put("R", 25);
        IdcardUtils.twFirstCode.put("S", 26);
        IdcardUtils.twFirstCode.put("T", 27);
        IdcardUtils.twFirstCode.put("U", 28);
        IdcardUtils.twFirstCode.put("V", 29);
        IdcardUtils.twFirstCode.put("X", 30);
        IdcardUtils.twFirstCode.put("Y", 31);
        IdcardUtils.twFirstCode.put("W", 32);
        IdcardUtils.twFirstCode.put("Z", 33);
        IdcardUtils.twFirstCode.put("I", 34);
        IdcardUtils.twFirstCode.put("O", 35);
        IdcardUtils.hkFirstCode.put("A", 1);
        IdcardUtils.hkFirstCode.put("B", 2);
        IdcardUtils.hkFirstCode.put("C", 3);
        IdcardUtils.hkFirstCode.put("R", 18);
        IdcardUtils.hkFirstCode.put("U", 21);
        IdcardUtils.hkFirstCode.put("Z", 26);
        IdcardUtils.hkFirstCode.put("X", 24);
        IdcardUtils.hkFirstCode.put("W", 23);
        IdcardUtils.hkFirstCode.put("O", 15);
        IdcardUtils.hkFirstCode.put("N", 14);
    }
}
