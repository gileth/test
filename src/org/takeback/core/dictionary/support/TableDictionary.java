// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.dictionary.support;

import org.slf4j.LoggerFactory;
import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.util.JSONUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import org.takeback.util.exp.exception.ExprException;
import java.util.Collection;
import org.takeback.core.schema.DataTypes;
import org.hibernate.Query;
import org.hibernate.Session;
import org.takeback.util.ApplicationContextHolder;
import org.hibernate.SessionFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.takeback.core.dictionary.DictionaryItem;
import java.util.LinkedHashMap;
import java.util.HashSet;
import org.takeback.core.dictionary.CodeRule;
import org.slf4j.Logger;
import org.takeback.core.dictionary.Dictionary;

public class TableDictionary extends Dictionary
{
    private static final long serialVersionUID = -997610255379902104L;
    private static final Logger LOGGER;
    protected CodeRule codeRule;
    protected String parent;
    protected String entityName;
    protected String keyField;
    protected String textField;
    protected String sortField;
    protected boolean ignoreSearchFieldExPrefix;
    protected String filter;
    protected String where;
    protected String iconCls;
    protected boolean supportRemote;
    protected String queryType;
    protected String searchFieldType;
    protected HashSet<String> folders;
    protected LinkedHashMap<String, String> propFields;
    protected boolean distinct;
    private String sessionFactory;
    
    public TableDictionary() {
        this.keyField = "id";
        this.textField = "text";
        this.where = "";
        this.supportRemote = true;
        this.queryType = "Query";
        this.searchFieldType = "string";
        this.distinct = false;
        this.sessionFactory = "sessionFactory";
    }
    
    @Override
    public void init() {
        if (!this.queryOnly && this.supportRemote) {
            final List<DictionaryItem> ls = this.initAllItems();
            for (final DictionaryItem di : ls) {
                this.addItem(di);
            }
            if (!StringUtils.isEmpty((CharSequence)this.parent)) {
                this.initNodeToFolder(ls);
            }
        }
    }
    
    public List<DictionaryItem> initAllItems() {
        final List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
        final SessionFactory sf = ApplicationContextHolder.getBean(this.sessionFactory, SessionFactory.class);
        Session ss = null;
        try {
            ss = sf.openSession();
            final String sql = this.spellSql();
            final Query q = this.createQuery(ss, sql);
            final List<Object[]> records = (List<Object[]>)q.list();
            for (final Object[] r : records) {
                final DictionaryItem dictionaryItem = this.parseDicItem(r);
                ls.add(dictionaryItem);
            }
        }
        catch (Exception e) {
            TableDictionary.LOGGER.error("init dic[{}] items failed, db error \r ", this.id, e);
        }
        finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
        return ls;
    }
    
    @Override
    public List<DictionaryItem> getSlice(final String parentKey, final int sliceType, final String query) {
        if (!StringUtils.isEmpty((CharSequence)query)) {
            return this.query(parentKey, query);
        }
        switch (sliceType) {
            case 0: {
                return this.getAllItems(parentKey);
            }
            case 1: {
                return this.getAllLeaf(parentKey);
            }
            case 3: {
                return this.getAllChild(parentKey);
            }
            default: {
                return null;
            }
        }
    }
    
