// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary.support;

import org.takeback.core.dictionary.DictionaryItem;
import java.util.Comparator;

class TableDictionary$1 implements Comparator<DictionaryItem> {
    @Override
    public int compare(final DictionaryItem d1, final DictionaryItem d2) {
        if (d1.getKey().length() > d2.getKey().length()) {
            return 1;
        }
        return -1;
    }
}