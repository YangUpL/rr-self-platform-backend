package com.yangrr.rrmianshi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;

import com.yangrr.rrmianshi.annotation.AuthCheck;
import com.yangrr.rrmianshi.common.BaseResponse;
import com.yangrr.rrmianshi.common.DeleteRequest;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.common.ResultUtils;
import com.yangrr.rrmianshi.domain.OjQuestion;
import com.yangrr.rrmianshi.domain.OjQuestionSubmit;
import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.dto.question.*;
import com.yangrr.rrmianshi.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yangrr.rrmianshi.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.exception.ThrowUtils;
import com.yangrr.rrmianshi.service.OjQuestionService;
import com.yangrr.rrmianshi.service.OjQuestionSubmitService;
import com.yangrr.rrmianshi.service.QuestionService;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.vo.OjQuestionSubmitVO;
import com.yangrr.rrmianshi.vo.OjQuestionVO;
import com.yangrr.rrmianshi.vo.SafetyUser;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.yangrr.rrmianshi.constant.UserConstant.ADMIN;

/**
 * 题目接口
 *
 */
@RestController
@RequestMapping("/ojquestion")
@Slf4j
@CrossOrigin(value = "http://localhost:8000", allowCredentials = "true", allowedHeaders = "*")
public class OJQuestionController {

    @Resource
    private OjQuestionService ojQuestionService;

    @Resource
    private UsersService usersService;

    @Resource
    private OjQuestionSubmitService questionSubmitService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addOjQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        OjQuestion oJQuestion = new OjQuestion();
        BeanUtils.copyProperties(questionAddRequest, oJQuestion);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            oJQuestion.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            oJQuestion.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            oJQuestion.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        OjTemplate template = questionAddRequest.getTemplate();
        if (template != null) {
            oJQuestion.setTemplate(GSON.toJson(template));
        }
        ojQuestionService.validQuestion(oJQuestion, true);
        SafetyUser loginUser = usersService.getCurrentUser(request);
        oJQuestion.setUserId(loginUser.getId());
        boolean result = ojQuestionService.save(oJQuestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = oJQuestion.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteOjQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SafetyUser user = usersService.getCurrentUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        OjQuestion oldQuestion = ojQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !usersService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        boolean b = ojQuestionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> updateOjQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        OjQuestion oJQuestion = new OjQuestion();
        BeanUtils.copyProperties(questionUpdateRequest, oJQuestion);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            oJQuestion.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            oJQuestion.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            oJQuestion.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        OjTemplate template = questionUpdateRequest.getTemplate();
        if (template != null) {
            oJQuestion.setTemplate(GSON.toJson(template));
        }
        // 参数校验
        ojQuestionService.validQuestion(oJQuestion, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        OjQuestion oldQuestion = ojQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = ojQuestionService.updateById(oJQuestion);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<OjQuestion> getOjQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        OjQuestion oJQuestion = ojQuestionService.getById(id);
        if (oJQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        SafetyUser loginUser = usersService.getCurrentUser(request);
        // 不是本人或管理员，不能直接获取所有信息
        if (!oJQuestion.getUserId().equals(loginUser.getId()) && !usersService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return ResultUtils.success(oJQuestion);
    }

    /**
     * 根据 id 获取（脱敏）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<OjQuestionVO> getOjQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        OjQuestion oJQuestion = ojQuestionService.getById(id);
        if (oJQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(ojQuestionService.getQuestionVO(oJQuestion, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<OjQuestionVO>> listOjQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<OjQuestion> questionPage = ojQuestionService.page(new Page<>(current, size),
                ojQuestionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(ojQuestionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<OjQuestionVO>> listMyOjQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SafetyUser loginUser = usersService.getCurrentUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<OjQuestion> questionPage = ojQuestionService.page(new Page<>(current, size),
                ojQuestionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(ojQuestionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Page<OjQuestion>> listOjQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                   HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<OjQuestion> questionPage = ojQuestionService.page(new Page<>(current, size),
                ojQuestionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editOjQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        OjQuestion oJQuestion = new OjQuestion();
        BeanUtils.copyProperties(questionEditRequest, oJQuestion);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            oJQuestion.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            oJQuestion.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            oJQuestion.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        ojQuestionService.validQuestion(oJQuestion, false);
        SafetyUser loginUser = usersService.getCurrentUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        OjQuestion oldQuestion = ojQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !usersService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        boolean result = ojQuestionService.updateById(oJQuestion);
        return ResultUtils.success(result);
    }

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录的 id
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doOjQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final SafetyUser loginUser = usersService.getCurrentUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<OjQuestionSubmitVO>> listOjQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                           HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<OjQuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final SafetyUser loginUser = usersService.getCurrentUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }
}
