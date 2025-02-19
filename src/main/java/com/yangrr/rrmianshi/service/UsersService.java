package com.yangrr.rrmianshi.service;

import com.yangrr.rrmianshi.common.BaseResponse;
import com.yangrr.rrmianshi.domain.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangrr.rrmianshi.dto.*;
import com.yangrr.rrmianshi.vo.PublicKeyVo;
import com.yangrr.rrmianshi.vo.SafetyUser;
import com.yangrr.rrmianshi.vo.SafetyUserPageVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 31841
* @description 针对表【users(用户信息表，存储用户的基本信息和状态)】的数据库操作Service
* @createDate 2025-01-27 14:46:07
*/
public interface UsersService extends IService<Users> {

    BaseResponse<Long> login(LoginDto loginDto, HttpServletRequest request);

    BaseResponse<SafetyUser> register(RegisterDto registerDto);

    void sendMail(EmailDto emailDto);

    SafetyUser getCurrentUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(SafetyUser user);

    Boolean addUser(AddUserDto addUserDto);

    SafetyUserPageVo getUserList(GlobalDto globalDto);

    Boolean updateUser(UpdateUserDto updateUserDto,HttpServletRequest request);

    Boolean deleteUser(Long id);

    SafetyUser getUserById(Long id);

    Boolean resetPassword(Integer id);
}