    public List<DictionaryItem> query(final String parentKey, String qs) {
        final List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
        String curSF = this.ignoreSearchFieldExPrefix ? this.searchFieldEx : this.searchField;
        qs = qs.toLowerCase();
        if (qs.charAt(0) == this.searchKeySymbol) {
            curSF = this.keyField;
            qs = qs.substring(1);
        }
        else if (qs.charAt(0) == this.searchExSymbol) {
            curSF = this.searchFieldEx;
            qs = qs.substring(1);
        }
        final SessionFactory sf = ApplicationContextHolder.getBean(this.sessionFactory, SessionFactory.class);
        Session ss = null;
        try {
            ss = sf.openSession();
            StringBuffer condition = new StringBuffer("lower(").append(curSF).append(")");
            if (qs.startsWith("=")) {
                qs = qs.substring(1);
                if (curSF.equals(this.searchField) && DataTypes.isNumberType(this.searchFieldType)) {
                    condition = new StringBuffer(curSF).append("=").append(qs);
                }
                else {
                    condition.append("='").append(qs).append("'");
                }
            }
            else {
                condition.append(" like '").append(qs).append("%'");
            }
            if (!StringUtils.isEmpty((CharSequence)parentKey)) {
                condition.append(" and substring(").append(this.searchField).append(",1,").append(parentKey.length()).append(")='").append(parentKey).append("'");
            }
            final String sql = this.spellSql(condition.toString());
            final Query q = this.createQuery(ss, sql);
            final List<Object[]> records = (List<Object[]>)q.list();
            for (int rowCount = records.size(), i = 0; i < rowCount; ++i) {
                final Object[] r = records.get(i);
                final DictionaryItem di = this.parseDicItem(r);
                ls.add(di);
            }
        }
        catch (Exception e) {
            TableDictionary.LOGGER.error("query failed from table dic:{}", this.id, e);
        }
        finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
        return ls;
    }
    
    protected List<DictionaryItem> getAllItemsFromCacheList(final String parentKey) {
        final List<DictionaryItem> list = this.itemsList();
        if (StringUtils.isEmpty((CharSequence)parentKey)) {
            return list;
        }
        final List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
        for (final DictionaryItem di : list) {
            final String p = (String)di.getProperty("parent");
            if (!StringUtils.isEmpty((CharSequence)p) && p.contains(parentKey)) {
                ls.add(di);
            }
        }
        return ls;
    }
    
    protected List<DictionaryItem> getAllChildFromCacheList(final String parentKey) {
        return this.getAllChildFromCacheList(parentKey, this.itemsList());
    }
    
    protected List<DictionaryItem> getAllChildFromCacheList(final String parentKey, final List<DictionaryItem> itemList) {
        if (this.codeRule == null && StringUtils.isEmpty((CharSequence)this.parent)) {
            return itemList;
        }
        final List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
        if (StringUtils.isEmpty((CharSequence)parentKey)) {
            if (this.codeRule != null) {
                final int firstCodeLength = this.codeRule.getLayerLength(0);
                for (final DictionaryItem di : itemList) {
                    if (di.getKey().length() == firstCodeLength) {
                        ls.add(di);
                    }
                }
            }
            else if (!StringUtils.isEmpty((CharSequence)this.parent)) {
                for (final DictionaryItem di2 : itemList) {
                    final String p = (String)di2.getProperty("parent");
                    if (StringUtils.isEmpty((CharSequence)p) || this.getItem(p) == null) {
                        ls.add(di2);
                    }
                }
            }
            return ls;
        }
        for (final DictionaryItem di2 : itemList) {
            final String p = (String)di2.getProperty("parent");
            if (!StringUtils.isEmpty((CharSequence)p) && parentKey.equals(p)) {
                ls.add(di2);
            }
        }
        return ls;
    }
    
    protected List<DictionaryItem> getAllLeafFromCacheList(final String parentKey) {
        final List<DictionaryItem> list = this.itemsList();
        if (StringUtils.isEmpty((CharSequence)parentKey)) {
            return list;
        }
        final List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
        for (final DictionaryItem di : list) {
            final String p = (String)di.getProperty("parent");
            final String f = (String)di.getProperty("folder");
            if (StringUtils.isEmpty((CharSequence)f) && !StringUtils.isEmpty((CharSequence)p) && p.contains(parentKey)) {
                ls.add(di);
            }
        }
        return ls;
    }
    
    protected List<DictionaryItem> getAllLeaf(final String parentKey) {
        if (!this.queryOnly) {
            this.checkItems();
            return this.getAllLeafFromCacheList(parentKey);
        }
        return this.initAllItems();
    }
    
