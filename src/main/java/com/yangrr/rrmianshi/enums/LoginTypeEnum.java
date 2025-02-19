package com.yangrr.rrmianshi.enums;

import lombok.*;

/**
 * @PATH com.yangrr.rrmianshi.enums.LoginTypeEnum
 * @Author YangRR
 * @CreateData 2025-01-29 09:49
 * @Description:
 */

@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    EMAIL_LOGIN(0, "邮箱登录"),

    PASSWORD_LOGIN(1, "邮箱登录");

    private final Integer loginType;
    private final String loginTypeName;

    public static LoginTypeEnum getLoginType(Integer loginType) {
        //LoginTypeEnum.values()就是两个枚举
        for (LoginTypeEnum loginTypeEnum : LoginTypeEnum.values()) {
            //找到符合的枚举
            if (loginTypeEnum.getLoginType().equals(loginType)) {
                return loginTypeEnum;
            }
        }
        return null;
    }
}
