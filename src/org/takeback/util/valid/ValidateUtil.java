// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.valid;

import java.util.regex.Pattern;

public class ValidateUtil
{
    private static ValidateUtil instance;
    public static final String regexPhonenumb = "^1[3-9][0-9]{9}$";
    public static final String regexEmail = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";
    public static final String regexAccount = "^[a-zA-Z_][a-zA-Z0-9]{5,15}$";
    public static final String regexPwd = "^[a-zA-Z_][a-zA-Z0-9]{5,15}$";
    public static final String allNumber = "^[0-9]{1,15}$";
    private Pattern pPhonenumb;
    private Pattern pEmail;
    private Pattern pAccount;
    private Pattern pPwd;
    
    public ValidateUtil() {
        this.pPhonenumb = Pattern.compile("^1[3-9][0-9]{9}$");
        this.pEmail = Pattern.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");
        this.pAccount = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9]{5,15}$");
        this.pPwd = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9]{5,15}$");
    }
    
    public static ValidateUtil instance() {
        if (ValidateUtil.instance == null) {
            ValidateUtil.instance = new ValidateUtil();
        }
        return ValidateUtil.instance;
    }
    
    public boolean validatePhone(final String value) {
        return this.pPhonenumb.matcher(value).matches();
    }
    
    public boolean validateEmail(final String value) {
        return this.pEmail.matcher(value).matches();
    }
    
    public boolean validateAccount(final String value) {
        return this.pAccount.matcher(value).matches();
    }
    
    public boolean isChinese(final String value) {
        for (int i = 0; i < value.length(); ++i) {
            final int c = value.charAt(i);
            if (c < 19968 || c > 171941) {
                return false;
            }
        }
        return true;
    }
    
    public boolean validatePwd(final String value) {
        return this.pPwd.matcher(value).matches();
    }
    
    static {
        ValidateUtil.instance = null;
    }
}
