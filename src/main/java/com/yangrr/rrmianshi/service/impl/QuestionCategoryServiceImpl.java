package com.yangrr.rrmianshi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.domain.QuestionCategory;
import com.yangrr.rrmianshi.dto.AddQuestionCategoryDto;
import com.yangrr.rrmianshi.dto.UpdateQuestionCategoryDto;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.mapper.QuestionCategoryMapper;
import com.yangrr.rrmianshi.service.QuestionCategoryService;

import com.yangrr.rrmianshi.vo.QuestionCategoryVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 31841
 * @description 针对表【question_category(题目分类表，用于存储题目的分类信息)】的数据库操作Service实现
 * @createDate 2025-02-03 10:08:10
 */
@Service
public class QuestionCategoryServiceImpl extends ServiceImpl<QuestionCategoryMapper, QuestionCategory>
        implements QuestionCategoryService {

    @Override
    public List<QuestionCategoryVo> listQuestionCategory() {
        List<QuestionCategory> list = this.list();
        return list.stream().map(questionCategory -> {
            QuestionCategoryVo questionCategoryVo = new QuestionCategoryVo();
            BeanUtils.copyProperties(questionCategory, questionCategoryVo);
            return questionCategoryVo;
        }).toList();
    }

    @Override
    public Boolean addQuestionCategory(AddQuestionCategoryDto addQuestionCategoryDto) {
        String type = addQuestionCategoryDto.getType();
        String typeImg = addQuestionCategoryDto.getTypeImg();
        String description = addQuestionCategoryDto.getDescription();
        if (StringUtils.isAnyBlank(type, typeImg, description)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "参数不能为空");
        }
        QuestionCategory questionCategory = new QuestionCategory();
        BeanUtils.copyProperties(addQuestionCategoryDto, questionCategory);
        return this.save(questionCategory);
    }

    @Override
    public Boolean updateQuestionCategory(UpdateQuestionCategoryDto updateQuestionCategoryDto) {
        Integer id = updateQuestionCategoryDto.getId();
        String type = updateQuestionCategoryDto.getType();
        String typeImg = updateQuestionCategoryDto.getTypeImg();
        String description = updateQuestionCategoryDto.getDescription();

        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id不能为空");
        }

        if (StringUtils.isAnyBlank(type, typeImg, description)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "参数不能为空");
        }

        QuestionCategory questionCategory = new QuestionCategory();
        BeanUtils.copyProperties(updateQuestionCategoryDto, questionCategory);
        return this.updateById(questionCategory);
    }

    @Override
    public Boolean deleteQuestionCategory(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id不能为空");
        }
        return this.removeById(id);
    }

    @Override
    public QuestionCategoryVo getQuestionCategoryById(Long id) {
        QuestionCategory questionCategory = this.getById(id);
        if (questionCategory == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id不存在");
        }
        QuestionCategoryVo questionCategoryVo = new QuestionCategoryVo();
        BeanUtils.copyProperties(questionCategory, questionCategoryVo);
        return questionCategoryVo;
    }
}




