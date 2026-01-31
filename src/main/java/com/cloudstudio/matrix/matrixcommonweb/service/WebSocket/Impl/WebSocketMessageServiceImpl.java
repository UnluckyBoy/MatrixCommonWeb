package com.cloudstudio.matrix.matrixcommonweb.service.WebSocket.Impl;

import com.cloudstudio.matrix.matrixcommonweb.configs.Handler.MatrixWebSocketHandler;
import com.cloudstudio.matrix.matrixcommonweb.configs.Handler.SessionManager;
import com.cloudstudio.matrix.matrixcommonweb.model.Common.WorkOrderNotification;
import com.cloudstudio.matrix.matrixcommonweb.model.WorkBean.WorkInfoBean;
import com.cloudstudio.matrix.matrixcommonweb.service.WebSocket.WebSocketMessageService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebSocketResponse;
import org.springframework.stereotype.Service;

/**
 * @ClassName：WebSocketMessageServiceImpl
 * @Author: matrix
 * @Date: 2026/2/1 01:25
 * @Description:WebSocket消息实现
 */
@Service("WebSocketMessageService")
public class WebSocketMessageServiceImpl implements WebSocketMessageService {
    private final SessionManager sessionManager;
    private final MatrixWebSocketHandler webSocketHandler;

    public WebSocketMessageServiceImpl(SessionManager sessionManager,
                                       MatrixWebSocketHandler webSocketHandler) {
        this.sessionManager = sessionManager;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void broadcastWorkOrderCreated(WorkInfoBean workInfo) {
        try {
            // 构建通知消息
            WorkOrderNotification notification = new WorkOrderNotification();
            notification.setType("notification");
            notification.setWorkOrderId(workInfo.getWorkID());
            notification.setWorkOrderTitle(workInfo.getWorkTitle());
            notification.setWorkOrderContent(workInfo.getWorkContent());
            notification.setCreator(workInfo.getCreator());
            notification.setCreateTime(workInfo.getCreatDate());
            notification.setPriority(workInfo.getPriority());
            notification.setStatus(workInfo.getWorkStatus());
            notification.setTimestamp(System.currentTimeMillis());

            // 创建 WebSocket 响应
            WebSocketResponse response = WebSocketResponse.success(
                    "notification",
                    "新工单创建",
                    notification
            );

            // 广播消息
            webSocketHandler.broadcastMessage(response);

        } catch (Exception e) {
            System.out.println(TimeUtil.GetTime(true)+" ---发送工单创建通知失败:"+e);
        }
    }
}
