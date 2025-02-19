package com.yangrr.rrmianshi.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户信息表，存储用户的基本信息和状态
 * @TableName users
 */
@TableName(value ="users")
@Data
public class Users implements Serializable {
    /**
     * 用户唯一标识，自增主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户登录使用的用户名，必须唯一
     */
    @TableField(value = "username")
    private String username;

    /**
     * 用户登录密码，经过加密存储
     */
    @TableField(value = "password")
    private String password;

    /**
     * 用户头像的 URL 或存储路径
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 用户电子邮箱，用于找回密码等操作，必须唯一
     */
    @TableField(value = "email")
    private String email;

    /**
     * 用户角色：0-普通用户，1-管理员，2-VIP 用户，3-封号
     */
    @TableField(value = "role")
    private Integer role;

    @TableField(value = "description")
    private String description;

    @TableField(value = "gender")
    private Integer gender;

    @TableField(value = "github_url")
    private String githubUrl;

    /**
     * 用户记录创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 用户记录更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 用户删除标记：0-未删除，1-已删除
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}