package com.cloudstudio.matrix.matrixcommonweb.model.Common;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName：WebSocketUserInfo
 * @Author: matrix
 * @Date: 2026/1/31 21:12
 * @Description:WebSocket用户实体类
 */
@Data
public class WebSocketUserInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String nickname;
    private String role;
    private String avatar;
    private LocalDateTime connectTime;

    public WebSocketSimpleInfo toWebSocketSimpleInfo(ConcurrentHashMap<String, WebSocketSession> sessions) {
        WebSocketSimpleInfo info = new WebSocketSimpleInfo();
        info.setSessionId(sessionId);
        info.setNickname(nickname != null ? nickname : "用户" +
                (sessionId.length() > 6 ? sessionId.substring(0, 6) : sessionId));
        info.setRole(role);
        info.setAvatar(avatar);
        info.setOnline(sessions.containsKey(sessionId));
        return info;
    }
}
