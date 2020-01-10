package com.hhh.shirospringboot.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author hhh
 * @date 2020/1/10 16:36
 * @Despriction
 */
public class Lru<K,V> extends LinkedHashMap<K, V> {
  private int CACHE_SIZE = 0;

  /**
   * 传递进来能缓存多少数据
   *
   * @param cacheSize 缓存大小
   */
  public Lru(int cacheSize) {
    //true 表示让 LinkedHashMap 按照访问顺序进行排序，最近访问的放在头部，最老访问的放在尾部
    super((int) (Math.ceil(cacheSize / 0.75)) + 1, 0.75f, true);
    CACHE_SIZE = cacheSize;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    //当 map 中的个数大于指定缓存个数的时候，就自动删除最老访问元素
    return size() > CACHE_SIZE;
  }
}
