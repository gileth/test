// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.io.Serializable;

public class CodeRule implements Serializable
{
    private String codeDefine;
    private int[] layerLens;
    private String[] define;
    private HashMap<Integer, Integer> layersMapping;
    private int count;
    private int maxLen;
    
    public CodeRule(final String cd) {
        this.codeDefine = cd;
        this.define = cd.split(",");
        this.count = this.define.length;
        this.layerLens = new int[this.count];
        this.layersMapping = new HashMap<Integer, Integer>();
        int i = 0;
        for (final String s : this.define) {
            final int len = Integer.parseInt(s);
            this.maxLen += len;
            if (i == 0) {
                this.layerLens[i] = len;
                this.layersMapping.put(len, i);
            }
            else {
                this.layerLens[i] = this.layerLens[i - 1] + len;
                this.layersMapping.put(this.layerLens[i - 1] + len, i);
            }
            ++i;
        }
    }
    
    public int getLayerCount() {
        return this.count;
    }
    
    public int getLayerLength(final int i) {
        if (i < this.count) {
            return this.layerLens[i];
        }
        return 0;
    }
    
    public boolean isLeaf(final String key) {
        return key.length() == this.maxLen;
    }
    
    public int indexOfLayer(final String key) {
        if (StringUtils.isEmpty((CharSequence)key)) {
            return -1;
        }
        final int keySize = key.length();
        if (this.layersMapping.containsKey(keySize)) {
            return this.layersMapping.get(keySize);
        }
        return -1;
    }
    
    public String getParentKey(final String key) {
        if (StringUtils.isEmpty((CharSequence)key)) {
            return "";
        }
        int index = this.indexOfLayer(key);
        if (index < 1) {
            return "";
        }
        --index;
        return key.substring(0, this.layerLens[index]);
    }
    
    public int getNextLength(final int length) {
        int i = 0;
        while (i < this.layerLens.length) {
            if (this.layerLens[i] == length) {
                if (i == this.layerLens.length - 1) {
                    return this.layerLens[i];
                }
                return this.layerLens[i + 1];
            }
            else {
                ++i;
            }
        }
        return -1;
    }
    
    public int getParentLength(final int length) {
        int i = 0;
        while (i < this.layerLens.length) {
            if (this.layerLens[i] == length) {
                if (i == 0) {
                    return this.layerLens[i];
                }
                return this.layerLens[i - 1];
            }
            else {
                ++i;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        return this.codeDefine;
    }
}
