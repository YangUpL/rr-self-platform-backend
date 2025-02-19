package com.yangrr.rrmianshi.vo;

import cn.hutool.json.JSONUtil;
import com.yangrr.rrmianshi.domain.OjQuestionSubmit;
import com.yangrr.rrmianshi.judge.codesandbox.model.JudgeInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目提交封装类
 * @TableName question
 */
@Data
public class OjQuestionSubmitVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 提交用户信息
     */
    private SafetyUser userVO;

    /**
     * 对应题目信息
     */
    private OjQuestionVO questionVO;

    /**
     * 包装类转对象
     *
     * @param ojQuestionSubmitVO
     * @return
     */
    public static OjQuestionSubmit voToObj(OjQuestionSubmitVO ojQuestionSubmitVO) {
        if (ojQuestionSubmitVO == null) {
            return null;
        }
        OjQuestionSubmit questionSubmit = new OjQuestionSubmit();
        BeanUtils.copyProperties(ojQuestionSubmitVO, questionSubmit);
        JudgeInfo judgeInfoObj = ojQuestionSubmitVO.getJudgeInfo();
        if (judgeInfoObj != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoObj));
        }
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static OjQuestionSubmitVO objToVo(OjQuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        OjQuestionSubmitVO ojQuestionSubmitVO = new OjQuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, ojQuestionSubmitVO);
        String judgeInfoStr = questionSubmit.getJudgeInfo();
        ojQuestionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfoStr, JudgeInfo.class));
        return ojQuestionSubmitVO;
    }

    private static final long serialVersionUID = 1L;
}