package com.hhh.shirospringboot.mapper;

import com.hhh.shirospringboot.model.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author hhh
 * @date 2020/1/19 16:23
 * @Despriction
 */
@Mapper
public interface GoodMapper {
  @Select("select * from goods where id = #{id}")
  Goods find(Integer id );
  @Select("update goods set amount = #{goods.amount} where id = #{goods.id}")
  Integer update(@Param("goods") Goods goods);
}
