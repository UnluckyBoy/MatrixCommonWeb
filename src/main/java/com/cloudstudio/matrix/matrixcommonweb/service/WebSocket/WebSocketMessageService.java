package com.cloudstudio.matrix.matrixcommonweb.service.WebSocket;

import com.cloudstudio.matrix.matrixcommonweb.model.WorkBean.WorkInfoBean;

/**
 * @ClassName：WebSocketMessageService
 * @Author: matrix
 * @Date: 2026/2/1 01:25
 * @Description:WebSocket消息服务接口
 */
public interface WebSocketMessageService {
    void broadcastWorkOrderCreated(WorkInfoBean workInfo);
}
