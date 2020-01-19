package com.hhh.shirospringboot;

import com.hhh.shirospringboot.service.GoodService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author hhh
 * @date 2020/1/19 16:33
 * @Despriction 测试Redisson Lock 用100并发，同时操作2个商品。
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GiftmallApplicationTests {
  @Resource
  private GoodService goodsService;

  private CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
  private CyclicBarrier cyclicBarrier1 = new CyclicBarrier(10);

  @Test
  public void contextLoads() {
    for (int i = 0; i < 10; i++) {
      new Thread(() -> {
        try {
          cyclicBarrier.await();

          goodsService.mult(1);
        } catch (InterruptedException | BrokenBarrierException e) {
          e.printStackTrace();
        }
      }
      ).start();
      new Thread(() -> {
        try {
          cyclicBarrier1.await();

          goodsService.mult(2);
        } catch (InterruptedException | BrokenBarrierException e) {
          e.printStackTrace();
        }
      }
      ).start();
    }

    try {
      Thread.sleep(6000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
