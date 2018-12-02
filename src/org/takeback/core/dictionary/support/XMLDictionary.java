// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary.support;

import java.util.Iterator;
import java.util.ArrayList;
import org.dom4j.Element;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.dictionary.DictionaryItem;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dom4j.Document;
import org.takeback.core.dictionary.Dictionary;

public class XMLDictionary extends Dictionary
{
    private static final long serialVersionUID = -596194603210170948L;
    private static final String LEAF_CNDS = "count(./*) = 0";
    private static final String FOLDER_CNDS = "count(./*) > 0";
    private Document defineDoc;
    
    public XMLDictionary() {
    }
    
    public XMLDictionary(final String id) {
        this.setId(id);
    }
    
    public void setDefineDoc(final Document doc) {
        this.defineDoc = doc;
    }
    
    @JsonIgnore
    public Document getDefineDoc() {
        return this.defineDoc;
    }
    
    @Override
    public List<DictionaryItem> getSlice(final String parentKey, final int sliceType, final String query) {
        List<DictionaryItem> result = null;
        switch (sliceType) {
            case 2: {
                result = this.getAllFolder(parentKey, query);
                break;
            }
            case 1: {
                result = this.getAllLeaf(parentKey, query);
                break;
            }
            case 3: {
                result = this.getAllChild(parentKey, query);
                break;
            }
            case 5: {
                result = this.getChildFolder(parentKey, query);
                break;
            }
            case 4: {
                result = this.getChildLeaf(parentKey, query);
                break;
            }
            default: {
                result = this.getAllItems(parentKey, query);
                break;
            }
        }
        return result;
    }
    
    private void linkQueryXPath(final StringBuffer xpath, final String query, final String exCnd) {
        if (!StringUtils.isEmpty((CharSequence)query)) {
            xpath.append("contains(lower-case(@");
            final char first = query.charAt(0);
            if (first == this.searchKeySymbol) {
                xpath.append("key").append("),lower-case('").append(query.substring(1)).append("')");
            }
            else if (first == this.searchExSymbol) {
                xpath.append(this.searchFieldEx).append("),lower-case('").append(query.substring(1)).append("')");
            }
            else {
                xpath.append(this.searchField).append("),lower-case('").append(query).append("')");
            }
            xpath.append(")");
            if (!StringUtils.isEmpty((CharSequence)exCnd)) {
                xpath.append(" and ").append(exCnd);
            }
        }
        else if (!StringUtils.isEmpty((CharSequence)exCnd)) {
            xpath.append(exCnd);
        }
    }
    
    private List<DictionaryItem> toDictionaryItemList(final List<Element> ls) {
        final List<DictionaryItem> result = new ArrayList<DictionaryItem>();
        for (final Element el : ls) {
            final String key = el.attributeValue("key");
            result.add(this.items.get(key));
        }
        return result;
    }
    
    private List<DictionaryItem> getAllItems(final String parentKey, final String query) {
        if (this.defineDoc == null) {
            return null;
        }
        final Element define = this.defineDoc.getRootElement();
        final StringBuffer xpath = new StringBuffer();
        if (!StringUtils.isEmpty((CharSequence)parentKey)) {
            xpath.append("//item[@key='").append(parentKey).append("']");
        }
        if (!StringUtils.isEmpty((CharSequence)query)) {
            xpath.append("//item[");
            this.linkQueryXPath(xpath, query, null);
            xpath.append("]");
        }
        else {
            xpath.append("//item");
        }
        final List<Element> ls = (List<Element>)define.selectNodes(xpath.toString());
        return this.toDictionaryItemList(ls);
    }
    
    private List<DictionaryItem> getAllChild(final String parentKey, final String query) {
        if (this.defineDoc == null) {
            return null;
        }
        final Element define = this.defineDoc.getRootElement();
        final StringBuffer xpath = new StringBuffer();
        if (!StringUtils.isEmpty((CharSequence)parentKey)) {
            xpath.append("//item[@key='").append(parentKey).append("']");
        }
        else {
            xpath.append(".");
        }
        if (!StringUtils.isEmpty((CharSequence)query)) {
            xpath.append("/item[");
            this.linkQueryXPath(xpath, query, null);
            xpath.append("]");
        }
        else {
            xpath.append("/item");
        }
        final List<Element> ls = (List<Element>)define.selectNodes(xpath.toString());
        return this.toDictionaryItemList(ls);
    }
    
    private List<DictionaryItem> getAllLeaf(final String parentKey, final String query) {
        if (this.defineDoc == null) {
            return null;
        }
        final Element define = this.defineDoc.getRootElement();
        final StringBuffer xpath = new StringBuffer();
        if (!StringUtils.isEmpty((CharSequence)parentKey)) {
            xpath.append("//item[@key='").append(parentKey).append("']/item[");
        }
        else {
            xpath.append("//item[");
        }
        this.linkQueryXPath(xpath, query, "count(./*) = 0");
        xpath.append("]");
        final List<Element> ls = (List<Element>)define.selectNodes(xpath.toString());
        return this.toDictionaryItemList(ls);
    }
    
    private List<DictionaryItem> getAllFolder(final String parentKey, final String query) {
        if (this.defineDoc == null) {
            return null;
        }
        final Element define = this.defineDoc.getRootElement();
        final StringBuffer xpath = new StringBuffer();
        if (!StringUtils.isEmpty((CharSequence)parentKey)) {
            xpath.append("//item[@key='").append(parentKey).append("']//item[");
        }
        else {
            xpath.append("//item[");
        }
        this.linkQueryXPath(xpath, query, "count(./*) > 0");
        xpath.append("]");
        final List<Element> ls = (List<Element>)define.selectNodes(xpath.toString());
        return this.toDictionaryItemList(ls);
    }
    
    private List<DictionaryItem> getChildFolder(final String parentKey, final String query) {
        if (this.defineDoc == null) {
            return null;
        }
        final Element define = this.defineDoc.getRootElement();
        final StringBuffer xpath = new StringBuffer();
        if (!StringUtils.isEmpty((CharSequence)parentKey)) {
            xpath.append("//item[@key='").append(parentKey).append("']/item[");
        }
        else {
            xpath.append("item[");
        }
        this.linkQueryXPath(xpath, query, "count(./*) > 0");
        xpath.append("]");
        final List<Element> ls = (List<Element>)define.selectNodes(xpath.toString());
        return this.toDictionaryItemList(ls);
    }
    
    private List<DictionaryItem> getChildLeaf(final String parentKey, final String query) {
        if (this.defineDoc == null) {
            return null;
        }
        final Element define = this.defineDoc.getRootElement();
        final StringBuffer xpath = new StringBuffer();
        if (!StringUtils.isEmpty((CharSequence)parentKey)) {
            xpath.append("//item[@key='").append(parentKey).append("']/item[");
        }
        else {
            xpath.append("item[");
        }
        this.linkQueryXPath(xpath, query, "count(./*) = 0");
        xpath.append("]");
        final List<Element> ls = (List<Element>)define.selectNodes(xpath.toString());
        return this.toDictionaryItemList(ls);
    }
}
