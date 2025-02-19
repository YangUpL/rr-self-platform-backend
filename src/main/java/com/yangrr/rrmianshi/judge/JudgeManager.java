package com.yangrr.rrmianshi.judge;


import com.yangrr.rrmianshi.domain.OjQuestionSubmit;
import com.yangrr.rrmianshi.judge.codesandbox.model.JudgeInfo;
import com.yangrr.rrmianshi.judge.strategy.DefaultJudgeStrategy;
import com.yangrr.rrmianshi.judge.strategy.JavaLanguageJudgeStrategy;
import com.yangrr.rrmianshi.judge.strategy.JudgeContext;
import com.yangrr.rrmianshi.judge.strategy.JudgeStrategy;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        OjQuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();

        //策略模式！找相应语言的策略
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }

        //正式执行   对比
        return judgeStrategy.doJudge(judgeContext);
    }

}
