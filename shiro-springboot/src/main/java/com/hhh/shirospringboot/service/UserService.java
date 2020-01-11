package com.hhh.shirospringboot.service;

import com.hhh.shirospringboot.model.UserDto;
import com.hhh.shirospringboot.model.common.BaseDto;
import com.hhh.shirospringboot.model.common.ResponseBean;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hhh
 * @date 2020/1/11 16:03
 * @Despriction
 */
public interface UserService {
  /**
   * 获取用户列表
   * @param baseDto
   * @return
   */
  ResponseBean userList(BaseDto baseDto);
  /**
   * 查看在线用户
   */
  ResponseBean online();

  /**
   * 登录
   * @param userDto
   * @param httpServletResponse
   * @return
   */
  ResponseBean login(UserDto userDto, HttpServletResponse httpServletResponse);
  /**
   * 获取当前用户
   */
  ResponseBean getCurrentUser();

  /**
   * 获取用户
   * @param id
   * @return
   */
  ResponseBean getUser(Integer id);

  /**
   * 添加用户
   * @param userDto
   * @return
   */
  ResponseBean addUser(UserDto userDto);

  /**
   * 更新用户信息
   * @param userDto
   * @return
   */
  ResponseBean updateUser(UserDto userDto);

  /**
   * 删除指定用户
   * @param id
   * @return
   */
  ResponseBean deleteUser(Integer id);

  /**
   * 剔除在线用户
   * @param id
   * @return
   */
  ResponseBean deleteOnLine(Integer id);
}
