package com.yangrr.rrmianshi.judge.codesandbox.model;

import com.yangrr.rrmianshi.dto.question.OjTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {

    private String template;

    private List<String> inputList;

    private String code;

    private String language;
}