    protected List<DictionaryItem> getAllItems(final String parentKey) {
        if (!this.queryOnly) {
            this.checkItems();
            return this.getAllItemsFromCacheList(parentKey);
        }
        final List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
        if (this.codeRule == null) {
            return ls;
        }
        if (StringUtils.isEmpty((CharSequence)parentKey)) {
            ls.addAll(this.initAllItems());
        }
        else {
            final int curLayer = this.codeRule.indexOfLayer(parentKey);
            if (curLayer == -1 || curLayer == this.codeRule.getLayerCount() - 1) {
                return ls;
            }
            final int curLen = this.codeRule.getLayerLength(curLayer);
            final String condition = new StringBuffer("substring(").append(this.keyField).append(",1,:curLen)=:parentKey").toString();
            final String sql = this.spellSql(condition);
            final SessionFactory sf = ApplicationContextHolder.getBean(this.sessionFactory, SessionFactory.class);
            Session ss = null;
            try {
                ss = sf.openSession();
                final Query q = this.createQuery(ss, sql);
                q.setInteger("curLen", curLen);
                q.setString("parentKey", parentKey);
                final List<Object[]> records = (List<Object[]>)q.list();
                for (final Object[] r : records) {
                    ls.add(this.parseDicItem(r));
                }
            }
            catch (Exception e) {
                TableDictionary.LOGGER.error("Get dictionary {} item failed.", this.id, e);
            }
            finally {
                if (ss.isOpen()) {
                    ss.close();
                }
            }
        }
        return ls;
    }
    
    protected List<DictionaryItem> getAllChild(final String parentKey) {
        if (!this.queryOnly) {
            this.checkItems();
            return this.getAllChildFromCacheList(parentKey);
        }
        if (this.codeRule != null) {
            final List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
            final SessionFactory sf = ApplicationContextHolder.getBean(this.sessionFactory, SessionFactory.class);
            Session ss = null;
            try {
                ss = sf.openSession();
                Query q = null;
                if (StringUtils.isEmpty((CharSequence)parentKey)) {
                    final String condition = new StringBuffer("length(").append(this.keyField).append(")=:len").toString();
                    final String sql = this.spellSql(condition);
                    q = this.createQuery(ss, sql);
                    q.setInteger("len", this.codeRule.getLayerLength(0));
                }
                else {
                    final int curLayer = this.codeRule.indexOfLayer(parentKey);
                    if (curLayer == -1 || curLayer == this.codeRule.getLayerCount() - 1) {
                        return ls;
                    }
                    final int curLen = this.codeRule.getLayerLength(curLayer);
                    final int nextLen = this.codeRule.getLayerLength(curLayer + 1);
                    final String condition2 = new StringBuffer("length(").append(this.keyField).append(")=:nextLen and substring(").append(this.keyField).append(",1,:curLen)=:parentKey").toString();
                    final String sql2 = this.spellSql(condition2);
                    q = this.createQuery(ss, sql2);
                    q.setInteger("curLen", curLen);
                    q.setInteger("nextLen", nextLen);
                    q.setString("parentKey", parentKey);
                }
                final List<Object[]> records = (List<Object[]>)q.list();
                for (final Object[] r : records) {
                    ls.add(this.parseDicItem(r));
                }
            }
            catch (Exception e) {
                TableDictionary.LOGGER.error("Failed to initialize dictionary {}.", this.id, e);
            }
            finally {
                if (ss.isOpen()) {
                    ss.close();
                }
            }
            return ls;
        }
        if (!StringUtils.isEmpty((CharSequence)this.parent)) {
            if (StringUtils.isEmpty((CharSequence)parentKey)) {
                synchronized (this) {
                    final List<DictionaryItem> itemList = this.initAllItems();
                    this.initNodeToFolder(itemList);
                    final List<DictionaryItem> ls2 = this.getAllChildFromCacheList(parentKey, itemList);
                    return ls2;
                }
            }
            return this.getItemsFromDBByParentKey(parentKey);
        }
        return this.initAllItems();
    }
    
    protected String spellSql() {
        return this.spellSql(null);
    }
    
