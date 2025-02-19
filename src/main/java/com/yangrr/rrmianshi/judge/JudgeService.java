package com.yangrr.rrmianshi.judge;

import com.yangrr.rrmianshi.domain.OjQuestionSubmit;

/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    OjQuestionSubmit doJudge(long questionSubmitId);
}
