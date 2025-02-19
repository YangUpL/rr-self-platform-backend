package com.yangrr.rrmianshi.factory;

import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.dto.LoginDto;
import com.yangrr.rrmianshi.enums.LoginTypeEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.strategy.EmailAndCodeLoginStrategy;
import com.yangrr.rrmianshi.strategy.LoginStrategy;
import com.yangrr.rrmianshi.strategy.PasswordAndUsernameStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @PATH com.yangrr.rrmianshi.factory.LoginFactory
 * @Author YangRR
 * @CreateData 2025-01-28 16:13
 * @Description:
 */

@Component
public class LoginFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public LoginStrategy getLoginStrategy(Integer loginType) {
        if (LoginTypeEnum.getLoginType(loginType) == LoginTypeEnum.EMAIL_LOGIN) {
            return applicationContext.getBean(EmailAndCodeLoginStrategy.class);
        } else if (LoginTypeEnum.getLoginType(loginType) == LoginTypeEnum.PASSWORD_LOGIN) {
            return applicationContext.getBean(PasswordAndUsernameStrategy.class);
        } else {
            throw new BusinessException(ErrorCode.LOGIN_TYPE_ERROR, "登录类型错误");
        }
    }
}
