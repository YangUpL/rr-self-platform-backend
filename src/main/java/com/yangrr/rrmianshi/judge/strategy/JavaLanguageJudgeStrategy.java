package com.yangrr.rrmianshi.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.yangrr.rrmianshi.domain.OjQuestion;
import com.yangrr.rrmianshi.dto.question.JudgeCase;
import com.yangrr.rrmianshi.dto.question.JudgeConfig;
import com.yangrr.rrmianshi.enums.JudgeInfoMessageEnum;
import com.yangrr.rrmianshi.judge.codesandbox.model.JudgeInfo;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Java 程序的判题策略
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        try{
            int code = judgeContext.getCode();
            String message = judgeContext.getMessage();
            String description = judgeContext.getDescription();
            JudgeInfo judgeInfo = new JudgeInfo();
            if (code == 50000 && message.equals("Compile Error")) {
                JudgeInfoMessageEnum compileError = JudgeInfoMessageEnum.COMPILE_ERROR;
                judgeInfo.setMessage(compileError.getText());
                return judgeInfo;
            }
            if (code == 50002 && message.equals("Dangerous Operation")) {
                JudgeInfoMessageEnum dangerousOperation = JudgeInfoMessageEnum.DANGEROUS_OPERATION;
                judgeInfo.setMessage(dangerousOperation.getText());
                return judgeInfo;
            }
            judgeInfo = judgeContext.getJudgeInfo();
            // 获取判题信息
            Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
            Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
            List<String> inputList = judgeContext.getInputList();
            List<String> outputList = judgeContext.getOutputList();
            OjQuestion question = judgeContext.getQuestion();
            List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
            //假设ACCEPTED
            JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
            judgeInfoResponse.setMemory(memory);
            judgeInfoResponse.setTime(time);
            // 先判断沙箱执行的结果输出数量是否和预期输出数量相等
            if (!inputList.isEmpty() && outputList.size() != inputList.size()) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
            // 依次判断每一项输出和预期输出是否相等
            for (int i = 0; i < judgeCaseList.size(); i++) {
                JudgeCase judgeCase = judgeCaseList.get(i);
                if (!judgeCase.getOutput().equals(outputList.get(i))) {
                    judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                    judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                    return judgeInfoResponse;
                }
            }

            // 判断题目限制
            String judgeConfigStr = question.getJudgeConfig();
            JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
            Long needMemoryLimit = judgeConfig.getMemoryLimit();
            Long needTimeLimit = judgeConfig.getTimeLimit();
            if (memory > needMemoryLimit) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
            // Java 程序本身需要额外执行 10 秒钟
//        long JAVA_PROGRAM_TIME_COST = 10000L;
//        if ((time - JAVA_PROGRAM_TIME_COST) > needTimeLimit) {
            if (time > needTimeLimit) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getText());
                return judgeInfoResponse;
            }
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getText());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return judgeInfoResponse;
    }
}
