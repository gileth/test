// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.commons.lang3.StringUtils;

public class PyConverter
{
    public static String getFirstLetter(final String s) {
        if (StringUtils.isEmpty((CharSequence)s)) {
            return null;
        }
        return PinyinHelper.getShortPinyin(s);
    }
    
    public static String getPinYinWithoutTone(final String s) {
        return PinyinHelper.convertToPinyinString(s, "", PinyinFormat.WITHOUT_TONE);
    }
    
    public static String getPinYin(final String s) {
        return PinyinHelper.convertToPinyinString(s, "");
    }
    
    public static void main(final String[] args) {
        System.out.println(getPinYinWithoutTone("\u4e2d\u534e\u4eba\u6c11\u5171\u548c\u56fd"));
    }
}
