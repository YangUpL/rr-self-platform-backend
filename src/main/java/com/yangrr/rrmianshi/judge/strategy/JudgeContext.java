package com.yangrr.rrmianshi.judge.strategy;

import com.yangrr.rrmianshi.domain.OjQuestion;
import com.yangrr.rrmianshi.domain.OjQuestionSubmit;
import com.yangrr.rrmianshi.dto.question.JudgeCase;
import com.yangrr.rrmianshi.judge.codesandbox.model.JudgeInfo;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private OjQuestion question;

    private OjQuestionSubmit questionSubmit;

    private int code;

    /**
     * 接口信息
     */
    private String message;
    /**
     * 描述
     */
    private String description;

}
