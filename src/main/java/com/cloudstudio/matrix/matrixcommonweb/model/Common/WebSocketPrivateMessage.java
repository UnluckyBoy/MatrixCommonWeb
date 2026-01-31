package com.cloudstudio.matrix.matrixcommonweb.model.Common;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName：WebSocketPrivateMessage
 * @Author: matrix
 * @Date: 2026/1/31 20:32
 * @Description:私发实体类
 */
@Data
public class WebSocketPrivateMessage implements Serializable {
    private String from;
    private String content;
    private long timestamp;

    public WebSocketPrivateMessage(String from, String content, long timestamp) {
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
    }
}
