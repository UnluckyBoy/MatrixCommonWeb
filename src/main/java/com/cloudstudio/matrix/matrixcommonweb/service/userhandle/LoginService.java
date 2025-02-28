package com.cloudstudio.matrix.matrixcommonweb.service.userhandle;

import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;

/**
 * @ClassName：LoginService
 * @Author: matrix
 * @Date: 2025/2/6 22:38
 * @Description:登录接口服务
 */
public interface LoginService {
    WebServerResponse login(String account, String password);//登录

    WebServerResponse getLoginInfo(String token);//Token获取登录信息
}
