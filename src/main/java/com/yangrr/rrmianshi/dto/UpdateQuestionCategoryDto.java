package com.yangrr.rrmianshi.dto;

import lombok.Data;

/**
 * @PATH com.yangrr.rrmianshi.vo.QuestionCategoryVo
 * @Author YangRR
 * @CreateData 2025-02-18 09:56
 * @Description:
 */
@Data
public class UpdateQuestionCategoryDto {
    /**
     * 分类的唯一标识
     */
    private Integer id;

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
