package com.yangrr.rrmianshi.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 题目分类表，用于存储题目的分类信息
 *
 * @TableName question_category
 */
@TableName(value = "question_category")
@Data
public class QuestionCategory implements Serializable {
    /**
     * 分类的唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类的名称
     */
    @TableField(value = "type")
    private String type;

    /**
     * 分类对应的图片
     */
    @TableField(value = "type_img")
    private String typeImg;

    /**
     * 分类的描述信息
     */
    @TableField(value = "description")
    private String description;

    /**
     * 分类创建的时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 分类更新的时间
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