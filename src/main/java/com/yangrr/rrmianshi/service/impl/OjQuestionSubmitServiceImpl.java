package com.yangrr.rrmianshi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.constant.CommonConstant;
import com.yangrr.rrmianshi.domain.OjQuestion;
import com.yangrr.rrmianshi.domain.OjQuestionSubmit;
import com.yangrr.rrmianshi.domain.Question;
import com.yangrr.rrmianshi.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yangrr.rrmianshi.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yangrr.rrmianshi.enums.QuestionSubmitLanguageEnum;
import com.yangrr.rrmianshi.enums.QuestionSubmitStatusEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.judge.JudgeService;
import com.yangrr.rrmianshi.mapper.OjQuestionSubmitMapper;
import com.yangrr.rrmianshi.service.OjQuestionService;
import com.yangrr.rrmianshi.service.OjQuestionSubmitService;
import com.yangrr.rrmianshi.service.QuestionService;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.utils.SqlUtils;
import com.yangrr.rrmianshi.vo.OjQuestionSubmitVO;
import com.yangrr.rrmianshi.vo.SafetyUser;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author 31841
* @description 针对表【oj_question_submit(题目提交)】的数据库操作Service实现
* @createDate 2025-02-12 09:58:45
*/
@Service
public class OjQuestionSubmitServiceImpl extends ServiceImpl<OjQuestionSubmitMapper, OjQuestionSubmit>
    implements OjQuestionSubmitService {
    @Resource
    private OjQuestionService questionService;

    @Resource
    private UsersService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, SafetyUser loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        OjQuestion question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        OjQuestionSubmit questionSubmit = new OjQuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        // 执行判题服务
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmitId);
        });

        //统计共有多少人提交（并发问题）
        questionService.update().setSql("submit_num = submit_num + 1").eq("id", questionId).update();


        return questionSubmitId;
    }


    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<OjQuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<OjQuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public OjQuestionSubmitVO getQuestionSubmitVO(OjQuestionSubmit questionSubmit, SafetyUser loginUser) {
        OjQuestionSubmitVO questionSubmitVO = OjQuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<OjQuestionSubmitVO> getQuestionSubmitVOPage(Page<OjQuestionSubmit> questionSubmitPage, SafetyUser loginUser) {
        List<OjQuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<OjQuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<OjQuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}




