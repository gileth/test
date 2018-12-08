package org.newtake.dao.impl;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang3.StringUtils;
import org.newtake.model.CtLog;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
 
@Repository("newtakeBaseDao")
public class BaseDaoImpl{
	@Resource(name="jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	static{
		  ConvertUtils.register(new SqlDateConverter(null), java.util.Date.class);
	}
	public Map<String,Object> find(String tableName,Object[] resultClumns,Object[] whereColumnAndValues){
		String rc = formatResultColumns(resultClumns);
		StringBuilder sb = new StringBuilder("select ").append(rc).append(" from ").append(tableName);
		Object[] args = formatWhereColumns(whereColumnAndValues,sb);
		try{
			return jdbcTemplate.queryForMap(sb.toString(), args);
		}catch(DataAccessException e){
			if((e instanceof IncorrectResultSizeDataAccessException)
                    &&((IncorrectResultSizeDataAccessException)e).getActualSize()==0){
                return null;
			}    
			throw e;
		}
	}
	
	public <T> T find(String sql,Class<T> clazz,Object... args){
		try{
			return jdbcTemplate.queryForObject(sql , BeanPropertyRowMapper.newInstance(clazz),args);
		}catch(DataAccessException e){
			if((e instanceof IncorrectResultSizeDataAccessException)
                    &&((IncorrectResultSizeDataAccessException)e).getActualSize()==0){
                return null;
			}    
			throw e;
		}
	}
	
	/**
	 * 
	 * @param obj 需要操作的对象，用来保存自动增长的id值
	 * @param sql 需要操作的sql语句
	 * @param idColumnName 对应自动增长的数据表字段名称
	 * @param params 需要操作的字段值
	 * @return
	 */
	public int insert(final Object obj,final String sql,final String idColumnName,Class<?> idColumnClass,final Object... params){
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
		int i = jdbcTemplate.update(new PreparedStatementCreator(){
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql, new String[]{idColumnName});
				if(params != null && params.length > 0){
					for(int i = 1 ; i <= params.length ; i++){
						ps.setObject(i, params[i-1]);
					}
				}
				return ps;
			}
		}, keyHolder);
		int id = keyHolder.getKey().intValue();
		String first = idColumnName.substring(0,1);
	
			Method method = obj.getClass().getDeclaredMethod("set".concat(idColumnName.replace(first, first.toUpperCase())), idColumnClass);
			method.invoke(obj, id);
			return i;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Spring queryForList方法扩展
	 * 可通过指定类型及返回的字段进行对象简单注入
	 * 对象所拥有的属性需与跟sql语句中返回的字段名称保持一致
	 * @param clazz 返回类型
	 * @param sql
	 * @param args
	 * @return
	 */
	public <T> List<T> findList(Class<T> clazz,String sql,Object... args){
		List<Map<String,Object>> list =  jdbcTemplate.queryForList(sql, args);
		if(list == null)
			return null;
		List<T> result = new ArrayList<T>();
		T t = null;
		try {
			for(Map<String,Object> data : list){
					t = clazz.newInstance();
					for(String name : data.keySet()){
						BeanUtils.setProperty(t, name, data.get(name));
					}
					result.add(t);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public List<Map<String, Object>> findList(String tableName,
			Object[] resultClumns, Object[] whereColumns, String orders,
			int size) {
		StringBuilder query = new StringBuilder();
		Object[] args = formatWhereColumns(whereColumns,query);
		if(StringUtils.isNotBlank(orders)){
			orders = " order by "+orders;
		}
		String rc = formatResultColumns(resultClumns);
		StringBuilder listSql = new StringBuilder("select ").append(rc).append(" from ").append(tableName).append(query).append(orders);
		if(size > 0){
			listSql.append(" limit ").append(size);
		}
		return jdbcTemplate.queryForList(listSql.toString(), args);
	}
	
	private String formatResultColumns(Object[] resultClumns){
		int length = resultClumns == null ? 0 : resultClumns.length;
		if(length == 0){
			return "*";
		}
		StringBuilder rc = new StringBuilder();
		for(int i = 0 ; i < length ; i++){
			rc.append(resultClumns[i]).append(",");
		}
		rc.deleteCharAt(rc.length()-1);
		return rc.toString();
	}
	
	private Object[] formatWhereColumns(Object[] whereColumns,StringBuilder query){
		int whereLength = whereColumns == null ? 0 : whereColumns.length ;
		Object[] args = new Object[whereLength/2];
		if(whereLength > 0){
			query.append(" where 1=1");
			String column = null;
			for(int i = 0 ; i < whereColumns.length ; i+=2){
				column = (String)whereColumns[i];
				query.append(" and ").append(column);
				args[i/2] = whereColumns[i+1];
			}
		}
		return args;
	}
	
	public void close(ResultSet rs,Statement stmt,Connection conn){
		try {
			if(rs!=null)rs.close();
			if(stmt!=null) stmt.close();
			if(conn!=null)conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void rollback(Connection conn){
		if(conn != null)
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public void close(Connection conn){
		try{
			if(conn!=null)conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void close(ResultSet rs){
		try{
			if(rs!=null)rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void close(Statement stmt){
		try{
			if(stmt!=null) stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public List<Map<String, Object>> findList(String sql, int size, Object... args) {
		StringBuilder listSql = new StringBuilder(sql);
		if(size > 0){
			listSql.append(" limit ").append(size);
		}
		return jdbcTemplate.queryForList(listSql.toString(), args);
	}

	/**
	 * 分页查询
	 * @param page 保存分页数据对象
	 * @param count_sql 查询总记录数
	 * @param list_sql 查询分页数据
	 * @param args 预查询语句参数值
	 */
//	@SuppressWarnings({ "rawtypes", "unchecked"})
//	public PageInfo<?> findPage(int pageno, int pagesize, String countSql,
//			StringBuilder listSql, Object... args) {
//		final PageInfo<?> page = new PageInfo(pageno);
//		if(pagesize > 0){
//			page.setPageSize(pagesize);
//		}
//		getPageSumData(page,countSql,args);
//		List data = null;
//		if(page.getTotalCount() > 0){
//			data = jdbcTemplate.queryForList(
//					listSql.append(" limit ").append(page.getFirstResult()).append(",").append(page.getPageSize()).toString(),args);
//		}	
//		page.setData((data == null ? new ArrayList():data));
//		return page;
//	}
//	
//	public PageInfo<?> findPage(int pageno, int pagesize,String tables,Object[]resultColumns,Object[] whereColumns,String orders){
//		StringBuilder query = new StringBuilder();
//		Object[] args = formatWhereColumns(whereColumns,query);
//		if(StringUtils.isNotBlank(orders)){
//			orders = " order by "+orders;
//		}
//		String rc = formatResultColumns(resultColumns);
//		
//		String countSql = new StringBuilder("select count(1) from ").append(tables).append(query).toString();
//		StringBuilder listSql = new StringBuilder("select ").append(rc).append(" from ").append(tables).append(query);
//		
//		return this.findPage(pageno, pagesize, countSql, listSql, args);
//		
//	}
	
}
