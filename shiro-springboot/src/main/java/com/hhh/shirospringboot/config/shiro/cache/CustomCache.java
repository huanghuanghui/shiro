package com.hhh.shirospringboot.config.shiro.cache;

import com.hhh.shirospringboot.model.common.Constant;
import com.hhh.shirospringboot.util.JwtUtil;
import com.hhh.shirospringboot.util.RedisHandle;
import org.apache.ibatis.cache.CacheException;
import org.apache.shiro.cache.Cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction 重写Shiro的Cache保存读取
 */
public class CustomCache<K, V> implements Cache<K, V> {

  /**
   * 缓存的key名称获取为shiro:cache:account
   */
  private String getKey(Object key) {
    return Constant.PREFIX_SHIRO_CACHE + JwtUtil.getClaim(key.toString(), Constant.ACCOUNT);
  }

  /**
   * 获取缓存
   */
  @Override
  public Object get(Object key) throws CacheException {
    return RedisHandle.get(this.getKey(key));
  }

  /**
   * 保存缓存
   */
  @Override
  public Object put(Object key, Object value) throws CacheException {
    // 读取配置文件，获取Redis的Shiro缓存过期时间
    String shiroCacheExpireTime = Constant.SHIOR_CACHE_EXPIRE_TIME;
    // 设置Redis的Shiro缓存
    return RedisHandle.set(this.getKey(key), value, Integer.parseInt(shiroCacheExpireTime));
  }

  /**
   * 移除缓存
   */
  @Override
  public Object remove(Object key) throws CacheException {
    RedisHandle.del(this.getKey(key));
    return null;
  }

  /**
   * 清空所有缓存
   */
  @Override
  public void clear() throws CacheException {

  }

  /**
   * 缓存的个数
   */
  @Override
  public int size() {
    return RedisHandle.dbsize().intValue();
  }

  /**
   * 获取所有的key 时间复杂度为 O(n)，会阻塞其他命令执行，不重写
   */
  @Override
  public Set keys() {
    return new HashSet();
  }

  /**
   * 获取所有的value 时间复杂度为 O(n)，会阻塞其他命令执行，不重写
   */
  @Override
  public Collection values() {
    return new ArrayList();
  }
}
