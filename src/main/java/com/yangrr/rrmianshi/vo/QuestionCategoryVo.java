package com.yangrr.rrmianshi.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @PATH com.yangrr.rrmianshi.vo.QuestionCategoryVo
 * @Author YangRR
 * @CreateData 2025-02-18 09:56
 * @Description:
 */
@Data
public class QuestionCategoryVo {
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

    /**
     * 分类创建的时间
     */
    private Date createTime;

    /**
     * 分类更新的时间
     */
    private Date updateTime;
}
