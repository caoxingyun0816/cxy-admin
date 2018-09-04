package com.imooc.cache;

/**
 * Created by caoxingyun on 2018/9/3.
 */
public class CacheImpl implements InitCache{
    public SystemConfigDao systemConfigDao;

    @Override
    public void initCache(){
        if(systemConfigDao !=null){
           SystemConfigCache.init(systemConfigDao);
        }
    }

    @Override
    public void refreshCache(int type) {

    }
}
