package com.yangrr.rrmianshi.filter;

import com.yangrr.rrmianshi.utils.UserThreadLocalUtil;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.vo.SafetyUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.yangrr.rrmianshi.constant.UserConstant.LOGIN_USER;

/**
 * @PATH com.yangrr.rrmianshi.filter.GlobleFilter
 * @Author YangRR
 * @CreateData 2025-02-11 09:28
 * @Description: 拦截器   将用户加入threadLocal   销毁threadLocal
 */
//实现springmvc拦截器
public class GlobalInterceptor implements HandlerInterceptor {
    private UsersService usersService;

    public GlobalInterceptor(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 当前登录用户
        SafetyUser currentUser = (SafetyUser) request.getSession().getAttribute(LOGIN_USER);
        //登陆了 将currentUser放到threadLocal中
        if(currentUser != null){
            UserThreadLocalUtil.set(currentUser);
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 销毁threadLocal
        UserThreadLocalUtil.remove();
    }
}