    protected String spellSql(final String condition) {
        final StringBuffer props = new StringBuffer();
        if (this.propFields != null) {
            final Set<String> fields = this.propFields.keySet();
            for (final Object fld : fields) {
                props.append(",").append(fld);
            }
        }
        final StringBuffer sql = new StringBuffer("select ").append(this.distinct ? "distinct " : "");
        sql.append(this.keyField).append(",").append(this.textField).append(props);
        if (!StringUtils.isEmpty((CharSequence)this.parent)) {
            sql.append(",").append(this.parent);
        }
        sql.append(" from ").append(this.entityName);
        if (this.queryOnly) {
            try {
                this.setFilter(this.filter);
            }
            catch (ExprException ex) {}
        }
        String cnd = this.where;
        if (!StringUtils.isEmpty((CharSequence)condition)) {
            cnd = (StringUtils.isEmpty((CharSequence)this.where) ? " where " : (this.where + " and "));
            cnd += condition;
        }
        sql.append(cnd);
        if (this.sortField != null) {
            sql.append(" order by ").append(this.sortField);
        }
        return sql.toString();
    }
    
    protected DictionaryItem parseDicItem(final Object[] r) {
        final String key = String.valueOf(r[0]);
        final String text = String.valueOf(r[1]);
        final DictionaryItem dictionaryItem = new DictionaryItem(key, text);
        if (this.propFields != null) {
            final Set<String> ps = this.propFields.keySet();
            int i = 2;
            for (final String p : ps) {
                if (r[i] != null) {
                    dictionaryItem.setProperty(StringUtils.isEmpty((CharSequence)this.propFields.get(p)) ? p : this.propFields.get(p), r[i]);
                }
                ++i;
            }
        }
        if (!StringUtils.isEmpty((CharSequence)this.iconCls)) {
            dictionaryItem.setProperty("iconCls", this.iconCls);
        }
        if (this.codeRule != null) {
            final String parentKey = this.codeRule.getParentKey(key);
            dictionaryItem.setProperty("parent", parentKey);
            if (this.codeRule.isLeaf(key)) {
                dictionaryItem.setLeaf(true);
            }
        }
        else if (!StringUtils.isEmpty((CharSequence)this.parent)) {
            final String parentKey = (r[r.length - 1] == null) ? null : String.valueOf(r[r.length - 1]);
            if (!StringUtils.isEmpty((CharSequence)parentKey)) {
                dictionaryItem.setProperty("parent", parentKey);
            }
        }
        else {
            dictionaryItem.setLeaf(true);
        }
        return dictionaryItem;
    }
    
    public List<DictionaryItem> getItemFromDBByText(final String text) {
        List<DictionaryItem> dicItemList = null;
        final String con = new StringBuffer(this.textField).append("=:text").toString();
        final String sql = this.spellSql(con);
        final SessionFactory sf = ApplicationContextHolder.getBean(this.sessionFactory, SessionFactory.class);
        Session ss = null;
        try {
            ss = sf.openSession();
            final Query q = this.createQuery(ss, sql);
            q.setString("text", text);
            final List<Object[]> l = (List<Object[]>)q.list();
            if (l.size() > 0) {
                dicItemList = new ArrayList<DictionaryItem>();
                for (int i = 0; i < l.size(); ++i) {
                    dicItemList.add(this.parseDicItem(l.get(i)));
                }
            }
        }
        catch (Exception e) {
            TableDictionary.LOGGER.error("get {} dicItem by text[" + text + "] failed.", this.id, e);
        }
        finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
        return dicItemList;
    }
    
    public DictionaryItem getItemFromDB(final String key) {
        DictionaryItem dictionaryItem = null;
        final String con = new StringBuffer(this.keyField).append("=:key").toString();
        final String sql = this.spellSql(con);
        final SessionFactory sf = ApplicationContextHolder.getBean(this.sessionFactory, SessionFactory.class);
        Session ss = null;
        try {
            ss = sf.openSession();
            final Query q = this.createQuery(ss, sql);
            q.setString("key", key);
            final List<Object[]> l = (List<Object[]>)q.list();
            if (l.size() > 0) {
                dictionaryItem = this.parseDicItem(l.get(0));
            }
        }
        catch (Exception e) {
            TableDictionary.LOGGER.error("get {} dicItem by key[" + key + "] failed.", this.id, e);
        }
        finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
        return dictionaryItem;
    }
    
