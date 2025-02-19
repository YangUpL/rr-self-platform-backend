package com.yangrr.rrmianshi.controller;

import com.yangrr.rrmianshi.annotation.AuthCheck;
import com.yangrr.rrmianshi.common.BaseResponse;
import com.yangrr.rrmianshi.common.ResultUtils;
import com.yangrr.rrmianshi.dto.AddQuestionCategoryDto;
import com.yangrr.rrmianshi.dto.UpdateQuestionCategoryDto;
import com.yangrr.rrmianshi.service.QuestionCategoryService;
import com.yangrr.rrmianshi.vo.QuestionCategoryVo;
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
@RequestMapping("/category/que")
@CrossOrigin(value = "http://localhost:8000", allowCredentials = "true",allowedHeaders = "*")
public class QuestionCategoryController {

    @Autowired
    private QuestionCategoryService questionCategoryService;

    @PostMapping("list")
    public BaseResponse<List<QuestionCategoryVo>> listQuestionCategory(){
        return ResultUtils.success(questionCategoryService.listQuestionCategory());
    }

    @GetMapping("get/{id}")
    public BaseResponse<QuestionCategoryVo> getQuestionCategoryById(@PathVariable("id") Long id){
        return ResultUtils.success(questionCategoryService.getQuestionCategoryById(id));
    }

    @PostMapping("add")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> addQuestionCategory(@RequestBody AddQuestionCategoryDto addQuestionCategoryDto){
        return ResultUtils.success(questionCategoryService.addQuestionCategory(addQuestionCategoryDto));
    }

    @PostMapping("update")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> updateQuestionCategory(@RequestBody UpdateQuestionCategoryDto updateQuestionCategoryDto){
        return ResultUtils.success(questionCategoryService.updateQuestionCategory(updateQuestionCategoryDto));
    }
    @GetMapping("delete/{id}")
    @AuthCheck(mustRole = ADMIN)
    public BaseResponse<Boolean> deleteQuestionCategory(@PathVariable("id") Long id){
        return ResultUtils.success(questionCategoryService.deleteQuestionCategory(id));
    }
}
