package com.yangrr.rrmianshi.vo;

import cn.hutool.json.JSONUtil;

import com.yangrr.rrmianshi.domain.OjQuestion;
import com.yangrr.rrmianshi.dto.question.JudgeConfig;
import com.yangrr.rrmianshi.dto.question.OjTemplate;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 * @TableName question
 */
@Data
public class OjQuestionVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * OJ模板
     */
    private OjTemplate template;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

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
     * 创建题目人的信息
     */
    private SafetyUser userVO;

    /**
     * 包装类转对象
     *
     * @param ojQuestionVO
     * @return
     */
    public static OjQuestion voToObj(OjQuestionVO ojQuestionVO) {
        if (ojQuestionVO == null) {
            return null;
        }
        OjQuestion question = new OjQuestion();
        BeanUtils.copyProperties(ojQuestionVO, question);
        List<String> tagList = ojQuestionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = ojQuestionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        OjTemplate template1 = ojQuestionVO.getTemplate();
        if (template1 != null) {
            question.setTemplate(JSONUtil.toJsonStr(template1));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static OjQuestionVO objToVo(OjQuestion question) {
        if (question == null) {
            return null;
        }
        OjQuestionVO ojQuestionVO = new OjQuestionVO();
        BeanUtils.copyProperties(question, ojQuestionVO);
        List<String> tagList = JSONUtil.toList(question.getTags(), String.class);
        ojQuestionVO.setTags(tagList);
        String judgeConfigStr = question.getJudgeConfig();
        ojQuestionVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        String template = question.getTemplate();
        ojQuestionVO.setTemplate(JSONUtil.toBean(template, OjTemplate.class));
        return ojQuestionVO;
    }

    private static final long serialVersionUID = 1L;
}