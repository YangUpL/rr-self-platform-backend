package com.yangrr.rrmianshi.factory;

import com.yangrr.rrmianshi.utils.UserThreadLocalUtil;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.enums.UserRoleEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.strategy.*;
import com.yangrr.rrmianshi.vo.SafetyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @PATH com.yangrr.rrmianshi.factory.UpdateUserFactory
 * @Author YangRR
 * @CreateData 2025-01-28 16:13
 * @Description:
 */

@Component
public class UpdateUserFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public UpdateUserStrategy getUpdateUserStrategy(Long id, Integer role) {
        SafetyUser safetyUser = UserThreadLocalUtil.get();
        if(safetyUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }

        if (safetyUser.getId().equals(id)) {
            return applicationContext.getBean(PersonalUpdateUser.class);
        } else if (UserRoleEnum.getRoleEnum(role) == UserRoleEnum.ADMIN){
            return applicationContext.getBean(AdminUpdateUser.class);
        } else {
            throw new BusinessException(ErrorCode.UPDATE_ERROR, "你不是本人或管理员");
        }
    }
}
