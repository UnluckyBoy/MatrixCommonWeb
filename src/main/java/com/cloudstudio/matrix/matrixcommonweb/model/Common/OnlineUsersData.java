package com.cloudstudio.matrix.matrixcommonweb.model.Common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName：OnlineUsersData
 * @Author: matrix
 * @Date: 2026/1/31 20:32
 * @Description:在线用户数
 */
@Data
public class OnlineUsersData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int total;
    private java.util.List<WebSocketSimpleInfo> users;
}
