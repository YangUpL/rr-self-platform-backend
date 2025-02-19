package com.yangrr.rrmianshi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QuestionInfo {
    private Long id;
    // Getters 和 Setters
    @ExcelProperty("Title")
    private String title;

    @ExcelProperty("Difficulty")
    private int difficulty;

    @ExcelProperty("Need Vip")
    private int needVip;

    @ExcelProperty("Tag List")
    private String tagList;


    public QuestionInfo() {
    }

    // 构造函数
    public QuestionInfo(String title, int difficulty, int needVip, String tagList) {
        this.title = title;
        this.difficulty = difficulty;
        this.needVip = needVip;
        this.tagList = tagList;
    }

}