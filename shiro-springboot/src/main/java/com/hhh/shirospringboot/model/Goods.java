package com.hhh.shirospringboot.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hhh
 * @date 2020/1/19 16:24
 * @Despriction
 */
@Data
public class Goods implements Serializable {
  private Integer id;
  private Integer amount;
}
