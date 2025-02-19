package com.yangrr.rrmianshi.dto;

import com.yangrr.rrmianshi.constant.CommonConstant;
import lombok.Data;

/**
 * @PATH com.yangrr.rrmianshi.dto.GlobleDto
 * @Author YangRR
 * @CreateData 2025-02-09 09:48
 * @Description:
 */

@Data
public class GlobalDto {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String sorterName;
    private String sorterOrder = CommonConstant.SORT_ORDER_ASC;
}
