package com.cloudstudio.matrix.matrixcommonweb.model.requestBody.userRequest;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName：LoginRequestBody
 * @Author: matrix
 * @Date: 2025/2/28 21:41
 * @Description:登录请求体
 */
@Data
public class LoginRequestBody implements Serializable {
    private String account;
    private String pass;
}
