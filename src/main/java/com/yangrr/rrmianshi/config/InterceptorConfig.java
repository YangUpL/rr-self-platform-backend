package com.yangrr.rrmianshi.config;

import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.filter.GlobalInterceptor;
import com.yangrr.rrmianshi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @PATH com.yangrr.rrmianshi.config.InterceptorConfig
 * @Author YangRR
 * @CreateData 2025-02-11 09:38
 * @Description:
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private UsersService usersService;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GlobalInterceptor(usersService)).addPathPatterns("/**");
    }
}
