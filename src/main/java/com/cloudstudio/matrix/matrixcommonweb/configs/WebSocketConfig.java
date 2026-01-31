package com.cloudstudio.matrix.matrixcommonweb.configs;

import com.cloudstudio.matrix.matrixcommonweb.configs.Handler.MatrixWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @ClassName：WebSocketConfig
 * @Author: matrix
 * @Date: 2025/2/28 22:53
 * @Description:WebSocket配置类
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final MatrixWebSocketHandler matrixWebSocketHandler;
    // 构造函数注入
    public WebSocketConfig(MatrixWebSocketHandler matrixWebSocketHandler) {
        this.matrixWebSocketHandler = matrixWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(matrixWebSocketHandler, "/ws")
                .setAllowedOrigins("*"); // 生产环境应该配置具体的域名
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        container.setMaxSessionIdleTimeout(600000L); // 10分钟
        return container;
    }
}
