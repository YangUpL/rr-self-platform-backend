package com.yangrr.rrmianshi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangrr.rrmianshi.domain.Question;
import com.yangrr.rrmianshi.dto.AddQuestionDto;
import com.yangrr.rrmianshi.dto.ListQuestionDto;
import com.yangrr.rrmianshi.dto.UpdateQuestionDto;
import com.yangrr.rrmianshi.excel.QuestionInfo;
import com.yangrr.rrmianshi.vo.PublicKeyVo;
import com.yangrr.rrmianshi.vo.QuestionNoAnswerPageVo;
import com.yangrr.rrmianshi.vo.QuestionNoAnswerVo;
import com.yangrr.rrmianshi.vo.QuestionVo;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
* @author 31841
* @description 针对表【question(题目表，用于存储具体的题目信息)】的数据库操作Service
* @createDate 2025-02-03 10:07:56
*/
public interface QuestionService extends IService<Question> {

    Boolean addQuestion(AddQuestionDto addQuestionDto);

    Boolean updateQuestion(UpdateQuestionDto updateQuestionDto);

    QuestionVo getQuestionById(String id , HttpServletRequest request);

    QuestionNoAnswerPageVo listQuestion(ListQuestionDto listQuestionDto);

    Boolean deleteQuestionById(Long id);

    PublicKeyVo getPublicKey();
}
