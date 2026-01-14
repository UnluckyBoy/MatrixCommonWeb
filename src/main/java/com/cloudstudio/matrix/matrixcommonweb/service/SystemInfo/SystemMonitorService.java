package com.cloudstudio.matrix.matrixcommonweb.service.SystemInfo;

import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;

/**
 * @ClassName：SystemMonitorService
 * @Author: matrix
 * @Date: 2025/8/4 12:30
 * @Description:系统信息服务接口
 */
public interface SystemMonitorService {
    WebServerResponse getSystemInfo();
}
