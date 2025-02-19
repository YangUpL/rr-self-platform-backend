package com.yangrr.rrmianshi.judge;

import cn.hutool.json.JSONUtil;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.domain.OjQuestion;
import com.yangrr.rrmianshi.domain.OjQuestionSubmit;
import com.yangrr.rrmianshi.dto.question.JudgeCase;
import com.yangrr.rrmianshi.enums.JudgeInfoMessageEnum;
import com.yangrr.rrmianshi.enums.QuestionSubmitStatusEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.judge.codesandbox.CodeSandbox;
import com.yangrr.rrmianshi.judge.codesandbox.CodeSandboxFactory;
import com.yangrr.rrmianshi.judge.codesandbox.CodeSandboxProxy;
import com.yangrr.rrmianshi.judge.codesandbox.model.ExecuteCodeRequest;
import com.yangrr.rrmianshi.judge.codesandbox.model.ExecuteCodeResponse;
import com.yangrr.rrmianshi.judge.codesandbox.model.JudgeInfo;
import com.yangrr.rrmianshi.judge.strategy.JudgeContext;
import com.yangrr.rrmianshi.service.OjQuestionService;
import com.yangrr.rrmianshi.service.OjQuestionSubmitService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private OjQuestionService questionService;

    @Resource
    private OjQuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public OjQuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        OjQuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        OjQuestion question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        OjQuestionSubmit questionSubmitUpdate = new OjQuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）工厂 得到所需要的沙箱
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        //代理  增强沙箱
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        String template = question.getTemplate();

        //构建请求参数
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .template(template)
                .build();
        JudgeInfo judgeInfo = new JudgeInfo();
        //调用沙箱 获取响应！！！！！！！！！
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        //得到输出结果
        List<String> outputList = executeCodeResponse.getOutputList();

        // 5）根据沙箱的执行结果，设置题目的判题状态和信息  传给判题逻辑
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        judgeContext.setCode(executeCodeResponse.getCode());
        judgeContext.setMessage(executeCodeResponse.getMessage());
        judgeContext.setDescription(executeCodeResponse.getDescription());

        try{
            //在这！！ 根据沙箱的结果，设置题目的判题状态和信息
            judgeInfo = judgeManager.doJudge(judgeContext);

            // 6）修改数据库中的判题结果
            questionSubmitUpdate = new OjQuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update = questionSubmitService.updateById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
        }catch (Exception e){
            // 6）修改数据库中的判题结果
            questionSubmitUpdate = new OjQuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update = questionSubmitService.updateById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
        }

        //统计通过的人数
        if (judgeInfo.getMessage().contains("Accepted")) {
            OjQuestion questionNew = new OjQuestion();
            questionNew.setAcceptedNum(question.getAcceptedNum() + 1);
            questionNew.setId(questionId);
            questionService.updateById(questionNew);
        }
        return questionSubmitService.getById(questionId);
    }
}
