package com.yangrr.rrmianshi.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.yangrr.rrmianshi.dto.GlobalDto;
import lombok.Data;

/**
 * 题目表，用于存储具体的题目信息
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable  {
    /**
     * 题目的唯一标识
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目的具体内容
     */
    @TableField(value = "question")
    private String question;

    /**
     * 题目的答案
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 分类表的外键，关联题目所属的分类
     */
    @TableField(value = "tid")
    private Integer tid;

    /**
     * 题目的难度，1-简单  3-中等  2-困难
     */
    @TableField(value = "difficulty")
    private Integer difficulty;

    /**
     * 是否需要 VIP 权限才能查看该题目，0-不需要  1-需要
     */
    @TableField(value = "need_vip")
    private Integer needVip;

    /**
     * 题目的标签列表，存 JSON 串，如 "java","设计模式"
     */
    @TableField(value = "tagList")
    private String taglist;

    /**
     * 题目创建的时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 题目更新的时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除，0-未删除  1-已删除
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}