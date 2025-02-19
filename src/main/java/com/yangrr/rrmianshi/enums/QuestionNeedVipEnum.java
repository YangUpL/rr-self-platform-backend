package com.yangrr.rrmianshi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @PATH com.yangrr.rrmianshi.enums.NeedVipEnum
 * @Author YangRR
 * @CreateData 2025-02-03 13:29
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum QuestionNeedVipEnum {
    NEED_VIP(1, "需要vip才能使用"),
    NOT_NEED_VIP(0, "不需要vip也能使用");
    private Integer code;
    private String msg;

    public static QuestionNeedVipEnum getEnum(Integer code) {
        for (QuestionNeedVipEnum needVipEnum : QuestionNeedVipEnum.values()) {
            if (needVipEnum.getCode().equals(code)) {
                return needVipEnum;
            }
        }
        return null;
    }
}
