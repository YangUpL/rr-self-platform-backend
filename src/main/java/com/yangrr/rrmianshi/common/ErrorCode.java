package com.yangrr.rrmianshi.common;

/**
 * @PATH com.yangrr.center.common.ErrorCode
 * @Author YangRR
 * @CreateData 2024-07-02 13:41
 * @Description 错误码
 */
public enum ErrorCode {
    /**
     *  40000 参数错误
     */
    PARAMS_ERROR(40000, "请求参数错误", ""),
    /**
     * 40001 数据为空
     */
    NULL_ERROR(40001,"请求数据为空",""),
    /**
     * 40100 未登录
     */
    NOT_LOGIN(40100, "未登录", ""),
    /**
     * 40101 无权限
     */
    NO_AUTH(40101, "无权限", ""),
    /**
     * 50000 内部系统错误
     */
    SYSTEM_ERROR(50000, "系统内部异常", ""),

    //数据库未查到数据
    DATA_NOT_FOUND(50001, "数据未找到", ""),

    //登陆类型错误
    LOGIN_TYPE_ERROR(50002, "登陆类型错误", ""),
    NOT_FOUND_ERROR(50003, "数据不存在", ""),
    UPDATE_ERROR(50004, "无权更新", ""),
    OPERATION_ERROR(50005, "操作失败",""),
    API_REQUEST_ERROR(50016, "接口调用失败",""),
    /**
     * 成功
     */
    SUCCESS(20000, "success", "");
    /**
     * 状态码信息
     */
    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String description;
    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
