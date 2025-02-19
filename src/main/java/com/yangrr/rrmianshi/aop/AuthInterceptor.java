package com.yangrr.rrmianshi.aop;


import com.yangrr.rrmianshi.annotation.AuthCheck;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.enums.UserRoleEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.vo.SafetyUser;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



/**
 * 权限校验 AOP
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UsersService usersService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        SafetyUser currentUser = usersService.getCurrentUser(request);
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            Integer mustRoleInt = Integer.parseInt(mustRole);
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getRoleEnum(mustRoleInt);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            Integer role = currentUser.getRole();
            // 如果被封号，直接拒绝
            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            // 必须有管理员权限
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRoleInt.equals(role)) {
                    throw new BusinessException(ErrorCode.NO_AUTH);
                }
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

