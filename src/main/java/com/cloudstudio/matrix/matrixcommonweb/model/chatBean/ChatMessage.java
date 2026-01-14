package com.cloudstudio.matrix.matrixcommonweb.model.chatBean;

import lombok.Data;

/**
 * @ClassName：ChatMessage
 * @Author: matrix
 * @Date: 2025/3/3 21:21
 * @Description:消息实体类
 */
@Data
public class ChatMessage {
    private String from;
    private String to;
    private String content;
}
