package com.yangrr.rrmianshi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.constant.CommonConstant;
import com.yangrr.rrmianshi.domain.OjQuestion;
import com.yangrr.rrmianshi.domain.Users;
import com.yangrr.rrmianshi.dto.question.QuestionQueryRequest;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.exception.ThrowUtils;
import com.yangrr.rrmianshi.mapper.OjQuestionMapper;
import com.yangrr.rrmianshi.service.OjQuestionService;
import com.yangrr.rrmianshi.service.UsersService;
import com.yangrr.rrmianshi.utils.SqlUtils;
import com.yangrr.rrmianshi.vo.OjQuestionVO;
import com.yangrr.rrmianshi.vo.SafetyUser;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 31841
* @description 针对表【oj_question(题目)】的数据库操作Service实现
* @createDate 2025-02-12 09:58:41
*/
@Service
public class OjQuestionServiceImpl extends ServiceImpl<OjQuestionMapper, OjQuestion>
    implements OjQuestionService {
    @Resource
    private UsersService userService;

    /**
     * 校验题目是否合法
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(OjQuestion question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        String template = question.getTemplate();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags,template), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<OjQuestion> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<OjQuestion> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public OjQuestionVO getQuestionVO(OjQuestion question, HttpServletRequest request) {
        OjQuestionVO questionVO = OjQuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        Users user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        SafetyUser userVO = userService.getUserById(user.getId());
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<OjQuestionVO> getQuestionVOPage(Page<OjQuestion> questionPage, HttpServletRequest request) {
        List<OjQuestion> questionList = questionPage.getRecords();
        Page<OjQuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(OjQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<Users>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(Users::getId));
        // 填充信息
        List<OjQuestionVO> questionVOList = questionList.stream().map(question -> {
            OjQuestionVO questionVO = OjQuestionVO.objToVo(question);
            Long userId = question.getUserId();
            Users user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserById(user.getId()));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

}




