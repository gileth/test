// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.service;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.io.Serializable;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.dao.BaseDAO;
import org.springframework.stereotype.Service;

@Service("baseService")
public class BaseService
{
    @Autowired
    protected BaseDAO dao;
    
    @Transactional(readOnly = true)
    public <T> List<T> findByHql(final String hql, final Map<String, Object> properties, final int pageSize, final int pageNo) {
        return this.dao.findByHqlPaging(hql, properties, pageSize, pageNo);
    }
    
    @Transactional(readOnly = true)
    public <T> List<T> find(final Class<T> cls, final Map<String, Object> properties, final int pageSize, final int pageNo, final String orderInfo) {
        return this.dao.find(cls, properties, pageSize, pageNo, orderInfo);
    }
    
    @Transactional(readOnly = true)
    public <T> List<T> findByProperties(final Class<T> cls, final Map<String, Object> properties) {
        return this.dao.findByProperties(cls, properties);
    }
    
    @Transactional(readOnly = true)
    public <T> List<T> findByExample(final Class<T> cls, final T instance) {
        return this.dao.findByExample(cls, instance);
    }
    
    @Transactional(readOnly = true)
    public <T> List<T> findByProperty(final Class<T> cls, final String propertyName, final Object value) {
        return this.dao.findByProperty(cls, propertyName, value);
    }
    
    @Transactional(readOnly = true)
    public <T> List<T> findByProperty(final Class<T> cls, final String propertyName, final Object value, final String orderInfo) {
        return this.dao.findByProperty(cls, propertyName, value, orderInfo);
    }
    
    @Transactional(readOnly = true)
    public <T> T get(final Class<T> cls, final Serializable id) {
        return this.dao.get(cls, id);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public <T> void update(final Class<T> cls, final T t) {
        this.dao.update(cls, t);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public <T> void merge(final Class<T> cls, final T t) {
        this.dao.merge(cls, t);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public <T> void delete(final Class<T> cls, final T t) {
        this.dao.delete(cls, t);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public <T> void save(final Class<T> cls, final T t) {
        this.dao.save(cls, t);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int executeUpdate(final String hql, final Map<String, Object> pramas) {
        return this.dao.executeUpdate(hql, pramas);
    }
    
    @Transactional(readOnly = true)
    public <T> long count(final Class<T> cls, final Map<String, Object> properties) {
        return this.dao.count(cls, properties);
    }
    
    @Transactional(readOnly = true)
    public long count(final String hql, final Map<String, Object> map) {
        return this.dao.count(hql, map);
    }
    
    @Transactional(readOnly = true)
    public <T> T getUnique(final Class<T> cls, final String propertyName, final Object value) {
        return this.dao.getUnique(cls, propertyName, value);
    }
    
    @Transactional(readOnly = true)
    public <T> Map entityPagingByProperties(final Class<T> cls, final Map<String, Object> properties, final int pageSize, final int pageNo, final String orderInfo) {
        final List<T> list = this.dao.find(cls, properties, pageSize, pageNo, orderInfo);
        long total = 0L;
        if (list != null && list.size() > 0) {
            total = this.dao.count(cls, properties);
        }
        final HashMap result = new HashMap();
        result.put("data", list);
        result.put("total", total);
        return result;
    }
    
    @Transactional(readOnly = true)
    public <T> Map hqlPagingByProperties(final String hql, final Map<String, Object> properties, final int pageSize, final int pageNo, final String orderInfo) {
        final List<T> list = this.dao.findByHqlPaging(StringUtils.isNotEmpty((CharSequence)orderInfo) ? (hql + " order by " + orderInfo) : hql, properties, pageSize, pageNo);
        long total = 0L;
        if (list != null && list.size() > 0) {
            if (hql.indexOf("select ") != -1) {
                total = this.dao.count(new StringBuffer("select count(*) ").append(hql.substring(hql.indexOf(" from "))).toString(), properties);
            }
            else {
                total = this.dao.count(new StringBuffer("select count(*) ").append(hql).toString(), properties);
            }
        }
        final HashMap result = new HashMap();
        result.put("data", list);
        result.put("total", total);
        return result;
    }
}
