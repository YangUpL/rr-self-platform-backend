package com.yangrr.rrmianshi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @PATH com.yangrr.rrmianshi.enums.QuestionDifferenceEnum
 * @Author YangRR
 * @CreateData 2025-02-03 13:25
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum QuestionDifferenceEnum {
    //    题目的难度，1-简单  3-中等  2-困难
    EASY(1, "简单"),
    MEDIUM(3, "中等"),
    HARD(2, "困难");

    private Integer code;
    private String desc;

    public static QuestionDifferenceEnum getEnumByCode(Integer code) {
        for (QuestionDifferenceEnum questionDifferenceEnum : QuestionDifferenceEnum.values()) {
            if (questionDifferenceEnum.getCode().equals(code)) {
                return questionDifferenceEnum;
            }
        }
        return null;
    }

}
