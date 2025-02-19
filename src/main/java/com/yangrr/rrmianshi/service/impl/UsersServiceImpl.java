package com.yangrr.rrmianshi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangrr.rrmianshi.common.BaseResponse;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.common.ResultUtils;
import com.yangrr.rrmianshi.constant.CommonConstant;
import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.dto.*;
import com.yangrr.rrmianshi.enums.UserRoleEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.facade.CheckFacade;
import com.yangrr.rrmianshi.factory.LoginFactory;
import com.yangrr.rrmianshi.factory.UpdateUserFactory;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.mapper.UsersMapper;
import com.yangrr.rrmianshi.strategy.LoginStrategy;
import com.yangrr.rrmianshi.facade.template.AddUserAvatar;
import com.yangrr.rrmianshi.strategy.UpdateUserStrategy;
import com.yangrr.rrmianshi.utils.BasicCheckUtils;
import com.yangrr.rrmianshi.utils.CommonUtils;
import com.yangrr.rrmianshi.utils.UserThreadLocalUtil;
import com.yangrr.rrmianshi.vo.PublicKeyVo;
import com.yangrr.rrmianshi.vo.SafetyUser;
import com.yangrr.rrmianshi.vo.SafetyUserPageVo;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.yangrr.rrmianshi.constant.UserConstant.DEFAULT_PASSWORD;
import static com.yangrr.rrmianshi.constant.UserConstant.LOGIN_USER;

