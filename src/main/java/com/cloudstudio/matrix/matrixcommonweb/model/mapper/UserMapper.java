package com.cloudstudio.matrix.matrixcommonweb.model.mapper;

import com.cloudstudio.matrix.matrixcommonweb.model.UserInfoBean;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName：UserMapper
 * @Author: matrix
 * @Date: 2024/12/17 22:10
 * @Description:用户SQl接口
 */
@Service
@Mapper
@Repository
public interface UserMapper {
    UserInfoBean loginQuery(Map<String, Object> map);
    UserInfoBean emailLogin(Map<String, Object> map);//邮箱登录
    boolean regisLoginInfo(String email);// 插入信息
}
