package com.yangrr.rrmianshi.dto.question;

import lombok.Data;

/**
 * @PATH com.yangrr.rrmianshi.dto.question.OjTemplate
 * @Author YangRR
 * @CreateData 2025-02-14 15:04
 * @Description:
 */
@Data
public class OjTemplate {
//    {"return":"int","methodName":"testMethod","argsType":["int","int"]}
    private String returnName;
    private String methodName;
    private String[] argsType;
}
