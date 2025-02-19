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
public enum UserGenderEnum {
    HIDDEN(-1, "隐藏性别"),
    MAN(0, "男"),
    WOMAN(1, "女");

    private final Integer code;
    private final String value;

    public static UserGenderEnum getGenderEnum(Integer gender) {
        for (UserGenderEnum userGenderEnum : UserGenderEnum.values()) {
            if (userGenderEnum.getCode().equals(gender)) {
                return userGenderEnum;
            }
        }
        return null;
    }
}
