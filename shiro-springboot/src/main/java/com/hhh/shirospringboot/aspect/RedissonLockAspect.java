package com.hhh.shirospringboot.aspect;

import com.hhh.shirospringboot.annotation.RedissonLock;
import com.hhh.shirospringboot.exception.CustomException;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author hhh
 * @date 2020/1/19 15:58
 * @Despriction
 */
@Aspect
@Component
@Order(1)//必须设置Order
@Log4j2
public class RedissonLockAspect {

  @Resource
  private Redisson redisson;

  /**
   * 环绕通知切面
   * @param joinPoint
   * @param redissonLock
   * @return
   * @throws Throwable
   */
  @Around("@annotation(redissonLock)")
  public Object redisLockAround(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
    Object object = null;
    //获取方法内所有参数
    Object[] params = joinPoint.getArgs();
    int lockIndex = redissonLock.lockIndex();
    //取得方法名
    String key = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
    log.info("RedissonLock-key:{}", key);
    //-1代表锁住整个方法，而不是哪条数据
    if (lockIndex != -1) {
      key += params[lockIndex];
    }
    //多久释放，默认10S
    int leaseTime = redissonLock.leaseTime();
    //最多等待5秒，若是5秒还没获取到锁，就直接失败
    int waitTime = 5;
    RLock rLock = redisson.getLock(key);
    log.info("RedissonLock-rLock:{}", rLock);
    //在切面里里面RLock.tryLock，是最多等待5秒，若还没取到锁就走失败，取到了则进入方法走逻辑。
    //第二个参数是自动释放锁的时间，以避免自己刚取到锁，就挂掉了，导致锁无法释放。
    Boolean result = rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
    log.info("RedissonLock-lock result:{}", result);
    if (result){
      log.info("RedissonLock-获取到锁key:{}",key);
      object = joinPoint.proceed();
      log.info("RedissonLock-开始释放锁key:{}",key);
      rLock.unlock();
    }else{
      log.info("RedissonLock-未获取到锁key:{}",key);
      throw new CustomException("RedissonLock-未获取到锁key:"+key+",快速失败");
    }
    return object;
  }
}
