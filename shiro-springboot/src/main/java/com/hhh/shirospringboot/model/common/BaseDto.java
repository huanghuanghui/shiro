package com.hhh.shirospringboot.model.common;

import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页排序通用Dto
 */
@Data
public class BaseDto implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 当前页数
   */
  @Transient
  @Min(value = 1, message = "当前页数不能小于1")
  private Integer page;

  /**
   * 每页条数
   */
  @Transient
  @Min(value = 1, message = "每页条数不能小于1")
  @Max(value = 50, message = "每页条数不能大于50")
  private Integer rows;

  /**
   * 排序的列名
   */
  @Transient
  private String sidx;

  /**
   * 排序规则(DESC或者ESC)
   */
  @Transient
  private String sord;
}
