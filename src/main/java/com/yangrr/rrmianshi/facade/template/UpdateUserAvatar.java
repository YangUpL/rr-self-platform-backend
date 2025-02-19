package com.yangrr.rrmianshi.facade.template;

import com.yangrr.rrmianshi.facade.CheckFacade;
import com.yangrr.rrmianshi.utils.BasicCheckUtils;

/**
 * @PATH com.yangrr.rrmianshi.template.CheckAvatarTemplate
 * @Author YangRR
 * @CreateData 2025-01-29 13:36
 * @Description:
 */
public class UpdateUserAvatar extends CheckFacade {
    @Override
    public String checkAvatar(String avatar) {
        //校验头像
        if (BasicCheckUtils.checkAvatar(avatar)) {
            return null;
        }
        return avatar;
    }
}
