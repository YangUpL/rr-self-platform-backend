package com.yangrr.rrmianshi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目
 * @TableName oj_question
 */
@TableName(value ="oj_question")
@Data
public class OjQuestion implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 标签列表（json 数组）
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 题目答案
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 题目提交数
     */
    @TableField(value = "submit_num")
    private Integer submitNum;

    /**
     * 题目通过数
     */
    @TableField(value = "accepted_num")
    private Integer acceptedNum;

    /**
     * 判题用例（json 数组）输入输出
     */
    @TableField(value = "judge_case")
    private String judgeCase;

    /**
     * 判题配置（json 对象） 时间内存
     */
    @TableField(value = "judge_config")
    private String judgeConfig;

    /**
     * 创建用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(value = "template")
    private String template;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}