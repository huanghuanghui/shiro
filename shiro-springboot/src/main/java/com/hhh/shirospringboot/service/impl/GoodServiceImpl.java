package com.hhh.shirospringboot.service.impl;

import com.hhh.shirospringboot.annotation.RedissonLock;
import com.hhh.shirospringboot.mapper.GoodMapper;
import com.hhh.shirospringboot.model.Goods;
import com.hhh.shirospringboot.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hhh
 * @date 2020/1/19 16:23
 * @Despriction
 */
@Service
public class GoodServiceImpl implements GoodService {

  @Autowired
  private GoodMapper goodMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  @RedissonLock
  public void mult(Integer goodsId) {
    Goods goods = goodMapper.find(goodsId);
    System.out.println(goods.getAmount());
    goods.setAmount(goods.getAmount() - 1);
    goodMapper.update(goods);
  }
}
