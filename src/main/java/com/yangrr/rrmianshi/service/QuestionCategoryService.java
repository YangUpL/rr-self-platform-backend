package com.yangrr.rrmianshi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangrr.rrmianshi.domain.QuestionCategory;
import com.yangrr.rrmianshi.dto.AddQuestionCategoryDto;
import com.yangrr.rrmianshi.dto.UpdateQuestionCategoryDto;
import com.yangrr.rrmianshi.vo.QuestionCategoryVo;

import java.util.List;

/**
* @author 31841
* @description 针对表【question_category(题目分类表，用于存储题目的分类信息)】的数据库操作Service
* @createDate 2025-02-03 10:08:10
*/
public interface QuestionCategoryService extends IService<QuestionCategory> {

    List<QuestionCategoryVo> listQuestionCategory();

    Boolean addQuestionCategory(AddQuestionCategoryDto addQuestionCategoryDto);

    Boolean updateQuestionCategory(UpdateQuestionCategoryDto updateQuestionCategoryDto);

    Boolean deleteQuestionCategory(Long id);

    QuestionCategoryVo getQuestionCategoryById(Long id);
}
