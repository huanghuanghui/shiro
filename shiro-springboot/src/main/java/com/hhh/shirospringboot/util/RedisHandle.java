package com.hhh.shirospringboot.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction Redis操作类
 */
@Log4j2
@Component
public class RedisHandle {

  public static RedisTemplate redisTemplate;

  @Autowired
  public void setRedisTemplate(RedisTemplate redisTemplate) {
    RedisHandle.redisTemplate = redisTemplate;
  }

  public static Integer REDIS_INCREASE = 1;
  /**
   * 默认失效时间 一天
   */
  private static Long defaultExpireTime = 60 * 60 * 24L;

  /**
   * 指定缓存失效时间
   *
   * @param key  键
   * @param time 时间(秒)
   * @return
   */
  public static boolean expire(String key, long time) {
    try {
      if (time > 0) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 根据key 获取过期时间
   *
   * @param key 键 不能为null
   * @return 时间(秒) 返回0代表为永久有效
   */
  public static long getExpire(String key) {
    return redisTemplate.getExpire(key, TimeUnit.SECONDS);
  }

  /**
   * 判断key是否存在
   *
   * @param key 键
   * @return true 存在 false不存在
   */
  public static  boolean hasKey(String key) {
    try {
      return redisTemplate.hasKey(key);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * setNotExist 当不存在的时候设置
   * @param key
   * @param value
   * @param expireTime 过期时间 second
   * @return
   */
  public static boolean setnx(String key, String value, long expireTime) {
    Boolean result = redisTemplate.opsForValue().setIfAbsent(key,value);
    if (result){
      redisTemplate.expire(key,expireTime,TimeUnit.SECONDS);
    }
    return result;
  }

  /**
   * 删除缓存
   *
   * @param key 可以传一个值 或多个
   */
  @SuppressWarnings("unchecked")
  public static void del(String... key) {
    if (key != null && key.length > 0) {
      if (key.length == 1) {
        redisTemplate.delete(key[0]);
      } else {
        redisTemplate.delete(CollectionUtils.arrayToList(key));
      }
    }
  }

  /**
   * 普通缓存获取
   *
   * @param key 键
   * @return 值
   */
  public static Object get(String key) {
    return key == null ? null : redisTemplate.opsForValue().get(key);
  }

  /**
   * 普通缓存放入
   *
   * @param key   键
   * @param value 值
   * @return true成功 false失败
   */
  public static boolean set(String key, Object value) {
    try {
      redisTemplate.opsForValue().set(key, value);
      redisTemplate.expire(key, defaultExpireTime, TimeUnit.SECONDS);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 普通缓存放入并设置时间
   *
   * @param key   键
   * @param value 值
   * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
   * @return true成功 false 失败
   */
  public static boolean set(String key, Object value, long time) {
    try {
      // 解决报错 non null key required
      if (time > 0 && key != null && value != null) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        redisTemplate.expire(key, defaultExpireTime, TimeUnit.SECONDS);
      } else if (key != null && value != null) {
        set(key, value);
      } else {
        log.error("non null key required_key=" + key + "|value=" + value);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean hmSet(String key, Map<String, Object> map) {
    try {
      redisTemplate.opsForHash().putAll(key, map);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 恒定自增1
   *
   * @param key 键
   * @return
   */
  public static long incr(String key) {
    return redisTemplate.opsForValue().increment(key, REDIS_INCREASE);
  }

  /**
   * 计算 dbsize
   *
   * @return
   */
  public static Long dbsize() {
    return (Long) redisTemplate.execute(new RedisCallback<Object>() {
      @Override
      public Long doInRedis(RedisConnection connection)
        throws DataAccessException {
        StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
        return stringRedisConnection.dbSize();
      }
    });
  }
  /**
   * scan 实现
   * @param pattern   表达式
   * @param consumer  对迭代到的key进行操作
   */
  public static void scan(String pattern, Consumer<byte[]> consumer) {
    redisTemplate.execute((RedisConnection connection) -> {
      try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
        cursor.forEachRemaining(consumer);
        return null;
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * 获取符合条件的key
   * @param pattern   表达式
   * @return
   */
  public static  List<String> keys(String pattern) {
    List<String> keys = new ArrayList<>();
    scan(pattern, item -> {
      //符合条件的key
      String key = new String(item,StandardCharsets.UTF_8);
      keys.add(key);
    });
    return keys;
  }
}
