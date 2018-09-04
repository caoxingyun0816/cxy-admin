package com.wondertek.mam.util.backupUtils.util22.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * this class listen the event of visitCache
 * 
 * @author wondertek
 * 
 */
public class GenericCacheEventListener implements CacheEventListener {
	protected Log log = LogFactory.getLog(getClass());

	@Override
	public void dispose() {
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		notifyElementEvicted(cache, element);
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element)
			throws CacheException {

	}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}