    public List<DictionaryItem> getItemsFromDB(final String keys) {
        final List<DictionaryItem> list = new ArrayList<DictionaryItem>();
        final String con = new StringBuffer(this.keyField).append(" in (").append(keys).append(")").toString();
        final String sql = this.spellSql(con);
        final SessionFactory sf = ApplicationContextHolder.getBean(this.sessionFactory, SessionFactory.class);
        Session ss = null;
        try {
            ss = sf.openSession();
            final Query q = this.createQuery(ss, sql);
            final List<Object[]> l = (List<Object[]>)q.list();
            if (l.size() > 0) {
                for (final Object[] o : l) {
                    final DictionaryItem dictionaryItem = this.parseDicItem(o);
                    list.add(dictionaryItem);
                }
            }
        }
        catch (Exception e) {
            TableDictionary.LOGGER.error("get {} dicItems by keys[" + keys + "] failed.", this.id, e);
        }
        finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
        return list;
    }
    
    public List<DictionaryItem> getItemsFromDBByParentKey(final String parentKey) {
        final List<DictionaryItem> list = new ArrayList<DictionaryItem>();
        String p = this.parent;
        final int index = this.parent.toLowerCase().lastIndexOf(" as ");
        if (index > -1) {
            p = this.parent.substring(0, index);
        }
        final String con = new StringBuffer(p).append("='").append(parentKey).append("'").toString();
        final String sql = this.spellSql(con);
        final SessionFactory sf = ApplicationContextHolder.getBean(this.sessionFactory, SessionFactory.class);
        Session ss = null;
        try {
            ss = sf.openSession();
            final Query q = this.createQuery(ss, sql);
            final List<Object[]> l = (List<Object[]>)q.list();
            if (l.size() > 0) {
                for (final Object[] o : l) {
                    final DictionaryItem dictionaryItem = this.parseDicItem(o);
                    if (!this.folders.contains(dictionaryItem.getKey())) {
                        dictionaryItem.setLeaf(true);
                    }
                    list.add(dictionaryItem);
                }
            }
        }
        catch (Exception e) {
            TableDictionary.LOGGER.error("get {} dicItems by parentKey[" + parentKey + "] failed.", this.id, e);
        }
        finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
        return list;
    }
    
    private Query createQuery(final Session ss, final String q) {
        if ("SQLQuery".equals(this.queryType)) {
            return (Query)ss.createSQLQuery(q);
        }
        return ss.createQuery(q);
    }
    
    @Override
    public List<String> getKey(final String text) {
        if (!this.queryOnly) {
            this.checkItems();
            return super.getKey(text);
        }
        final List<String> list = new ArrayList<String>();
        final List<DictionaryItem> li = this.getItemFromDBByText(text);
        if (li != null) {
            for (final DictionaryItem d : li) {
                list.add(d.getKey());
            }
        }
        return list;
    }
    
    @Override
    public String getText(final String key) {
        if (!this.queryOnly) {
            final DictionaryItem di = this.getItem(key);
            return (di == null) ? "" : di.getText();
        }
        final DictionaryItem di = this.getItemFromDB(key);
        if (di != null) {
            return di.getText();
        }
        return "";
    }
    
    public String getWholeText(final String key, final int includeParentMinLen) {
        final StringBuffer text = new StringBuffer();
        final StringBuffer sb = new StringBuffer();
        sb.append(",'").append(key).append("'");
        for (String pkey = this.codeRule.getParentKey(key); !StringUtils.isEmpty((CharSequence)pkey) && pkey.length() >= includeParentMinLen; pkey = this.codeRule.getParentKey(pkey)) {
            sb.append(",'").append(pkey).append("'");
        }
        final List<DictionaryItem> list = this.getItemsFromDB(sb.substring(1));
        Collections.sort(list, new Comparator<DictionaryItem>() {
            @Override
            public int compare(final DictionaryItem d1, final DictionaryItem d2) {
                if (d1.getKey().length() > d2.getKey().length()) {
                    return 1;
                }
                return -1;
            }
        });
        for (int i = 0; i < list.size(); ++i) {
            final DictionaryItem di = list.get(i);
            if (i + 1 < list.size()) {
                final DictionaryItem dd = list.get(i + 1);
                if (dd.getText().contains(di.getText())) {
                    di.setProperty("___removeText___", "1");
                }
            }
        }
        for (int i = 0; i < list.size(); ++i) {
            final DictionaryItem di = list.get(i);
            if (!di.getProperty("___removeText___").equals("1")) {
                text.append(di.getText());
            }
        }
        return text.toString();
    }
    
