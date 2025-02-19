package com.yangrr.rrmianshi.controller;

import com.yangrr.rrmianshi.common.ErrorCode;
import com.yangrr.rrmianshi.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@CrossOrigin(value = {"http://localhost:8000","http://localhost:3000"}, allowCredentials = "true", allowedHeaders = "*")
public class ChatController {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    /**
     * @param response
     * @param message  要发送的消息
     * @param prompt   提示（定制）信息
     * @return
     * @throws InterruptedException
     */
    @GetMapping("basic")
    public Flux<String> basicChat(HttpServletResponse response,
                                  @RequestParam("message") String message,
                                  @RequestParam(value = "prompt",
                                          required = false,
                                          defaultValue = "") String prompt
    ) throws InterruptedException {

        if (message == null || message.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息不能为空");
        }
        response.setCharacterEncoding("UTF-8");

        return ollamaChatModel.stream(prompt + ":" + message);
    }

}