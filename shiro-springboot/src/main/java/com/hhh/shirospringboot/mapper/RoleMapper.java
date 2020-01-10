package com.hhh.shirospringboot.mapper;

import com.hhh.shirospringboot.model.RoleDto;
import com.hhh.shirospringboot.model.UserDto;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleMapper extends Mapper<RoleDto> {
  /**
   * 根据User查询Role
   */
  List<RoleDto> findRoleByUser(UserDto userDto);
}
