package com.cloudstudio.matrix.matrixcommonweb.controller;

import com.cloudstudio.matrix.matrixcommonweb.controller.component.MatrixWebSocketHandler;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * @ClassName：WebSocketController
 * @Author: matrix
 * @Date: 2025/2/28 22:56
 * @Description:WebSocket控制类
 */
@Controller
@RequestMapping("/WebApi")
public class WebSocketController {
    private final MatrixWebSocketHandler webSocketHandler;

    private static final Gson gson=new Gson();

    @Autowired
    public WebSocketController(MatrixWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @PostMapping("/connect")
    public void connectServer(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        boolean result = false;
        if(result){
            response.getWriter().write(gson.toJson(WebServerResponse.success()));
        }else{
            response.getWriter().write(gson.toJson(WebServerResponse.failure()));
        }
    }

    @PostMapping("/send")
    public void sendMessage(HttpServletResponse response, @RequestParam String ip, @RequestParam String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        System.out.println(TimeUtil.GetTime(true)+" ---请求参数:ip="+ip+"  message="+message);
        boolean result=webSocketHandler.sendMessageToIp(ip, message);
        if(result){
            response.getWriter().write(gson.toJson(WebServerResponse.success()));
        }else{
            response.getWriter().write(gson.toJson(WebServerResponse.failure()));
        }
    }
}
