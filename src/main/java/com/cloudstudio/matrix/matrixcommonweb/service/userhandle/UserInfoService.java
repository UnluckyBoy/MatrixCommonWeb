package com.cloudstudio.matrix.matrixcommonweb.service.userhandle;

import com.cloudstudio.matrix.matrixcommonweb.model.UserInfoBean;

import java.util.Map;

/**
 * @ClassName：UserInfoService
 * @Author: matrix
 * @Date: 2024/12/17 22:16
 * @Description:用户信息查询服务接口
 */
public interface UserInfoService {
    UserInfoBean loginQuery(Map<String, Object> map);
    UserInfoBean emailLogin(Map<String, Object> map);
    boolean regisLoginInfo(String email);
}
