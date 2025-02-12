package com.cloudstudio.matrix.matrixcommonweb.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName：UserInfoBean
 * @Author: matrix
 * @Date: 2024/12/17 22:11
 * @Description:用户信息Bean
 */
@Data
public class UserInfoBean implements Serializable {
    private String uAccount;
    private String uName;
    private String organization_name;
    private String headerImageUrl;
    private String authority_key;
}
