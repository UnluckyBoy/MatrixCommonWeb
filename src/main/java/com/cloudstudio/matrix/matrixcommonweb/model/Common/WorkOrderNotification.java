package com.cloudstudio.matrix.matrixcommonweb.model.Common;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName：WorkOrderNotification
 * @Author: matrix
 * @Date: 2026/2/1 01:24
 * @Description:工单消息通知
 */
@Data
public class WorkOrderNotification implements Serializable {

    private String type;                // 通知类型
    private String workOrderId;         // 工单ID
    private String workOrderTitle;      // 工单标题
    private String workOrderContent;    // 工单内容
    private String creator;             // 创建人
    private String createTime;   // 创建时间
    private String priority;            // 优先级
    private String status;              // 状态
    private long timestamp;             // 时间戳
}