package com.cloudstudio.matrix.matrixcommonweb.configs;

import com.cloudstudio.matrix.matrixcommonweb.controller.component.MatrixWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @ClassName：WebSocketConfig
 * @Author: matrix
 * @Date: 2025/2/28 22:53
 * @Description:WebSocket配置类
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MatrixWebSocketHandler webSocketHandler;

    public WebSocketConfig(MatrixWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws").setAllowedOrigins("*");
    }
}
