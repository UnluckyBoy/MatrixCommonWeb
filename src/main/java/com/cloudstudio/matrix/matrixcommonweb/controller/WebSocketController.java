package com.cloudstudio.matrix.matrixcommonweb.controller;

import com.cloudstudio.matrix.matrixcommonweb.model.chatBean.ChatMessage;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName：WebSocketController
 * @Author: matrix
 * @Date: 2025/2/28 22:56
 * @Description:WebSocket控制类
 */
@Controller
@RequestMapping("/WebApi")
public class WebSocketController {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/chat")
    @SendToUser("/queue/reply")
    public ChatMessage handleChatMessage(ChatMessage message) {
        // 处理消息并返回给特定用户
        return message;
    }
}
