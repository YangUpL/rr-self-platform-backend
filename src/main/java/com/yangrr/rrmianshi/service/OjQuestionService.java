package com.yangrr.rrmianshi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangrr.rrmianshi.domain.OjQuestion;
import com.yangrr.rrmianshi.dto.question.QuestionQueryRequest;
import com.yangrr.rrmianshi.vo.OjQuestionVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 31841
* @description 针对表【oj_question(题目)】的数据库操作Service
* @createDate 2025-02-12 09:58:41
*/
public interface OjQuestionService extends IService<OjQuestion> {

    void validQuestion(OjQuestion question, boolean add);

    QueryWrapper<OjQuestion> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    OjQuestionVO getQuestionVO(OjQuestion question, HttpServletRequest request);

    Page<OjQuestionVO> getQuestionVOPage(Page<OjQuestion> questionPage, HttpServletRequest request);
}
