package com.wondertek.mam.util.backupUtils.util22.cache;

import java.io.Serializable;
import java.util.List;

/** 定义cache获取的接口类
 * @author jimmy deng
 * @param <T>cache对象的类型
 * @param <PK>cache对象的key,为一个数组对象 */
public interface GenericCache<T, PK extends Serializable> {

	/** 根据ID获取对象 */
	T get(PK id);

	/** 根据一组ID,获取相应对象组 */
	List<T> get(PK[] ids);

	/** 精简批量方式, 一次性从数据源中获取多条 如从数据库中获取的缓存, 可以通过一个sql完成多条数据 */
	List<T> getBatch(PK[] ids);

	/** 从缓存中删除指定id对象 */
	void remove(PK id);

	/** 更新对象 */
	void update(PK id, T object);

}
