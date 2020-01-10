package com.hhh.shirospringboot.config.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction 重写Shiro缓存管理器
 */
public class CustomCacheManager implements CacheManager {
  @Override
  public <K, V> Cache<K, V> getCache(String s) throws CacheException {
    return new CustomCache<K, V>();
  }
}
