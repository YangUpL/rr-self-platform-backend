package com.yangrr.rrmianshi.strategy;

import com.yangrr.rrmianshi.dto.UpdateUserDto;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @PATH com.yangrr.rrmianshi.strategy.UpdateUserStrtery
 * @Author YangRR
 * @CreateData 2025-02-11 09:53
 * @Description:
 */
public interface UpdateUserStrategy {
    Boolean updateUser(UpdateUserDto updateUserDto, HttpServletRequest request);
}
