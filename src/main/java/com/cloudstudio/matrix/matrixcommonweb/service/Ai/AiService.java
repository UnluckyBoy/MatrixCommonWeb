package com.cloudstudio.matrix.matrixcommonweb.service.Ai;

import com.cloudstudio.matrix.matrixcommonweb.model.AiBean.ChatRequest;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;

import java.io.IOException;

/**
 * @ClassName：AiService
 * @Author: matrix
 * @Date: 2026/1/17 11:56
 * @Description:Ai服务接口
 */
public interface AiService {
    WebServerResponse chat(ChatRequest request) throws IOException;
}
