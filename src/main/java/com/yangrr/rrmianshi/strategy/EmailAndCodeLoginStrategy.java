package com.yangrr.rrmianshi.strategy;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @PATH com.yangrr.rrmianshi.strategy.EmailAndCodeLoginStrategy
 * @Author YangRR
 * @CreateData 2025-01-28 15:50
 * @Description: 邮箱验证码登录策略
 */

@Component
public class EmailAndCodeLoginStrategy implements LoginStrategy {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UsersService usersService;


    @Override
    public SafetyUser login(LoginDto loginDto) {
        String email = loginDto.getEmail();
        String code = loginDto.getCode();
        if (StringUtils.isAnyBlank(email, code)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "邮箱或验证码不能为空");
        }
        //校验邮箱格式是否合法
        BasicCheckUtils.checkEmail(email);

        // 校验验证码是否正确
        if (!code.equals(redisTemplate.opsForValue().get(email))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不正确");
        }

        //根据邮箱查询数据库并返回
        Users user = usersService.getOne(
                new QueryWrapper<Users>()
                        .eq("email", email)
        );

        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱不存在");
        }

        //将user拷贝被safetyUser
        SafetyUser safetyUser = new SafetyUser();
        BeanUtils.copyProperties(user, safetyUser);


        return safetyUser;
    }
}
