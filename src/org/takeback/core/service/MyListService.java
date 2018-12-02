// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.service;

import java.io.Serializable;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.util.converter.ConversionUtils;
import java.util.List;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.dao.BaseDAO;
import org.springframework.stereotype.Service;

@Service("myListService")
public class MyListService
{
    @Autowired
    protected BaseDAO dao;
    protected static String CMD;
    protected static String PAGE;
    protected static String START;
    protected static String LIMIT;
    protected static String ID;
    protected static String CND;
    protected static String ORDERINFO;
    public static String ENTITYNAME;
    
    @Transactional(readOnly = true)
    public Map<String, Object> list(final Map<String, Object> req) {
        final String entityName = req.get(MyListService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final int limit = req.get(MyListService.LIMIT);
        final int page = req.get(MyListService.PAGE);
        final List<?> cnd = ConversionUtils.convert(req.get(MyListService.CND), (Class<List<?>>)List.class);
        String filter = null;
        if (cnd != null) {
            filter = ExpressionProcessor.instance().toString(cnd);
        }
        final String orderInfo = req.get(MyListService.ORDERINFO);
        final List<?> ls = this.dao.query(entityName, filter, limit, page, orderInfo);
        this.afterList(ls);
        final long count = this.dao.totalSize(entityName, filter);
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalSize", count);
        result.put("body", ls);
        return result;
    }
    
    @Transactional(readOnly = true)
    public Object load(final Map<String, Object> req) {
        final String entityName = req.get(MyListService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final Object pkey = req.get("id");
        Serializable id = null;
        if (pkey instanceof Integer) {
            id = ConversionUtils.convert(pkey, Long.class);
        }
        else {
            id = ConversionUtils.convert(pkey, String.class);
        }
        final Object entity = this.dao.get(entityName, id);
        this.afterLoad(entity);
        return entity;
    }
    
    @Transactional
    public void delete(final Map<String, Object> req) {
        final String entityName = req.get(MyListService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final Object pkey = req.get("id");
        Serializable id = null;
        if (pkey instanceof Integer) {
            id = ConversionUtils.convert(pkey, Long.class);
        }
        else {
            id = ConversionUtils.convert(pkey, String.class);
        }
        this.dao.delete(entityName, id);
    }
    
    @Transactional
    public void save(final Map<String, Object> req) {
        final String entityName = req.get(MyListService.ENTITYNAME);
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final Map<String, Object> data = req.get("data");
        this.beforeProcessSaveData(data);
        try {
            final Class<?> cls = Class.forName(entityName);
            final Object obj = ConversionUtils.convert(data, cls);
            this.beforeSave(obj);
            this.dao.getSession().saveOrUpdate(obj);
        }
        catch (ClassNotFoundException e) {
            throw new CodedBaseRuntimeException(510, "parse class[" + entityName + "] failed");
        }
    }
    
    protected void beforeProcessSaveData(final Map<String, Object> data) {
    }
    
    protected void beforeSave(final Object obj) {
    }
    
    protected void afterList(final List<?> ls) {
    }
    
    protected void afterLoad(final Object entity) {
    }
    
    static {
        MyListService.CMD = "cmd";
        MyListService.PAGE = "page";
        MyListService.START = "start";
        MyListService.LIMIT = "limit";
        MyListService.ID = "id";
        MyListService.CND = "cnd";
        MyListService.ORDERINFO = "orderInfo";
        MyListService.ENTITYNAME = "entityName";
    }
}
