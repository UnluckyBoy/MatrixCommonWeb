package com.cloudstudio.matrix.matrixcommonweb.service.Common;

import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;

/**
 * @ClassName：CommonService
 * @Author: matrix
 * @Date: 2026/2/19 23:39
 * @Description:公共服务类
 */
public interface CommonService {
    WebServerResponse sendVerificationCode(String to);
}
