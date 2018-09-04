package com.imooc.config;

import com.imooc.cache.CacheImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by caoxingyun on 2018/9/3.
 * cache 初始化缓存
 */
@Configuration
@ComponentScan("com.imooc")
public class CacheConfig {
    @Bean(initMethod = "initCache")
    public CacheImpl getCache(){
        return new CacheImpl();
    }

}
