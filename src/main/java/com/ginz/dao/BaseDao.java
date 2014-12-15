package com.ginz.dao;

import java.io.Serializable;
import java.util.List;

/**
 * 基础数据库操作类
 * 
 * @author 孙宇
 * 
 */
public interface BaseDao<T> {

	/**
	 * 保存一个对象
	 * 
	 * @param o
	 * @return
	 */
	public void saveTrend(T o);
	
	public void saveCus(T o);
	
	public void saveSta(T o);
	
	public Serializable save(T o);
	
	public Serializable saveC(T o);

	/**
	 * 删除一个对象
	 * 
	 * @param o
	 */
	public void delete(T o);

	/**
	 * 更新一个对象
	 * 
	 * @param o
	 */
	public void update(T o);
	public void merge(T o);

	/**
	 * 保存或更新对象
	 * 
	 * @param o
	 */
	public void saveOrUpdate(T o);

	/**
	 * 查询
	 * @param hql
	 * @return
	 */
	public List<T> find(String hql);
	
	/**
	 * 查询
	 * @param hql
	 * @return
	 */
	public List<T> find(String hql, int start, int length);

	/**
	 * 查询集合
	 * @param hql
	 * @param param
	 * @return
	 */
	public List<T> find(String hql, Object[] param);

	/**
	 * 查询集合
	 * @param hql
	 * @param param
	 * @return
	 */
	public List<T> find(String hql, List<Object> param);

	/**
	 * 查询集合(带分页)
	 * @param hql
	 * @param param
	 * @param page 查询第几页
	 * @param rows 每页显示几条记录
	 * @return
	 */
	public List<T> find(String hql, Object[] param, Integer page, Integer rows);

	/**
	 * 查询集合(带分页)
	 * @param hql
	 * @param param
	 * @param page
	 * @param rows
	 * @return
	 */
	public List<T> find(String hql, List<Object> param, Integer page, Integer rows);

	/**
	 * 获得一个对象
	 * @param c 对象类型
	 * @param id
	 * @return Object
	 */
	public T get(Class<T> c, Serializable id);

	/**
	 * 获得一个对象
	 * @param hql
	 * @param param
	 * @return Object
	 */
	public T get(String hql, Object[] param);

	/**
	 * 获得一个对象
	 * 
	 * @param hql
	 * @param param
	 * @return
	 */
	public T get(String hql, List<Object> param);

	/**
	 * select count(*) from 类
	 * 
	 * @param hql
	 * @return
	 */
	public Long count(String hql);
	/**
	 * select count(*) from 类
	 * 
	 * @param sql
	 * @return
	 */
	public Long countBySql(String sql);


	/**
	 * select count(*) from 类
	 * 
	 * @param hql
	 * @param param
	 * @return
	 */
	public Long count(String hql, Object[] param);

	/**
	 * select count(*) from 类
	 * 
	 * @param hql
	 * @param param
	 * @return
	 */
	public Long count(String hql, List<Object> param);

	/**
	 * 执行HQL语句
	 * 
	 * @param hql
	 * @return 响应数目
	 */
	public Integer executeHql(String hql);

	/**
	 * 执行HQL语句
	 * 
	 * @param hql
	 * @param param
	 * @return 响应数目
	 */
	public Integer executeHql(String hql, Object[] param);

	/**
	 * 执行HQL语句
	 * 
	 * @param hql
	 * @param param
	 * @return
	 */
	public Integer executeHql(String hql, List<Object> param);

	public List<T> queryBySql(String sqlQuery);
	
	public List<T> queryBySql(String sqlQuery, int start, int length);
	
	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 *            SQL语句
	 * @return 响应行数
	 */
	public int executeSql(String sql);
}
