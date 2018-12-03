// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.dao;

import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.Query;
import java.io.Serializable;
import org.takeback.dao.exception.DaoException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

@Repository("baseDao")
public class BaseDAO implements IBaseDAO
{
    private static final Logger log;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NO = 1;
    @Autowired
    protected SessionFactory sessionFactory;
    
    @Override
    public Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }
    
    public Session getNewSession() {
        return this.sessionFactory.openSession();
    }
    
    public <T> void save(final Class<T> cls, final T t) {
        BaseDAO.log.debug("saving {} instance", cls.getSimpleName());
        try {
            this.getSession().save(t);
        }
        catch (DaoException re) {
            BaseDAO.log.error("saving {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }
    
    public <T> void delete(final Class<T> cls, final T t) {
        BaseDAO.log.debug("deleting {} instance", cls.getSimpleName());
        try {
            this.getSession().delete(t);
        }
        catch (DaoException re) {
            BaseDAO.log.error("deleting {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }
    
    public <T> void delete(final String entityName, final Serializable id) {
        BaseDAO.log.debug("deleting {} instance", entityName);
        try {
            this.getSession().delete(entityName, this.get(entityName, id));
        }
        catch (DaoException re) {
            BaseDAO.log.error("deleting {} instance failed", entityName, re);
            throw re;
        }
    }
    
    public <T> void update(final Class<T> cls, final T t) {
        BaseDAO.log.debug("updating {} instance", cls.getSimpleName());
        try {
            this.getSession().update(t);
        }
        catch (DaoException re) {
            BaseDAO.log.error("updating {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }
    
    public <T> void merge(final Class<T> cls, final T t) {
        BaseDAO.log.debug("merge {} instance", cls.getSimpleName());
        try {
            this.getSession().merge(t);
        }
        catch (DaoException re) {
            BaseDAO.log.error("merge {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }
    
    public <T> void saveOrUpdate(final Class<T> cls, final T t) {
        BaseDAO.log.debug("saving or updating {} instance", cls.getSimpleName());
        try {
            this.getSession().saveOrUpdate(t);
        }
        catch (DaoException re) {
            BaseDAO.log.error("saving or updating {} instance failed", cls.getSimpleName(), re);
            throw re;
        }
    }
    
    public <T> T get(final Class<T> cls, final Serializable id) {
        BaseDAO.log.debug("getting {} instance with id {}", cls.getSimpleName(), id);
        try {
            final T instance = (T)this.getSession().get((Class)cls, id);
            return instance;
        }
        catch (DaoException re) {
            BaseDAO.log.error("getting {} instance with id {} failed", new Object[] { cls.getSimpleName(), id, re });
            throw re;
        }
    }
    
    public <T> T get(final String entityName, final Serializable id) {
        BaseDAO.log.debug("getting {} instance with id {}", entityName, id);
        try {
            final T instance = (T)this.getSession().get(entityName, id);
            return instance;
        }
        catch (DaoException re) {
            BaseDAO.log.error("getting {} instance with id {} failed", new Object[] { entityName, id, re });
            throw re;
        }
    }
    
    public <T> T getUnique(final String hql, final Object... pramas) {
        BaseDAO.log.debug("get unique result with hql {}, with params {}", hql, pramas);
        try {
            final Query q = this.getSession().createQuery(hql);
            for (int i = 0; i < pramas.length; ++i) {
                q.setParameter(i, pramas[i]);
            }
            final T instance = (T)q.uniqueResult();
            return instance;
        }
        catch (DaoException re) {
            BaseDAO.log.error("get unique result with hql {}, with params {}", new Object[] { hql, pramas, re });
            throw re;
        }
    }
    
    public <T> T getUnique(final Class<T> cls, final String propertyName, final Object value) {
        BaseDAO.log.debug("finding {} instance with property: {}, value: {}", new Object[] { cls.getSimpleName(), propertyName, value });
        try {
            final Query queryObject = this.processHql(cls.getSimpleName(), propertyName, value, null);
            return (T)queryObject.uniqueResult();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with property: {}, value: {} falied", new Object[] { cls.getSimpleName(), propertyName, value, re });
            throw re;
        }
    }
    
    public <T> T getUniqueByProps(final Class<T> cls, final Map<String, Object> props) {
        BaseDAO.log.debug("finding {} instance with properties: {}", cls.getSimpleName(), props);
        try {
            final Query queryObject = this.processHql(cls.getSimpleName(), props, null);
            return (T)queryObject.uniqueResult();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with properties: {} falied", new Object[] { cls.getSimpleName(), props, re });
            throw re;
        }
    }
    
    public int executeUpdate(final String hql, final Object[] pramas) {
        BaseDAO.log.debug("executeUpdate with hql {}, with params {}", hql, pramas);
        try {
            final Query q = this.getSession().createQuery(hql);
            for (int i = 0; i < pramas.length; ++i) {
                q.setParameter(i, pramas[i]);
            }
            return q.executeUpdate();
        }
        catch (DaoException re) {
            BaseDAO.log.error("executeUpdate with hql {}, with params {}", new Object[] { hql, pramas, re });
            throw re;
        }
    }
    
    public int executeUpdate(final String hql, final Map<String, Object> pramas) {
        BaseDAO.log.debug("executeUpdate with hql {}, with params {}", hql, pramas);
        try {
            final Query q = this.getSession().createQuery(hql);
            for (final String k : pramas.keySet()) {
                q.setParameter(k, pramas.get(k));
            }
            return q.executeUpdate();
        }
        catch (DaoException re) {
            BaseDAO.log.error("executeUpdate with hql {}, with params {}", new Object[] { hql, pramas, re });
            throw re;
        }
    }
    
    public <T> List<T> findByProperty(final Class<T> cls, final String propertyName, final Object value) {
        BaseDAO.log.debug("finding {} instance with property: {}, value: {}", new Object[] { cls.getSimpleName(), propertyName, value });
        try {
            final Query queryObject = this.processHql(cls.getSimpleName(), propertyName, value, null);
            return (List<T>)queryObject.list();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with property: {}, value: {} falied", new Object[] { cls.getSimpleName(), propertyName, value, re });
            throw re;
        }
    }
    
    public <T> List<T> findByProperty(final Class<T> cls, final String propertyName, final Object value, final String orderInfo) {
        BaseDAO.log.debug("finding {} instance with property: {}, value: {}", new Object[] { cls.getSimpleName(), propertyName, value });
        try {
            final Query queryObject = this.processHql(cls.getSimpleName(), propertyName, value, orderInfo);
            return (List<T>)queryObject.list();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with property: {}, value: {} falied", new Object[] { cls.getSimpleName(), propertyName, value, re });
            throw re;
        }
    }
    
    public <T> List<T> findByExample(final Class<T> cls, final T instance) {
        BaseDAO.log.debug("finding {} instance by example {}", cls.getSimpleName(), instance);
        try {
            final List<T> results = (List<T>)this.getSession().createCriteria((Class)cls).add((Criterion)Example.create(instance)).list();
            BaseDAO.log.debug("find by example successful, result size: " + results.size());
            return results;
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance by example {} falied", new Object[] { cls.getSimpleName(), instance, re });
            throw re;
        }
    }
    
    public <T> List<T> findByProperties(final Class<T> cls, final Map<String, Object> properties) {
        BaseDAO.log.debug("finding {} instance with properties: {}", cls.getSimpleName(), properties);
        try {
            final Query queryObject = this.processHql(cls.getSimpleName(), properties, null);
            return (List<T>)queryObject.list();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with properties: {} failed", new Object[] { cls.getSimpleName(), properties, re });
            throw re;
        }
    }
    
    public <T> List<T> findByProperties(final Class<T> cls, final Map<String, Object> properties, final String orderInfo) {
        BaseDAO.log.debug("finding {} instance with properties: {}", cls.getSimpleName(), properties);
        try {
            final Query queryObject = this.processHql(cls.getSimpleName(), properties, orderInfo);
            return (List<T>)queryObject.list();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with properties: {} failed", new Object[] { cls.getSimpleName(), properties, re });
            throw re;
        }
    }
    
    public <T> List<T> find(final String entityName, final Map<String, Object> properties, int pageSize, int pageNo, final String orderInfo) {
        BaseDAO.log.debug("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", new Object[] { entityName, properties, orderInfo, pageSize, pageNo });
        try {
            final Query queryObject = this.processHql(entityName, properties, orderInfo);
            pageSize = ((pageSize < 1) ? 10 : pageSize);
            pageNo = ((pageNo < 1) ? 1 : pageNo);
            queryObject.setFirstResult(pageSize * (pageNo - 1)).setMaxResults(pageSize);
            return (List<T>)queryObject.list();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", new Object[] { entityName, properties, orderInfo, pageSize, pageNo, re });
            throw re;
        }
    }
    
    public <T> List<T> find(final Class<T> cls, final Map<String, Object> properties, final int pageSize, final int pageNo, final String orderInfo) {
        return this.find(cls.getSimpleName(), properties, pageSize, pageNo, orderInfo);
    }
    
    public <T> List<T> findByProperties(final Class<T> cls, final Map<String, Object> properties, final String... columns) {
        BaseDAO.log.debug("finding {} instance with properties: {}", cls.getSimpleName(), properties);
        try {
            final StringBuilder queryString = new StringBuilder("select ");
            int columnsize = columns.length;
            for (final String column : columns) {
                queryString.append("model.").append(column);
                if (columnsize > 1) {
                    queryString.append(",");
                }
                --columnsize;
            }
            queryString.append(" from ").append(cls.getSimpleName()).append(" as model");
            queryString.append(" where ");
            int size = properties.size();
            final List<Object> values = new ArrayList<Object>(size);
            for (final String prop : properties.keySet()) {
                queryString.append("model.").append(prop).append("= ?");
                values.add(properties.get(prop));
                if (size > 1) {
                    queryString.append(" and ");
                }
                --size;
            }
            final Query queryObject = this.getSession().createQuery(queryString.toString());
            for (int i = 0; i < values.size(); ++i) {
                queryObject.setParameter(i, values.get(i));
            }
            return (List<T>)queryObject.list();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with properties: {} failed", new Object[] { cls.getSimpleName(), properties, re });
            throw re;
        }
    }
    
    public <T> List<T> find(final Class<T> cls, final Map<String, Object> properties, int pageSize, int pageNo, final String orderInfo, final String... columns) {
        BaseDAO.log.debug("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", new Object[] { cls.getSimpleName(), properties, orderInfo, pageSize, pageNo });
        try {
            final StringBuilder queryString = new StringBuilder("select ");
            int columnsize = columns.length;
            for (final String column : columns) {
                queryString.append("model.").append(column);
                if (columnsize > 1) {
                    queryString.append(",");
                }
                --columnsize;
            }
            queryString.append(" from ").append(cls.getSimpleName()).append(" as model");
            List<Object> values = null;
            if (properties != null && properties.size() > 0) {
                queryString.append(" where ");
                int size = properties.size();
                values = new ArrayList<Object>(size);
                for (final String prop : properties.keySet()) {
                    queryString.append("model.").append(prop).append("= ?");
                    values.add(properties.get(prop));
                    if (size > 1) {
                        queryString.append(" and ");
                    }
                    --size;
                }
            }
            if (!StringUtils.isEmpty((CharSequence)orderInfo)) {
                queryString.append(" order by ").append(orderInfo);
            }
            final Query queryObject = this.getSession().createQuery(queryString.toString());
            if (values != null) {
                for (int i = 0; i < values.size(); ++i) {
                    queryObject.setParameter(i, values.get(i));
                }
            }
            pageSize = ((pageSize < 1) ? 10 : pageSize);
            pageNo = ((pageNo < 1) ? 1 : pageNo);
            queryObject.setFirstResult(pageSize * (pageNo - 1)).setMaxResults(pageSize);
            return (List<T>)queryObject.list();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", new Object[] { cls.getSimpleName(), properties, orderInfo, pageSize, pageNo, re });
            throw re;
        }
    }
    
    public <T> List<T> findByHql(final String hql) {
        return this.findByHql(hql, null);
    }
    
    public <T> List<T> findByHql(final String hql, final Map<String, Object> properties) {
        final Query query = this.getSession().createQuery(hql);
        if (properties != null && !properties.isEmpty()) {
            for (final String key : properties.keySet()) {
                final Object obj = properties.get(key);
                this.setParameter(query, key, obj);
            }
        }
        return (List<T>)query.list();
    }
    
    public <T> List<T> findByHqlPaging(final String hql, final int pagesize, final int pageno) {
        return (List<T>)this.getSession().createQuery(hql).setFirstResult(pagesize * (pageno - 1)).setMaxResults(pagesize).list();
    }
    
    public <T> List<T> findByHqlPaging(final String hql, final Object[] params, final int pagesize, final int pageno) {
        final Query query = this.getSession().createQuery(hql);
        if (params.length > 0) {
            int i = 0;
            for (final Object param : params) {
                query.setParameter(i, param);
                ++i;
            }
        }
        query.setFirstResult(pagesize * (pageno - 1));
        query.setMaxResults(pagesize);
        return (List<T>)query.list();
    }
    
    public <T> List<T> findByHqlPaging(final String hql, final Map<String, Object> map, final int pagesize, final int pageno) {
        final Query query = this.getSession().createQuery(hql);
        if (map != null && !map.isEmpty()) {
            for (final String key : map.keySet()) {
                final Object obj = map.get(key);
                this.setParameter(query, key, obj);
            }
        }
        if (pagesize > -1) {
            query.setFirstResult(pagesize * (pageno - 1));
            query.setMaxResults(pagesize);
        }
        return (List<T>)query.list();
    }
    
    public <T> long count(final Class<T> cls, final Map<String, Object> properties) {
        final StringBuilder queryString = new StringBuilder("SELECT COUNT(*) FROM ").append(cls.getSimpleName()).append(" AS MODEL");
        if (properties != null && properties.size() > 0) {
            int size = properties.size();
            queryString.append(" WHERE ");
            for (final String prop : properties.keySet()) {
                queryString.append("MODEL.").append(prop).append("= :").append(prop);
                if (size > 1) {
                    queryString.append(" AND ");
                }
                --size;
            }
        }
        final Query query = this.getSession().createQuery(queryString.toString());
        if (properties != null) {
            for (final String k : properties.keySet()) {
                query.setParameter(k, properties.get(k));
            }
        }
        return (long)query.uniqueResult();
    }
    
    public long count(final String hql, final Map<String, Object> properties) {
        final Query query = this.getSession().createQuery(hql);
        if (properties != null && !properties.isEmpty()) {
            for (final String key : properties.keySet()) {
                final Object obj = properties.get(key);
                this.setParameter(query, key, obj);
            }
        }
        return (long)query.uniqueResult();
    }
    
    public long countByHql(final String hql, final Object... params) {
        final Query query = this.getSession().createQuery(hql);
        int i = 0;
        for (final Object p : params) {
            query.setParameter(i, p);
            ++i;
        }
        return (long)query.uniqueResult();
    }
    
    public <T> List<T> query(final String entityName, final String filter, int pageSize, int pageNo, final String orderInfo) {
        BaseDAO.log.debug("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", new Object[] { entityName, filter, orderInfo, pageSize, pageNo });
        try {
            final Query queryObject = this.weaveHql(null, entityName, filter, orderInfo, new Object[0]);
            pageSize = ((pageSize < 1) ? 10 : pageSize);
            pageNo = ((pageNo < 1) ? 1 : pageNo);
            queryObject.setFirstResult(pageSize * (pageNo - 1)).setMaxResults(pageSize);
            return (List<T>)queryObject.list();
        }
        catch (DaoException re) {
            BaseDAO.log.error("finding {} instance with properties: {} ,order: {}, pageSize: {}, pageNo: {}", new Object[] { entityName, filter, orderInfo, pageSize, pageNo, re });
            throw re;
        }
    }
    
    public long totalSize(final String entity, final String filter) {
        final Query query = this.weaveHql("SELECT COUNT(1)", entity, filter, null, new Object[0]);
        return (long)query.uniqueResult();
    }
    
    public Query weaveHql(final String queryColumns, final String entity, final String filter, final String orderInfo, final Object... params) {
        final StringBuilder queryString = new StringBuilder();
        if (!StringUtils.isEmpty((CharSequence)queryColumns)) {
            queryString.append(queryColumns).append(" ");
        }
        queryString.append("FROM ").append(entity).append(" AS a");
        if (!StringUtils.isEmpty((CharSequence)filter)) {
            queryString.append(" WHERE ").append(filter);
        }
        if (!StringUtils.isEmpty((CharSequence)orderInfo)) {
            queryString.append(" ORDER BY ").append(orderInfo);
        }
        final Query query = this.getSession().createQuery(queryString.toString());
        for (int i = 0; i < params.length; ++i) {
            query.setParameter(i, params[i]);
        }
        return query;
    }
    
    protected Query processHql(final String entity, final String property, final Object value, final String orderInfo) {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(property, value);
        return this.processHql(entity, properties, orderInfo);
    }
    
    protected Query processHql(final String entity, final Map<String, Object> properties, final String orderInfo) {
        final StringBuilder queryString = new StringBuilder("FROM ").append(entity).append(" AS MODEL");
        if (properties != null) {
            int size = properties.size();
            if (size > 0) {
                queryString.append(" WHERE ");
                for (final String prop : properties.keySet()) {
                    queryString.append("MODEL.").append(prop).append("= :").append(prop);
                    if (size > 1) {
                        queryString.append(" AND ");
                    }
                    --size;
                }
            }
        }
        if (!StringUtils.isEmpty((CharSequence)orderInfo)) {
            queryString.append(" ORDER BY ").append(orderInfo);
        }
        final Query query = this.getSession().createQuery(queryString.toString());
        if (properties != null) {
            for (final String k : properties.keySet()) {
                query.setParameter(k, properties.get(k));
            }
        }
        return query;
    }
    
    private void setParameter(final Query query, final String name, final Object value) {
        if (value instanceof Collection) {
            query.setParameterList(name, (Collection)value);
        }
        else if (value instanceof Object[]) {
            query.setParameterList(name, (Object[])value);
        }
        else {
            query.setParameter(name, value);
        }
    }
    
    static {
        log = LoggerFactory.getLogger((Class)BaseDAO.class);
    }
}
