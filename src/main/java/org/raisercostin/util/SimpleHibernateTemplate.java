package org.raisercostin.util;

import java.io.Serializable;
import java.util.*;

import org.hibernate.*;
import org.hibernate.classic.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class SimpleHibernateTemplate {
	private final HibernateTemplate ht;

	public SimpleHibernateTemplate(HibernateTemplate ht) {
		this.ht = ht;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		ht.setSessionFactory(sessionFactory);
	}

	public SessionFactory getSessionFactory() {
		return ht.getSessionFactory();
	}

	public void setEntityInterceptorBeanName(String entityInterceptorBeanName) {
		ht.setEntityInterceptorBeanName(entityInterceptorBeanName);
	}

	public void setAllowCreate(boolean allowCreate) {
		ht.setAllowCreate(allowCreate);
	}

	public void setEntityInterceptor(Interceptor entityInterceptor) {
		ht.setEntityInterceptor(entityInterceptor);
	}

	public Interceptor getEntityInterceptor() throws IllegalStateException, BeansException {
		return ht.getEntityInterceptor();
	}

	public boolean isAllowCreate() {
		return ht.isAllowCreate();
	}

	public void setAlwaysUseNewSession(boolean alwaysUseNewSession) {
		ht.setAlwaysUseNewSession(alwaysUseNewSession);
	}

	public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
		ht.setJdbcExceptionTranslator(jdbcExceptionTranslator);
	}

	public boolean isAlwaysUseNewSession() {
		return ht.isAlwaysUseNewSession();
	}

	public void setExposeNativeSession(boolean exposeNativeSession) {
		ht.setExposeNativeSession(exposeNativeSession);
	}

	public SQLExceptionTranslator getJdbcExceptionTranslator() {
		return ht.getJdbcExceptionTranslator();
	}

	public boolean isExposeNativeSession() {
		return ht.isExposeNativeSession();
	}

	public void setFlushModeName(String constantName) {
		ht.setFlushModeName(constantName);
	}

	public void setCheckWriteOperations(boolean checkWriteOperations) {
		ht.setCheckWriteOperations(checkWriteOperations);
	}

	public void setFlushMode(int flushMode) {
		ht.setFlushMode(flushMode);
	}

	public int getFlushMode() {
		return ht.getFlushMode();
	}

	public void setFilterName(String filter) {
		ht.setFilterName(filter);
	}

	public boolean isCheckWriteOperations() {
		return ht.isCheckWriteOperations();
	}

	public void setCacheQueries(boolean cacheQueries) {
		ht.setCacheQueries(cacheQueries);
	}

	public void setFilterNames(String[] filterNames) {
		ht.setFilterNames(filterNames);
	}

	public boolean isCacheQueries() {
		return ht.isCacheQueries();
	}

	public void setQueryCacheRegion(String queryCacheRegion) {
		ht.setQueryCacheRegion(queryCacheRegion);
	}

	public String[] getFilterNames() {
		return ht.getFilterNames();
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		ht.setBeanFactory(beanFactory);
	}

	public String getQueryCacheRegion() {
		return ht.getQueryCacheRegion();
	}

	public void afterPropertiesSet() {
		ht.afterPropertiesSet();
	}

	public void setFetchSize(int fetchSize) {
		ht.setFetchSize(fetchSize);
	}

	public int getFetchSize() {
		return ht.getFetchSize();
	}

	public void setMaxResults(int maxResults) {
		ht.setMaxResults(maxResults);
	}

	public int getMaxResults() {
		return ht.getMaxResults();
	}

	public <T> T execute(HibernateCallback<T> action) throws DataAccessException {
		return ht.execute(action);
	}

	public List<?> executeFind(HibernateCallback<?> action) throws DataAccessException {
		return ht.executeFind(action);
	}

	public <T> T executeWithNewSession(HibernateCallback<T> action) {
		return ht.executeWithNewSession(action);
	}

	public <T> T executeWithNativeSession(HibernateCallback<T> action) {
		return ht.executeWithNativeSession(action);
	}

	public DataAccessException convertHibernateAccessException(HibernateException ex) {
		return ht.convertHibernateAccessException(ex);
	}

	public <T> T get(Class<T> entityClass, Serializable id) throws DataAccessException {
		return ht.get(entityClass, id);
	}

	public <T> T get(Class<T> entityClass, Serializable id, LockMode lockMode) throws DataAccessException {
		return ht.get(entityClass, id, lockMode);
	}

	public Object get(String entityName, Serializable id) throws DataAccessException {
		return ht.get(entityName, id);
	}

	public Object get(String entityName, Serializable id, LockMode lockMode) throws DataAccessException {
		return ht.get(entityName, id, lockMode);
	}

	public <T> T load(Class<T> entityClass, Serializable id) throws DataAccessException {
		return ht.load(entityClass, id);
	}

	public <T> T load(Class<T> entityClass, Serializable id, LockMode lockMode) throws DataAccessException {
		return ht.load(entityClass, id, lockMode);
	}

	public Object load(String entityName, Serializable id) throws DataAccessException {
		return ht.load(entityName, id);
	}

	public Object load(String entityName, Serializable id, LockMode lockMode) throws DataAccessException {
		return ht.load(entityName, id, lockMode);
	}

	public <T> List<T> loadAll(Class<T> entityClass) throws DataAccessException {
		return ht.loadAll(entityClass);
	}

	public void load(Object entity, Serializable id) throws DataAccessException {
		ht.load(entity, id);
	}

	public void refresh(Object entity) throws DataAccessException {
		ht.refresh(entity);
	}

	public void refresh(Object entity, LockMode lockMode) throws DataAccessException {
		ht.refresh(entity, lockMode);
	}

	public boolean contains(Object entity) throws DataAccessException {
		return ht.contains(entity);
	}

	public void evict(Object entity) throws DataAccessException {
		ht.evict(entity);
	}

	public void initialize(Object proxy) throws DataAccessException {
		ht.initialize(proxy);
	}

	public Filter enableFilter(String filterName) throws IllegalStateException {
		return ht.enableFilter(filterName);
	}

	public void lock(Object entity, LockMode lockMode) throws DataAccessException {
		ht.lock(entity, lockMode);
	}

	public void lock(String entityName, Object entity, LockMode lockMode) throws DataAccessException {
		ht.lock(entityName, entity, lockMode);
	}

	public Serializable save(Object entity) throws DataAccessException {
		return ht.save(entity);
	}

	public Serializable save(String entityName, Object entity) throws DataAccessException {
		return ht.save(entityName, entity);
	}

	public void update(Object entity) throws DataAccessException {
		ht.update(entity);
	}

	public void update(Object entity, LockMode lockMode) throws DataAccessException {
		ht.update(entity, lockMode);
	}

	public void update(String entityName, Object entity) throws DataAccessException {
		ht.update(entityName, entity);
	}

	public void update(String entityName, Object entity, LockMode lockMode) throws DataAccessException {
		ht.update(entityName, entity, lockMode);
	}

	public void saveOrUpdate(Object entity) throws DataAccessException {
		ht.saveOrUpdate(entity);
	}

	public void saveOrUpdate(String entityName, Object entity) throws DataAccessException {
		ht.saveOrUpdate(entityName, entity);
	}

	public void saveOrUpdateAll(Collection<?> entities) throws DataAccessException {
		ht.saveOrUpdateAll(entities);
	}

	public void replicate(Object entity, ReplicationMode replicationMode) throws DataAccessException {
		ht.replicate(entity, replicationMode);
	}

	public void replicate(String entityName, Object entity, ReplicationMode replicationMode) throws DataAccessException {
		ht.replicate(entityName, entity, replicationMode);
	}

	public void persist(Object entity) throws DataAccessException {
		ht.persist(entity);
	}

	public void persist(String entityName, Object entity) throws DataAccessException {
		ht.persist(entityName, entity);
	}

	public <T> T merge(T entity) throws DataAccessException {
		return ht.merge(entity);
	}

	public <T> T merge(String entityName, T entity) throws DataAccessException {
		return ht.merge(entityName, entity);
	}

	public void delete(Object entity) throws DataAccessException {
		ht.delete(entity);
	}

	public void delete(Object entity, LockMode lockMode) throws DataAccessException {
		ht.delete(entity, lockMode);
	}

	public void delete(String entityName, Object entity) throws DataAccessException {
		ht.delete(entityName, entity);
	}

	public void delete(String entityName, Object entity, LockMode lockMode) throws DataAccessException {
		ht.delete(entityName, entity, lockMode);
	}

	public void deleteAll(Collection<?> entities) throws DataAccessException {
		ht.deleteAll(entities);
	}

	public void flush() throws DataAccessException {
		ht.flush();
	}

	public void clear() throws DataAccessException {
		ht.clear();
	}

	public List<?> find(String queryString) throws DataAccessException {
		return ht.find(queryString);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(String queryString, Object value) throws DataAccessException {
		return ht.find(queryString, value);
	}

	public List<?> find(String queryString, Object... values) throws DataAccessException {
		return ht.find(queryString, values);
	}

	public List<?> findByNamedParam(String queryString, String paramName, Object value) throws DataAccessException {
		return ht.findByNamedParam(queryString, paramName, value);
	}

	public List<?> findByNamedParam(String queryString, String[] paramNames, Object[] values)
			throws DataAccessException {
		return ht.findByNamedParam(queryString, paramNames, values);
	}

	public List<?> findByValueBean(String queryString, Object valueBean) throws DataAccessException {
		return ht.findByValueBean(queryString, valueBean);
	}

	public List<?> findByNamedQuery(String queryName) throws DataAccessException {
		return ht.findByNamedQuery(queryName);
	}

	public List<?> findByNamedQuery(String queryName, Object value) throws DataAccessException {
		return ht.findByNamedQuery(queryName, value);
	}

	public List<?> findByNamedQuery(String queryName, Object... values) throws DataAccessException {
		return ht.findByNamedQuery(queryName, values);
	}

	public List<?> findByNamedQueryAndNamedParam(String queryName, String paramName, Object value)
			throws DataAccessException {
		return ht.findByNamedQueryAndNamedParam(queryName, paramName, value);
	}

	public List<?> findByNamedQueryAndNamedParam(String queryName, String[] paramNames, Object[] values)
			throws DataAccessException {
		return ht.findByNamedQueryAndNamedParam(queryName, paramNames, values);
	}

	public List<?> findByNamedQueryAndValueBean(String queryName, Object valueBean) throws DataAccessException {
		return ht.findByNamedQueryAndValueBean(queryName, valueBean);
	}

	public List<?> findByCriteria(DetachedCriteria criteria) throws DataAccessException {
		return ht.findByCriteria(criteria);
	}

	public List<?> findByCriteria(DetachedCriteria criteria, int firstResult, int maxResults)
			throws DataAccessException {
		return ht.findByCriteria(criteria, firstResult, maxResults);
	}

	public List<?> findByExample(Object exampleEntity) throws DataAccessException {
		return ht.findByExample(exampleEntity);
	}

	public List<?> findByExample(String entityName, Object exampleEntity) throws DataAccessException {
		return ht.findByExample(entityName, exampleEntity);
	}

	public List<?> findByExample(Object exampleEntity, int firstResult, int maxResults) throws DataAccessException {
		return ht.findByExample(exampleEntity, firstResult, maxResults);
	}

	public List<?> findByExample(String entityName, Object exampleEntity, int firstResult, int maxResults)
			throws DataAccessException {
		return ht.findByExample(entityName, exampleEntity, firstResult, maxResults);
	}

	public Iterator<?> iterate(String queryString) throws DataAccessException {
		return ht.iterate(queryString);
	}

	public Iterator<?> iterate(String queryString, Object value) throws DataAccessException {
		return ht.iterate(queryString, value);
	}

	public Iterator<?> iterate(String queryString, Object... values) throws DataAccessException {
		return ht.iterate(queryString, values);
	}

	public void closeIterator(Iterator<?> it) throws DataAccessException {
		ht.closeIterator(it);
	}

	public int bulkUpdate(String queryString) throws DataAccessException {
		return ht.bulkUpdate(queryString);
	}

	public int bulkUpdate(String queryString, Object value) throws DataAccessException {
		return ht.bulkUpdate(queryString, value);
	}

	public int bulkUpdate(String queryString, Object... values) throws DataAccessException {
		return ht.bulkUpdate(queryString, values);
	}

	@SuppressWarnings("unchecked")
	public <T> T findUniqueResult(String queryString, Object... values) {
		List<T> result = ht.find(queryString, values);
		if (result.size() == 0) {
			// List<T> result = ht.find(queryString, values);
			return null;
		}
		if (result.size() > 1) {
			throw new RuntimeException("Query [" + queryString + "] returned more than one value:" + result);
		}
		return result.get(0);
	}

	@SuppressWarnings("unchecked")
	public <T> T findMandatoryUniqueResult(String queryString, Object... values) {
		List<T> result = ht.find(queryString, values);
		if (result.size() == 0) {
			throw new RuntimeException("Query [" + queryString + "] returned no value.");
		}
		if (result.size() > 1) {
			throw new RuntimeException("Query [" + queryString + "] returned more than one value:" + result);
		}
		return result.get(0);
	}

	@SuppressWarnings("unchecked")
	public <T> T findFirstResult(String queryString, Object... values) {
		List<T> result = ht.find(queryString, values);
		if (result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

	public Session createSessionAndTransaction() {
		SessionFactory sf = ht.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		return session;
	}

	public void CommitAndCloseSession(Session mySession) {
		mySession.getTransaction().commit();
		mySession.close();
	}
}
