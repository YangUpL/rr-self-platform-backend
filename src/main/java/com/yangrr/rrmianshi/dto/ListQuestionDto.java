package com.yangrr.rrmianshi.dto;

import lombok.Data;

/**
 * @PATH com.yangrr.rrmianshi.dto.ListQuestionDto
 * @Author YangRR
 * @CreateData 2025-02-09 09:50
 * @Description:
 */
@Data
public class ListQuestionDto extends GlobalDto {
    private String searchQuestion;
    private Integer tid;
    private Integer[] difficulties;
}
