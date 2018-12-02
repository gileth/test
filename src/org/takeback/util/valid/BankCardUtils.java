// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.valid;

public class BankCardUtils
{
    public static boolean checkBankCard(final String cardId) {
        final char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        return bit != 'N' && cardId.charAt(cardId.length() - 1) == bit;
    }
    
    public static char getBankCardCheckCode(final String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0 || !nonCheckCodeCardId.matches("\\d+")) {
            return 'N';
        }
        final char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; --i, ++j) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : ((char)(10 - luhmSum % 10 + 48));
    }
    
    public static void main(final String[] args) {
        System.out.println(checkBankCard("6225881414207430"));
    }
}
