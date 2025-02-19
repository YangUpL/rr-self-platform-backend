package com.yangrr.rrmianshi.dto;

import lombok.Data;

/**
 * @PATH com.yangrr.rrmianshi.dto.LoginDto
 * @Author YangRR
 * @CreateData 2025-01-27 15:16
 * @Description:
 */
@Data
public class RegisterDto {
    //用户名
    private String username;

    //密码
    private String password;

    //密码
    private String confirmPassword;

    //邮箱
    private String email;

    //验证码
    private String code;
}
