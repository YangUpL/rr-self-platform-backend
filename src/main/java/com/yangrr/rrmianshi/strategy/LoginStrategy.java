package com.yangrr.rrmianshi.strategy;

import com.yangrr.rrmianshi.common.BaseResponse;
import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.dto.LoginDto;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.vo.SafetyUser;

/**
 * 登录策略
 */
public interface LoginStrategy {
    SafetyUser login(LoginDto loginDto);
}
