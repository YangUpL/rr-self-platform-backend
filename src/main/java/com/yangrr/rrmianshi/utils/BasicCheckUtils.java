package com.yangrr.rrmianshi.utils;

import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.enums.UserGenderEnum;
import com.yangrr.rrmianshi.enums.UserRoleEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

/**
 * @PATH com.yangrr.rrmianshi.utils.BasicCheck
 * @Author YangRR
 * @CreateData 2025-01-28 17:32
 * @Description:
 */
public class BasicCheckUtils {

    public static void checkDescGithubGender(String description, Integer gender, String githubUrl) {
        if (StringUtils.isBlank(description) || gender == null || StringUtils.isBlank(githubUrl)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "参数为空");
        }
        if (description.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述长度不合法");
        }
        if(UserGenderEnum.getGenderEnum(gender) == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别不合法");
        }
        if(!githubUrl.startsWith("https://github.com/")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "github格式不合法");
        }
    }


    public static void checkName(String username) {
        //  1<用户名长度<30
        if (username.isEmpty() || username.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度不合法");
        }
    }

    public static void checkNameAndPwd(String username, String password) {
        //  1<用户名长度<30
        if (username.isEmpty() || username.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度不合法");
        }
        //  1<密码长度<30
        if (password.isEmpty() || password.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不合法");
        }
    }

    public static void checkEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "邮箱为空");
        }
        //校验邮箱格式是否合法
        if (!email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不合法");
        }

    }

    public static void checkRole(Integer role) {
        if (role == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "角色为空");
        }
        UserRoleEnum roleEnum = UserRoleEnum.getRoleEnum(role);
        if (roleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不合法");
        }
    }

    public static Boolean checkAvatar(String avatar) {
        return StringUtils.isBlank(avatar);
    }
}
