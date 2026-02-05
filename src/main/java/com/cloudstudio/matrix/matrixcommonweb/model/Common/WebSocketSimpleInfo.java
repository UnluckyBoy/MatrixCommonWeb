package com.cloudstudio.matrix.matrixcommonweb.model.Common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName：LoginUserInfo
 * @Author: matrix
 * @Date: 2026/1/31 20:36
 * @Description:用户实体类
 */
@Data
public class WebSocketSimpleInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String nickname;
    private String account;
    private String role;
    private String avatar;
    private boolean online;
}
