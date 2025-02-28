package com.cloudstudio.matrix.matrixcommonweb.controller.component;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName：MatrixWebSocketHandler
 * @Author: matrix
 * @Date: 2025/2/28 22:54
 * @Description:WebSocket处理器
 */
@Component
public class MatrixWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessionsByIp = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        InetAddress inetAddress = InetAddress.getByName(Objects.requireNonNull(session.getRemoteAddress()).getAddress().getHostAddress());
        String ip = inetAddress.getHostAddress();
        sessionsByIp.put(ip, session);
        System.out.println("连接成功: " + ip);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 这里你可以解析消息并处理
        String payload = message.getPayload();
        System.out.println("接收消息: " + payload + " from " + session.getRemoteAddress());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        InetAddress inetAddress = InetAddress.getByName(Objects.requireNonNull(session.getRemoteAddress()).getAddress().getHostAddress());
        String ip = inetAddress.getHostAddress();
        sessionsByIp.remove(ip);
        System.out.println("已连接: " + ip);
    }

    public boolean sendMessageToIp(String ip, String message) throws IOException {
        WebSocketSession session = sessionsByIp.get(InetAddress.getByName(ip).getHostAddress());
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
            return true;
        } else {
            System.out.println("No active session for IP: " + ip);
            return false;
        }
    }
}
