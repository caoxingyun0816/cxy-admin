package com.wondertek.mam.util.backupUtils.util22.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.wondertek.mobilevideo.core.cache.memcached.Memcached;

/** 项目中缓冲的数据来源主要有三种情况： 从数据库中获取: memcache的对象失效由外部定时器进行清理 从文件中获取: memcache的对象失效,根据文件修改时间判断,每2分钟检查
 * 从公共服务中获取: memcache的根据设置的过期时间自动失效,同时程序中也会主动更新移除. memExpiry为memcached过期时间 */
public abstract class GenericCacheManager<T, PK extends Serializable> implements
        com.wondertek.mobilevideo.core.cache.GenericCache<T, PK> {
	protected final static Logger log = Logger.getLogger(GenericCacheManager.class);


	/** 如果配置了就会使用，不配置不使用 */
	protected Memcached memcached;

	/** memcached 过期时间,分钟为单位 默认为0为永不过期 */
	protected long memExpiry = 0;

	/** 配置ehcache的名字 */
	protected String ehcacheName;
	protected CacheManager ehcacheManager;
	protected Cache cache = null;

	/** 设置NODB模式，避免数据库出错情况下，一直查数据库，把门户服务器拖死 */
	public static boolean NO_DB_MODE = false;

	protected T getCache(PK id) {

		// 从ehcache中获取成功,则返回
		if (cache == null && ehcacheManager != null)
			cache = ehcacheManager.getCache(ehcacheName);
		if (cache != null) {
			Element elm = cache.get(id);
			if (elm != null)
				return (T) elm.getObjectValue();
		}

		// 从memcache中获取成功,则返回
		if (memcached != null) {
			Object obj = memcached.get(ehcacheName + id);
			if (obj != null){
				//如果ehcache过期了，memcache还有值，需要重置一下ehcache
				if (ehcacheManager != null ) {
					Element elm = new Element(id, obj);
					if (ehcacheManager.getCache(ehcacheName) != null)
						ehcacheManager.getCache(ehcacheName).put(elm);
				}
				return (T) obj;
			}
		}
		return null;
	}

	@Override
	public T get(PK id) {
		try {
			T object = getCache(id);
			if (object != null)
				return object;

			// 缓存中没有,从数据源中获取
			object = getObject(id);

			// 数据源中不存在,则新建
			if (object == null)
				object = createObject(id);

			// 从数据源中获取或新建完成后,更新缓存
			if (object != null)
				this.update(id, object);
			return object;

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<T> get(PK[] ids) {
		List<T> rs = new ArrayList<T>();
		for (PK id : ids) {
			rs.add(this.get(id));
		}
		return rs;
	}

	@Override
	public List<T> getBatch(PK[] ids) {

		Map<PK, T> incache = new HashMap<PK, T>();
		List<PK> nocacheIds = new ArrayList<PK>();

		for (PK id : ids) {
			T obj = getCache(id);
			if (obj != null)
				incache.put(id, obj);
			else
				nocacheIds.add(id);
		}

		Map<PK, T> noincache = this.getBatchObject(nocacheIds);
		for (PK id : nocacheIds) {
			update(id, noincache.get(id));
		}

		List<T> rs = new ArrayList<T>();
		for (PK id : ids) {
			T t = incache.get(id);
			if (t != null)
				rs.add(t);
			else {
				t = noincache.get(id);
				if (t != null)
					rs.add(t);
				else
					rs.add(this.createObject(id));
			}
		}
		return rs;
	}

	@Override
	public void remove(PK id) {
		if (cache == null && ehcacheManager != null)
			cache = ehcacheManager.getCache(ehcacheName);

		if (cache != null)
			cache.remove(id);

		if (memcached != null)
			memcached.delete(ehcacheName + id);
	}

	@Override
	public void update(PK id, T obj) {
		if (ehcacheManager != null && obj != null) {
			Element elm = new Element(id, obj);
			if (ehcacheManager.getCache(ehcacheName) != null)
				ehcacheManager.getCache(ehcacheName).put(elm);
		}

		if (memcached != null && obj != null) {
			// 这里设置的失效时间  是服务器操作点的相对时间  不是 绝对 时间
			memcached.set(ehcacheName + id, obj, new Date(getMemExpiry() * 60 * 1000));
		}
	}

	/** 继承此类需要重载此方法,
	 * @param id
	 * @return */
	protected T createObject(PK id) {
		return null;
	}

	/** 继承此类需要重载此方法
	 * @param id
	 * @return */
	protected T getObject(PK id) {
		return null;
	}

	/** 为了提供一次获取大量缓存的效率, 继承此类需要重载此方法 需要取出一组,并把对象放到缓存中.
	 * @param id
	 * @return */
	protected Map<PK, T> getBatchObject(List<PK> id) {
		return null;
	}

	public void flushEhCache() {
		if (ehcacheManager != null)
			ehcacheManager.clearAll();
	}

	public boolean flushMemcached() {
		if (memcached != null) {
			memcached.flushAll();
			return true;
		}
		return false;
	}

	public void setMemcached(Memcached memcached) {
		this.memcached = memcached;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public String getEhcacheName() {
		return ehcacheName;
	}

	public void setEhcacheName(String ehcacheName) {
		this.ehcacheName = ehcacheName;
	}

	public void setMemExpiry(long memExpiry) {
		this.memExpiry = memExpiry;
	}

	public long getMemExpiry() {
		return memExpiry;
	}

}
