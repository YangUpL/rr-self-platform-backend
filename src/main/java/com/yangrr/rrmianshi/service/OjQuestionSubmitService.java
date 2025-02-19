package com.yangrr.rrmianshi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangrr.rrmianshi.domain.OjQuestionSubmit;
import com.yangrr.rrmianshi.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yangrr.rrmianshi.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yangrr.rrmianshi.vo.OjQuestionSubmitVO;
import com.yangrr.rrmianshi.vo.SafetyUser;

/**
* @author 31841
* @description 针对表【oj_question_submit(题目提交)】的数据库操作Service
* @createDate 2025-02-12 09:58:45
*/
public interface OjQuestionSubmitService extends IService<OjQuestionSubmit> {

    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, SafetyUser loginUser);

    QueryWrapper<OjQuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    OjQuestionSubmitVO getQuestionSubmitVO(OjQuestionSubmit questionSubmit, SafetyUser loginUser);

    Page<OjQuestionSubmitVO> getQuestionSubmitVOPage(Page<OjQuestionSubmit> questionSubmitPage, SafetyUser loginUser);
}
