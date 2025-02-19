package com.yangrr.rrmianshi.vo;

/**
 * @PATH com.yangrr.rrmianshi.vo.SafetyUser
 * @Author YangRR
 * @CreateData 2025-01-28 17:53
 * @Description:
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 脱敏用户
 */
@Data
public class SafetyUser {
    /**
     * 用户唯一标识，自增主键
     */
    private Long id;

    /**
     * 用户登录使用的用户名，必须唯一
     */
    private String username;

    /**
     * 用户头像的 URL 或存储路径
     */
    private String avatar;

    /**
     * 用户电子邮箱，用于找回密码等操作，必须唯一
     */
    private String email;

    /**
     * 用户角色：0-普通用户，1-管理员，2-VIP 用户,3-封禁
     */
    private Integer role;

    /**
     * 用户记录创建时间
     */
    private Date createTime;

    /**
     * 用户记录更新时间
     */
    private Date updateTime;

    private String description;

    private Integer gender;

    private String githubUrl;
}
