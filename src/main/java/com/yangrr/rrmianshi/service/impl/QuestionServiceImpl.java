package com.yangrr.rrmianshi.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangrr.rrmianshi.domain.QuestionCategory;
import com.yangrr.rrmianshi.service.QuestionCategoryService;
import com.yangrr.rrmianshi.utils.UserThreadLocalUtil;
import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.constant.CommonConstant;
import com.yangrr.rrmianshi.domain.Question;
import com.yangrr.rrmianshi.dto.AddQuestionDto;
import com.yangrr.rrmianshi.dto.ListQuestionDto;
import com.yangrr.rrmianshi.dto.UpdateQuestionDto;
import com.yangrr.rrmianshi.enums.QuestionNeedVipEnum;
import com.yangrr.rrmianshi.enums.UserRoleEnum;
import com.yangrr.rrmianshi.exception.BusinessException;
import com.yangrr.rrmianshi.facade.CheckFacade;
import com.yangrr.rrmianshi.mapper.QuestionMapper;
import com.yangrr.rrmianshi.service.QuestionService;
import com.yangrr.rrmianshi.vo.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.List;
import java.util.stream.Collectors;

import static com.yangrr.rrmianshi.utils.PrivatePublicUtils.decodePrivateKey;
import static com.yangrr.rrmianshi.utils.PrivatePublicUtils.decrypt;

/**
 * @author 31841
 * @description 针对表【question(题目表，用于存储具体的题目信息)】的数据库操作Service实现
 * @createDate 2025-02-03 10:07:56
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Resource
    private QuestionCategoryService questionCategoryService;

    @Override
    public Boolean addQuestion(AddQuestionDto addQuestionDto) {
        String question = addQuestionDto.getQuestion();
        String answer = addQuestionDto.getAnswer();
        Integer tid = addQuestionDto.getTid();
        Integer difficulty = addQuestionDto.getDifficulty();
        Integer needVip = addQuestionDto.getNeedVip();
        List<String> taglist = addQuestionDto.getTaglist();

        //校验
        CheckFacade.check(question, answer, difficulty, needVip, taglist);

        //校验tid是否存在
        if (tid == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目分类不能为空");
        }

        QuestionCategory byId = questionCategoryService.getById(tid);
        if (byId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类不存在");
        }


        Question newQuestion = new Question();
        BeanUtils.copyProperties(addQuestionDto, newQuestion);

        JSONUtil.toJsonStr(taglist);
        newQuestion.setTaglist(JSONUtil.toJsonStr(taglist));

        return this.save(newQuestion);
    }

    @Override
    public Boolean updateQuestion(UpdateQuestionDto updateQuestionDto) {
        Long id = updateQuestionDto.getId();
        String question = updateQuestionDto.getQuestion();
        String answer = updateQuestionDto.getAnswer();
        Integer tid = updateQuestionDto.getTid();
        Integer difficulty = updateQuestionDto.getDifficulty();
        Integer needVip = updateQuestionDto.getNeedVip();
        List<String> taglist = updateQuestionDto.getTaglist();

        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }
        //校验
        CheckFacade.check(question, answer, difficulty, needVip, taglist);

        //校验tid是否存在
        if (tid == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目分类不能为空");
        }

        QuestionCategory byId = questionCategoryService.getById(tid);
        if (byId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类不存在");
        }

        Question newQuestion = new Question();
        BeanUtils.copyProperties(updateQuestionDto, newQuestion);
        JSONUtil.toJsonStr(taglist);
        newQuestion.setTaglist(JSONUtil.toJsonStr(taglist));
        return this.updateById(newQuestion);
    }

    @Override
    public QuestionVo getQuestionById(String id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }

        //私钥解码id
        String realId = null;
        try {
            String privateKeyBase64 = new String(Files.readAllBytes(Paths.get("privateKey.txt")));
            PrivateKey privateKey = decodePrivateKey(privateKeyBase64);
            realId = decrypt(id, privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Question question = this.getById(realId);

        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        //根据ThreadLocal获取用户信息
        SafetyUser user = UserThreadLocalUtil.get();
        String answer = question.getAnswer();
        //未登录  或者  题目需要vip但用户不是vip用户  返回答案的128个字符
        if (user == null ||
                (QuestionNeedVipEnum.getEnum(question.getNeedVip()) == QuestionNeedVipEnum.NEED_VIP
                        && UserRoleEnum.getRoleEnum(user.getRole()) == UserRoleEnum.USER)) {
            if(answer != null) {
                question.setAnswer(question.getAnswer().substring(0,(int) (answer.length() * 0.3)));
            }
        }

        //将taglist字符串转换为List
        List<String> tagList = JSONUtil.toList(question.getTaglist(), String.class);
        QuestionVo vo = new QuestionVo();
        //将taglist赋值给vo
        vo.setTaglist(tagList);
        //将question赋值给vo
        BeanUtils.copyProperties(question, vo);

        return vo;
    }

    @Override
    public QuestionNoAnswerPageVo listQuestion(ListQuestionDto listQuestionDto) {
        Integer tid = listQuestionDto.getTid();
        Integer[] difficulties = listQuestionDto.getDifficulties();
        Integer pageNum = listQuestionDto.getPageNum();
        Integer pageSize = listQuestionDto.getPageSize();
        String sorterName = listQuestionDto.getSorterName();
        String sorterOrder = listQuestionDto.getSorterOrder();

        if (tid == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "tid不能为空");
        }

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid);
        queryWrapper.orderByDesc(sorterOrder.equals(CommonConstant.SORT_ORDER_DESC), sorterName);
        queryWrapper.orderByAsc(sorterOrder.equals(CommonConstant.SORT_ORDER_ASC), sorterName);
        if (difficulties != null && difficulties.length > 0) {
            queryWrapper.in("difficulty", List.of(difficulties));
        }
        Page<Question> questionPage = new Page<>(pageNum, pageSize);
        Page<Question> page = this.page(questionPage, queryWrapper);

        List<QuestionNoAnswerVo> voList = page.getRecords().stream()
                .map(question -> {
                    QuestionNoAnswerVo vo = new QuestionNoAnswerVo();
                    // 将 taglist 字符串转换为 List
                    List<String> tagList = JSONUtil.toList(question.getTaglist(), String.class);
                    vo.setTaglist(tagList);
                    // 将 question 赋值给 vo
                    BeanUtils.copyProperties(question, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        QuestionNoAnswerPageVo questionNoAnswerPageVo = new QuestionNoAnswerPageVo();
        questionNoAnswerPageVo.setList(voList);
        questionNoAnswerPageVo.setTotal((int) page.getTotal());
        questionNoAnswerPageVo.setPageSize((int) page.getSize());

        return questionNoAnswerPageVo;
    }

    @Override
    public Boolean deleteQuestionById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }
        return this.removeById(id);
    }

    @Override
    public PublicKeyVo getPublicKey() {
        //读取publicKey.txt文件中的公钥
        PublicKeyVo publicKeyVo = new PublicKeyVo();
        try {
            String publicKey = new String(Files.readAllBytes(Paths.get("publicKey.txt")));
            publicKeyVo.setPublicKey(publicKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return publicKeyVo;
    }
}




