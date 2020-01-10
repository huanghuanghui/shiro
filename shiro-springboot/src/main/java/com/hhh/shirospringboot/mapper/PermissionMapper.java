package com.hhh.shirospringboot.mapper;

import com.hhh.shirospringboot.model.PermissionDto;
import com.hhh.shirospringboot.model.RoleDto;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PermissionMapper extends Mapper<PermissionDto> {
  /**
   * 根据Role查询Permission
   */
  List<PermissionDto> findPermissionByRole(RoleDto roleDto);
}
