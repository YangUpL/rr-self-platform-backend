package com.yangrr.rrmianshi.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @PATH com.yangrr.rrmianshi.vo.QuestionVo
 * @Author YangRR
 * @CreateData 2025-02-05 10:38
 * @Description:
 */
@Data
public class QuestionNoAnswerPageVo {
    private Integer total;
    private Integer pageSize;
    private List<QuestionNoAnswerVo> list;
}
