/**********************************************************************
 * FILE : CommonCacheManager.java
 * CREATE DATE : 2012-6-8
 * DESCRIPTION :
 * 为业务要求比较简单的数据提供缓存服务
 * 不通过在Cache中的缓存Spring配置中加载,通过在应用中的Spring配置中加载
 * 	<bean id="commonCacheManager"
		class="com.wondertek.mobilevideo.core.cache.CommonCacheManager">
		<property name="ehcacheManager" ref="ehcacheManager" />
		<property name="memcached" ref="memcached" />
		<property name="ehcacheName" value="commonCache" />
		<property name="memExpiry" value="120" />
	</bean>
 * 注意:应用中spring配置加载此缓存服务实例时千万不能将缓存名设置与已有缓存名重复
 * 
 * 缓存数据结构:
 * 	  Key:==> String
 * 	Value:==> Map
 * CHANGE HISTORY LOG
 *---------------------------------------------------------------------
 * 编号|    日期    |     姓名     |     操作     | 变更描述
 *---------------------------------------------------------------------
 *  1    2012-6-8      jiguian       创建				创建文件
 **********************************************************************
 */
package com.wondertek.mam.util.backupUtils.util22.cache;

import com.wondertek.mobilevideo.core.cache.GenericCacheManager;

import java.util.Map;

public class CommonCacheManager extends GenericCacheManager<Map,String> {
	
}
