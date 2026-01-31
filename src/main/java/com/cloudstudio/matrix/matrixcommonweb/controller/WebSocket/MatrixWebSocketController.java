package com.cloudstudio.matrix.matrixcommonweb.controller.WebSocket;

import com.cloudstudio.matrix.matrixcommonweb.configs.Handler.MatrixWebSocketHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName：WebSocketController
 * @Author: matrix
 * @Date: 2026/1/31 16:26
 * @Description:WebSocket控制类
 */
@Controller
@RequestMapping("/websocketApi")
public class MatrixWebSocketController {


    @RequestMapping("/status")
    public String getStatus() {
        return "当前连接数: " +0;
    }

    @RequestMapping("/broadcast")
    public String broadcast(@RequestParam String message) {
        // 这里可以添加权限验证逻辑
        String formattedMessage = "{\"type\":\"broadcast\",\"message\":\"" + message + "\"}";
        // 广播消息
        // 注意：实际广播应该在WebSocketHandler中调用
        return "消息已发送";
    }
}