    public boolean hasCodeRule() {
        return this.codeRule != null;
    }
    
    private void initNodeToFolder(final List<DictionaryItem> list) {
        this.folders = new HashSet<String>();
        for (final DictionaryItem di : list) {
            final String p = (String)di.getProperty("parent");
            final DictionaryItem pdi = this.getItem(p);
            if (pdi != null) {
                this.folders.add(pdi.getKey());
            }
        }
        for (final DictionaryItem dictionaryItem : list) {
            if (!this.folders.contains(dictionaryItem.getKey())) {
                dictionaryItem.setLeaf(true);
            }
        }
    }
    
    @Override
    public DictionaryItem getItem(final String key) {
        if (!this.queryOnly) {
            this.checkItems();
            return super.getItem(key);
        }
        return this.getItemFromDB(key);
    }
    
    private void checkItems() {
        if (!this.supportRemote && this.getItems().size() == 0) {
            final List<DictionaryItem> ls = this.initAllItems();
            for (final DictionaryItem di : ls) {
                this.addItem(di);
            }
            if (!StringUtils.isEmpty((CharSequence)this.parent)) {
                this.initNodeToFolder(ls);
            }
        }
    }
    
    public void setParent(final String parent) {
        this.parent = parent;
    }
    
    public void setSearchFieldType(final String searchFieldType) {
        this.searchFieldType = searchFieldType;
    }
    
    public void setEntityName(final String entityName) {
        this.entityName = entityName;
    }
    
    public String getEntityName() {
        return this.entityName;
    }
    
    public String getParent() {
        return this.parent;
    }
    
    public String getKeyField() {
        return this.keyField;
    }
    
    public String getTextField() {
        return this.textField;
    }
    
    public String getSortField() {
        return this.sortField;
    }
    
    public String getIconCls() {
        return this.iconCls;
    }
    
    public String getFilter() {
        return this.filter;
    }
    
    @Deprecated
    public void setEntry(final String entry) {
        this.setEntityName(entry);
    }
    
    @Deprecated
    public String getEntry() {
        return this.getEntityName();
    }
    
    public void setKeyField(final String keyField) {
        this.keyField = keyField;
    }
    
    public void setTextField(final String textField) {
        this.textField = textField;
    }
    
    public void setSortField(final String sortField) {
        this.sortField = sortField;
    }
    
    public void setIgnoreSearchFieldExPrefix(final boolean ignoreSearchFieldExPrefix) {
        this.ignoreSearchFieldExPrefix = ignoreSearchFieldExPrefix;
    }
    
    public void setIconCls(final String iconCls) {
        this.iconCls = iconCls;
    }
    
    public void setQueryType(final String queryType) {
        this.queryType = queryType;
    }
    
    public boolean isSupportRemote() {
        return this.supportRemote;
    }
    
    public void setSupportRemote(final boolean supportRemote) {
        this.supportRemote = supportRemote;
    }
    
    public boolean isDistinct() {
        return this.distinct;
    }
    
    public void setDistinct(final boolean distinct) {
        this.distinct = distinct;
    }
    
    public void setCodeRule(final String sCodeRule) {
        if (!StringUtils.isEmpty((CharSequence)sCodeRule)) {
            this.codeRule = new CodeRule(sCodeRule);
        }
    }
    
    public void setFilter(final String filter) throws ExprException {
        if (!StringUtils.isEmpty((CharSequence)filter)) {
            this.filter = filter;
            final List<?> exp = JSONUtils.parse(filter,List.class);
            this.where = " where " + ExpressionProcessor.instance().toString(exp);
        }
    }
    
    public void setPropField(final String nm, final String v) {
        if (this.propFields == null) {
            this.propFields = new LinkedHashMap<String, String>();
        }
        this.propFields.put(nm, v);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)TableDictionary.class);
    }
}
