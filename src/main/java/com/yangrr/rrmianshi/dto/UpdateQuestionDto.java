package com.yangrr.rrmianshi.dto;

import lombok.Data;

import java.util.List;

/**
 * @PATH com.yangrr.rrmianshi.dto.AddQuestionDto
 * @Author YangRR
 * @CreateData 2025-02-04 13:48
 * @Description:
 */

@Data
public class UpdateQuestionDto {

    private Long id;

    /**
     * 题目的具体内容
     */
    private String question;

    /**
     * 题目的答案
     */
    private String answer;

    /**
     * 分类表的外键，关联题目所属的分类
     */
    private Integer tid;

    /**
     * 题目的难度，1-简单  3-中等  2-困难
     */
    private Integer difficulty;

    /**
     * 是否需要 VIP 权限才能查看该题目，0-不需要  1-需要
     */
    private Integer needVip;

    /**
     * 题目的标签列表，存 JSON 串，如 "java","设计模式"
     */
    private List<String> taglist;
}
