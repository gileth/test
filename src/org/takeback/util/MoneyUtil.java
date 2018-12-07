// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

public class MoneyUtil
{
    private static final String[] NUMBERS;
    private static final String[] IUNIT;
    private static final String[] DUNIT;
    
    public static String toChinese(String str) {
        str = str.replaceAll(",", "");
        String integerStr;
        String decimalStr;
        if (str.indexOf(".") > 0) {
            integerStr = str.substring(0, str.indexOf("."));
            decimalStr = str.substring(str.indexOf(".") + 1);
        }
        else if (str.indexOf(".") == 0) {
            integerStr = "";
            decimalStr = str.substring(1);
        }
        else {
            integerStr = str;
            decimalStr = "";
        }
        if (!integerStr.equals("")) {
            integerStr = Long.toString(Long.parseLong(integerStr));
            if (integerStr.equals("0")) {
                integerStr = "";
            }
        }
        if (integerStr.length() > MoneyUtil.IUNIT.length) {
            System.out.println(str + ":超出处理能力");
            return str;
        }
        final int[] integers = toArray(integerStr);
        final boolean isMust5 = isMust5(integerStr);
        final int[] decimals = toArray(decimalStr);
        return getChineseInteger(integers, isMust5) + getChineseDecimal(decimals);
    }
    
    private static int[] toArray(final String number) {
        final int[] array = new int[number.length()];
        for (int i = 0; i < number.length(); ++i) {
            array[i] = Integer.parseInt(number.substring(i, i + 1));
        }
        return array;
    }
    
    private static String getChineseInteger(final int[] integers, final boolean isMust5) {
        final StringBuffer chineseInteger = new StringBuffer("");
        for (int length = integers.length, i = 0; i < length; ++i) {
            String key = "";
            if (integers[i] == 0) {
                if (length - i == 13) {
                    key = MoneyUtil.IUNIT[4];
                }
                else if (length - i == 9) {
                    key = MoneyUtil.IUNIT[8];
                }
                else if (length - i == 5 && isMust5) {
                    key = MoneyUtil.IUNIT[4];
                }
                else if (length - i == 1) {
                    key = MoneyUtil.IUNIT[0];
                }
                if (length - i > 1 && integers[i + 1] != 0) {
                    key += MoneyUtil.NUMBERS[0];
                }
            }
            chineseInteger.append((integers[i] == 0) ? key : (MoneyUtil.NUMBERS[integers[i]] + MoneyUtil.IUNIT[length - i - 1]));
        }
        return chineseInteger.toString();
    }
    
    private static String getChineseDecimal(final int[] decimals) {
        final StringBuffer chineseDecimal = new StringBuffer("");
        for (int i = 0; i < decimals.length && i != 3; ++i) {
            chineseDecimal.append((decimals[i] == 0) ? "" : (MoneyUtil.NUMBERS[decimals[i]] + MoneyUtil.DUNIT[i]));
        }
        return chineseDecimal.toString();
    }
    
    private static boolean isMust5(final String integerStr) {
        final int length = integerStr.length();
        if (length > 4) {
            String subInteger = "";
            if (length > 8) {
                subInteger = integerStr.substring(length - 8, length - 4);
            }
            else {
                subInteger = integerStr.substring(0, length - 4);
            }
            return Integer.parseInt(subInteger) > 0;
        }
        return false;
    }
    
    static {
        NUMBERS = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
        IUNIT = new String[] { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟" };
        DUNIT = new String[] { "角", "分", "厘" };
    }
}