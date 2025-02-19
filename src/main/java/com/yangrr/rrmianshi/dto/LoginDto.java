package com.yangrr.rrmianshi.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @PATH com.yangrr.rrmianshi.dto.LoginDto
 * @Author YangRR
 * @CreateData 2025-01-27 15:16
 * @Description:
 */
@Data
public class LoginDto {
    //用户名
    private String username;
    //密码
    private String password;
    //邮箱
    private String email;
    //验证码
    private String code;
    //登录类型  0-email  1-username
    private Integer loginType;
}
