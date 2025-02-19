package com.yangrr.rrmianshi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @PATH com.yangrr.rrmianshi.enums.LoginTypeEnum
 * @Author YangRR
 * @CreateData 2025-01-29 09:49
 * @Description:
 */

@Getter
@AllArgsConstructor
public enum UserRoleEnum {
    USER(0, "普通用户"),
    ADMIN(1, "管理员"),
    VIP(2, "VIP用户"),
    BAN(3, "封禁用户");

    private final Integer role;
    private final String roleName;

    public static UserRoleEnum getRoleEnum(Integer role) {
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.getRole().equals(role)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
