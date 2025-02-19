package com.yangrr.rrmianshi.dto;

import lombok.Data;

/**
 * @PATH com.yangrr.rrmianshi.dto.QuestionCatagoryDto
 * @Author YangRR
 * @CreateData 2025-02-18 11:19
 * @Description:
 */
@Data
public class AddQuestionCategoryDto {
    /**
     * 分类的名称
     */
    private String type;

    /**
     * 分类对应的图片
     */
    private String typeImg;

    /**
     * 分类的描述信息
     */
    private String description;
}
