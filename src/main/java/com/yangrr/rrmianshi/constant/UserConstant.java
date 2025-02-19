package com.yangrr.rrmianshi.constant;

/**
 * @PATH com.yangrr.rrmianshi.constant.UserConstant
 * @Author YangRR
 * @CreateData 2025-01-29 09:34
 * @Description:
 */
public class UserConstant {
    public static final String LOGIN_USER = "login_user";

    //默认头像
    public static final String DEFAULT_AVATAR = "https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF";

    //默认密码
    public static final String DEFAULT_PASSWORD = "123456";

    //为了适配数据库的0-user 1-admin 2-vip 3-ban
    public static final String USER = "0";
    public static final String ADMIN = "1";
    public static final String VIP = "2";
    public static final String BAN = "3";
}
