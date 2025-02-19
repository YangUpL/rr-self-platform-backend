package com.yangrr.rrmianshi.strategy;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangrr.rrmianshi.common.BaseResponse;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.common.ResultUtils;
import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.dto.LoginDto;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.utils.BasicCheckUtils;
import com.yangrr.rrmianshi.vo.SafetyUser;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @PATH com.yangrr.rrmianshi.strategy.PasswordAndUsernameStrategy
 * @Author YangRR
 * @CreateData 2025-01-28 15:40
 * @Description: 用户名密码登录策略
 */

@Component
public class PasswordAndUsernameStrategy implements LoginStrategy {

    @Autowired
    private UsersService usersService;

    @Override
    public SafetyUser login(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户名或密码不能为空");
        }

        // 校验用户名和密码是否合法
        BasicCheckUtils.checkNameAndPwd(username, password);



        //得到MD5加密后的密码
        String safePwd = MD5.create().digestHex16(password, StandardCharsets.UTF_8);
        //  查询数据库
        Users user = usersService.getOne(
                new QueryWrapper<Users>()
                        .eq("username", username)
                        .eq("password", safePwd)
        );


        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        //将user拷贝被safetyUser
        SafetyUser safetyUser = new SafetyUser();
        BeanUtils.copyProperties(user, safetyUser);


        return safetyUser;
    }
}
