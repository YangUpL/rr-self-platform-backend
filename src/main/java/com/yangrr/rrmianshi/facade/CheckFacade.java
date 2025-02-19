package com.yangrr.rrmianshi.facade;

import cn.hutool.core.util.ObjectUtil;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.enums.QuestionDifferenceEnum;
import com.yangrr.rrmianshi.enums.QuestionNeedVipEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.utils.BasicCheckUtils;

import java.util.List;

/**
 * @PATH com.yangrr.rrmianshi.facade.CheckFacade
 * @Author YangRR
 * @CreateData 2025-01-29 13:29
 * @Description:
 */

/**
 * 检查的门面
 */
public abstract class CheckFacade {

    public static void check(String username, String password, String email) {
        //校验用户名和密码
        BasicCheckUtils.checkNameAndPwd(username, password);
        //校验邮箱
        BasicCheckUtils.checkEmail(email);
    }

    public String check(String username, String password, String email, Integer role, String avatar) {
        //校验用户名和密码
        BasicCheckUtils.checkNameAndPwd(username, password);
        //校验邮箱
        BasicCheckUtils.checkEmail(email);
        //校验角色
        BasicCheckUtils.checkRole(role);
        //校验头像  null-->数据库不更改
        return this.checkAvatar(avatar);
    }


    public String check(String username, String email, Integer role, String avatar) {
        //校验用户名和密码
        BasicCheckUtils.checkName(username);
        //校验邮箱
        BasicCheckUtils.checkEmail(email);
        //校验角色
        BasicCheckUtils.checkRole(role);
        //校验头像  null-->数据库不更改
        return this.checkAvatar(avatar);
    }


    //校验头像抽象方法
    public abstract String checkAvatar(String avatar);

    public static void check(String question, String answer, Integer difficulty, Integer needVip, List<String> taglist) {
        if (ObjectUtil.hasEmpty(question, answer, difficulty, needVip, taglist)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "参数不能为空");
        }

//        if (QuestionCatagoryEnum.getEnumByCode(tid) == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目类型错误");
//        }
        if (QuestionDifferenceEnum.getEnumByCode(difficulty) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目难度错误");
        }
        if (QuestionNeedVipEnum.getEnum(needVip) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "是否需要会员错误");
        }
    }


}
