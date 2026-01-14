package com.cloudstudio.matrix.matrixcommonweb.model.Applet;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName：OrganizationBean
 * @Author: matrix
 * @Date: 2026/1/11 15:57
 * @Description:机构信息实体
 */
@Data
public class OrganizationBean implements Serializable {
    private String organization_Id;
    private String organization_Name;
    private String organization_Desc;
    private String organization_Owner;
    private String organization_Addr;
    private String organization_Type;
}
