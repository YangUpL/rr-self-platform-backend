package com.yangrr.rrmianshi.controller;

import com.yangrr.rrmianshi.annotation.AuthCheck;
import com.yangrr.rrmianshi.common.BaseResponse;
import com.yangrr.rrmianshi.common.ResultUtils;
import com.yangrr.rrmianshi.dto.AddQuestionDto;
import com.yangrr.rrmianshi.dto.ListQuestionDto;
import com.yangrr.rrmianshi.dto.UpdateQuestionDto;
import com.yangrr.rrmianshi.service.QuestionService;
import com.yangrr.rrmianshi.vo.PublicKeyVo;
import com.yangrr.rrmianshi.vo.QuestionNoAnswerPageVo;
import com.yangrr.rrmianshi.vo.QuestionNoAnswerVo;
import com.yangrr.rrmianshi.vo.QuestionVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yangrr.rrmianshi.constant.UserConstant.ADMIN;

/**
 * @PATH com.yangrr.rrmianshi.controller.chatController
 * @Author YangRR
 * @CreateData 2025-02-04 11:45
 * @Description: 题目接口
 */
@RestController
@RequestMapping("/question")
@CrossOrigin(value = "http://localhost:8000", allowCredentials = "true",allowedHeaders = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("add")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> addQuestion(@RequestBody AddQuestionDto addQuestionDto){
        return ResultUtils.success(questionService.addQuestion(addQuestionDto));
    }


    @PostMapping("update")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> updateQuestion(@RequestBody UpdateQuestionDto updateQuestionDto){
        return ResultUtils.success(questionService.updateQuestion(updateQuestionDto));
    }

    @GetMapping("get")
    public BaseResponse<QuestionVo> getQuestionById(String id, HttpServletRequest request){
        return ResultUtils.success(questionService.getQuestionById(id,request));
    }

    @GetMapping("delete/{id}")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> deleteQuestionById(@PathVariable("id") Long id){
        return ResultUtils.success(questionService.deleteQuestionById(id));
    }

    @PostMapping("list")
    public BaseResponse<QuestionNoAnswerPageVo> listQuestion(@RequestBody ListQuestionDto listQuestionDto){
        return ResultUtils.success(questionService.listQuestion(listQuestionDto));
    }

    @GetMapping("publicKey")
    public BaseResponse<PublicKeyVo> getPublicKey(){
        return ResultUtils.success(questionService.getPublicKey());
    }

}
