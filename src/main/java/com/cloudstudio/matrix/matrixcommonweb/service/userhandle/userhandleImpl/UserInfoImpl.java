package com.cloudstudio.matrix.matrixcommonweb.service.userhandle.userhandleImpl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cloudstudio.matrix.matrixcommonweb.model.UserInfoBean;
import com.cloudstudio.matrix.matrixcommonweb.model.mapper.UserMapper;
import com.cloudstudio.matrix.matrixcommonweb.service.userhandle.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName：UserInfoImpl
 * @Author: matrix
 * @Date: 2024/12/17 22:17
 * @Description:用户信息查询服务实现类
 */
@Service("UserInfoService")
public class UserInfoImpl implements UserInfoService {
    @Autowired
    UserMapper userMapper;

    @DS("mysql")
    @Override
    public UserInfoBean loginQuery(Map<String, Object> map) {
        return userMapper.loginQuery(map);
    }

    @DS("mysql")
    @Override
    public UserInfoBean emailLogin(Map<String, Object> map) {
        return userMapper.emailLogin(map);
    }

    @Override
    public boolean regisLoginInfo(String email) {
        return userMapper.regisLoginInfo(email);
    }
}
