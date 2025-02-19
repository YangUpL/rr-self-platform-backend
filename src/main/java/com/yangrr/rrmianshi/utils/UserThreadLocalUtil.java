package com.yangrr.rrmianshi.utils;

import com.yangrr.rrmianshi.vo.SafetyUser;

/**
 * @PATH com.yangrr.rrmianshi.ThreadLocalUtil
 * @Author YangRR
 * @CreateData 2025-02-11 09:31
 * @Description:
 */
public class UserThreadLocalUtil {
    private static final ThreadLocal<SafetyUser> THREAD_LOCAL = new ThreadLocal<>();
    public static void set(SafetyUser SafetyUser) {
        THREAD_LOCAL.set(SafetyUser);
    }
    public static SafetyUser get() {
        return THREAD_LOCAL.get();
    }
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
