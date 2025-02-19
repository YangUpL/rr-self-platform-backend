package com.yangrr.rrmianshi.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yangrr.rrmianshi.utils.BasicCheckUtils;
import com.yangrr.rrmianshi.utils.UserThreadLocalUtil;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.dto.UpdateUserDto;
import com.yangrr.rrmianshi.enums.UserRoleEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.facade.CheckFacade;
import com.yangrr.rrmianshi.facade.template.UpdateUserAvatar;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.vo.SafetyUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yangrr.rrmianshi.constant.UserConstant.LOGIN_USER;

/**
 * @PATH com.yangrr.rrmianshi.strategy.PersonalUpdateUser
 * @Author YangRR
 * @CreateData 2025-02-11 09:54
 * @Description:
 */

@Component
public class PersonalUpdateUser implements UpdateUserStrategy {

    @Autowired
    private UsersService usersService;

    @Override
    public Boolean updateUser(UpdateUserDto updateUserDto, HttpServletRequest request) {
        Long id = updateUserDto.getId();
        String username = updateUserDto.getUsername();
        String avatar = updateUserDto.getAvatar();
        String email = updateUserDto.getEmail();
        String description = updateUserDto.getDescription();
        Integer gender = updateUserDto.getGender();
        String githubUrl = updateUserDto.getGithubUrl();


        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id不能为空");
        }

        BasicCheckUtils.checkDescGithubGender(description, gender, githubUrl);
        //得到当前登录用户
        SafetyUser currentUser = UserThreadLocalUtil.get();
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        //门面模式校验用户名 密码 邮箱 角色 头像
        CheckFacade updateUserAvatar = new UpdateUserAvatar();
        updateUserDto.setAvatar(updateUserAvatar.check(username, email, UserThreadLocalUtil.get().getRole(), avatar));


        //只有自己和管理员可以修改用户(如果不是二者-->权限不足)
        if (!(UserRoleEnum.getRoleEnum(currentUser.getRole()) == UserRoleEnum.ADMIN) &&
                !currentUser.getId().equals(updateUserDto.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "权限不足");
        }

        //查看username和email是否存在
        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getUsername, username)
                .or()
                .eq(Users::getEmail, email);
        //只查三列
        usersLambdaQueryWrapper.select(Users::getUsername, Users::getEmail, Users::getId, Users::getRole);
        Users user = usersService.getOne(usersLambdaQueryWrapper);
        if (user != null) {
            //存在则判断是不是本人，不是-->抛异常
            if (!user.getId().equals(id)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户名或邮箱已存在");
            }
        }
        updateUserDto.setRole(currentUser.getRole());
        //修改用户
        Users newUser = new Users();
        BeanUtils.copyProperties(updateUserDto, newUser);
        boolean update = usersService.updateById(newUser);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改用户失败");
        }
        //更新session中的user
        SafetyUser safetyUser = new SafetyUser();
        BeanUtils.copyProperties(newUser, safetyUser);
        request.getSession().setAttribute(LOGIN_USER, safetyUser);
        return update;
    }
}