/**
 * @author 31841
 * @description 针对表【users(用户信息表，存储用户的基本信息和状态)】的数据库操作Service实现
 * @createDate 2025-01-27 14:46:07
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
        implements UsersService {

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LoginFactory loginFactory;

    @Autowired
    private UpdateUserFactory updateUserFactory;

    @Override
    public BaseResponse<Long> login(LoginDto loginDto, HttpServletRequest request){
        //得到登陆策略
        LoginStrategy loginStrategy = loginFactory.getLoginStrategy(loginDto.getLoginType());

        //执行登录逻辑
        SafetyUser user = loginStrategy.login(loginDto);

        //将用户信息存入session
        request.getSession().setAttribute(LOGIN_USER, user);
        return ResultUtils.success(user.getId());
    }

    @Override
    public BaseResponse<SafetyUser> register(RegisterDto registerDto) {
        String username = registerDto.getUsername();
        String password = registerDto.getPassword();
        String confirmPassword = registerDto.getConfirmPassword();
        String email = registerDto.getEmail();
        String code = registerDto.getCode();

        if (StringUtils.isAnyBlank(username, password, confirmPassword, email, code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

//        // 校验用户名和密码
//        BasicCheckUtils.checkNameAndPwd(username, password);
//
//        //校验邮箱格式是否合法
//        BasicCheckUtils.checkEmail(email);

        //门面模式校验用户名 密码 邮箱
        CheckFacade.check(username, password, email);

        // 校验验证码
        if (!code.equals(redisTemplate.opsForValue().get(email))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }

        // 校验用户名是否存在
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).or().eq("email", email);
        Users user = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或邮箱已存在");
        }

        //密码MD5加密
        String safePwd = MD5.create().digestHex16(password, StandardCharsets.UTF_8);

        // 创建用户
        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setPassword(safePwd);
        newUser.setEmail(email);
        boolean saveResult = this.save(newUser);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        //将user拷贝被safetyUser
        SafetyUser safetyUser = new SafetyUser();
        BeanUtils.copyProperties(newUser, safetyUser);


        return ResultUtils.success(safetyUser);
    }

    @Override
    public void sendMail(EmailDto emailDto) {
        String email = emailDto.getEmail();

        // 校验邮箱格式是否合法
        BasicCheckUtils.checkEmail(email);

        // 校验验证码
        commonUtils.sendMailUtils(emailDto);
    }

    @Override
    public SafetyUser getCurrentUser(HttpServletRequest request) {
        SafetyUser currentUser = (SafetyUser) request.getSession().getAttribute(LOGIN_USER);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return currentUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(LOGIN_USER);
        SafetyUser user = (SafetyUser) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(SafetyUser user) {
        return user != null && (UserRoleEnum.getRoleEnum(user.getRole()) == UserRoleEnum.ADMIN);
    }

    @Override
    public Boolean addUser(AddUserDto addUserDto) {
        String username = addUserDto.getUsername();
        String defaultPassword = MD5.create().digestHex16(DEFAULT_PASSWORD, StandardCharsets.UTF_8);
        String avatar = addUserDto.getAvatar();
        String email = addUserDto.getEmail();
        Integer role = addUserDto.getRole();
        String description = addUserDto.getDescription();
        Integer gender = addUserDto.getGender();
        String githubUrl = addUserDto.getGithubUrl();

        BasicCheckUtils.checkDescGithubGender(description, gender, githubUrl);

//        // 校验用户名和密码
//        BasicCheckUtils.checkNameAndPwd(username, defaultPassword);
//
//        // 校验邮箱格式是否合法
//        BasicCheckUtils.checkEmail(email);
//
//        // 校验角色是否合法
//        BasicCheckUtils.checkRole(role);
//
//        //校验头像
//        if (BasicCheckUtils.checkAvatar(avatar)) {
//            //默认头像
//            addUserDto.setAvatar("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF");
//        }

        //门面模式校验用户名 密码 邮箱 角色 头像
        CheckFacade addUserAvatar = new AddUserAvatar();
        addUserDto.setAvatar(addUserAvatar.check(username, defaultPassword, email, role, avatar));


        //查看username和email是否存在
        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getUsername, username)
                .or()
                .eq(Users::getEmail, email);
        //只查两列
        usersLambdaQueryWrapper.select(Users::getUsername, Users::getEmail);
        Users userOld = this.getOne(usersLambdaQueryWrapper);
        if (userOld != null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户名或邮箱已存在");
        }


        // 创建用户
        Users user = new Users();
        BeanUtils.copyProperties(addUserDto, user);
        user.setPassword(defaultPassword);
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加用户失败");
        }
        return save;
    }

    @Override
    public SafetyUserPageVo getUserList(GlobalDto globalDto) {
        Integer pageNum = globalDto.getPageNum();
        Integer pageSize = globalDto.getPageSize();
        String sorterName = globalDto.getSorterName();
        String sorterOrder = globalDto.getSorterOrder();


        QueryWrapper<Users> userQueryWrapper = new QueryWrapper<Users>();
        userQueryWrapper.orderByDesc(sorterOrder.equals(CommonConstant.SORT_ORDER_DESC), sorterName);
        userQueryWrapper.orderByAsc(sorterOrder.equals(CommonConstant.SORT_ORDER_ASC), sorterName);


        //分页查询
        Page<Users> usersPage = new Page<>(pageNum, pageSize);
        Page<Users> page = this.page(usersPage, userQueryWrapper);

        List<SafetyUser> safetyUserList = page.getRecords().stream().map(user -> {
            SafetyUser safetyUser = new SafetyUser();
            BeanUtils.copyProperties(user, safetyUser);
            return safetyUser;
        }).toList();


        SafetyUserPageVo safetyUserPageVo = new SafetyUserPageVo();
        safetyUserPageVo.setList(safetyUserList);
        safetyUserPageVo.setTotal((int) page.getTotal());
        safetyUserPageVo.setPageSize((int) page.getSize());


        return safetyUserPageVo;
    }


    @Override
    public Boolean updateUser(UpdateUserDto updateUserDto, HttpServletRequest request) {
        UpdateUserStrategy updateUserStrategy = updateUserFactory.getUpdateUserStrategy(updateUserDto.getId(), UserThreadLocalUtil.get().getRole());
        return updateUserStrategy.updateUser(updateUserDto, request);
    }

    @Override
    public Boolean deleteUser(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id不能为空");
        }
        return this.removeById(id);
    }

    @Override
    public SafetyUser getUserById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id不能为空");
        }
        Users user = this.getById(id);
        if (ObjectUtil.isNull(user)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        SafetyUser safetyUser = new SafetyUser();
        BeanUtils.copyProperties(user, safetyUser);

        return safetyUser;
    }

    @Override
    public Boolean resetPassword(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id不能为空");
        }
        String defaultPassword = MD5.create().digestHex16(DEFAULT_PASSWORD, StandardCharsets.UTF_8);

        return this.update().set("password", defaultPassword).eq("id", id).update();
    }
}




