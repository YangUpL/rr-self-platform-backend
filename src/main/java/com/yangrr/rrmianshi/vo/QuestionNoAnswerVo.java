package com.yangrr.rrmianshi.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @PATH com.yangrr.rrmianshi.vo.QuestionVo
 * @Author YangRR
 * @CreateData 2025-02-05 10:38
 * @Description:
 */
@Data
public class QuestionNoAnswerVo{
    private Long id;

    /**
     * 题目的具体内容
     */
    private String question;

    /**
     * 分类表的外键，关联题目所属的分类
     */
    private Integer tid;

    /**
     * 题目的难度，0-简单  3-中等  2-困难
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

    /**
     * 题目创建的时间
     */
    private Date createTime;

    /**
     * 题目更新的时间
     */
    private Date updateTime;
}
