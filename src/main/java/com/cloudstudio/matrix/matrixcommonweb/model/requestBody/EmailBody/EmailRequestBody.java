package com.cloudstudio.matrix.matrixcommonweb.model.requestBody.EmailBody;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName：EmailRequestBody
 * @Author: matrix
 * @Date: 2026/2/20 00:07
 * @Description:邮箱验证请求入参类
 */
@Data
public class EmailRequestBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private String inputCode;
}
