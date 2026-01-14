package com.cloudstudio.matrix.matrixcommonweb.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName：ImageBean
 * @Author: matrix
 * @Date: 2026/1/10 12:54
 * @Description:图片实体类
 */
@Data
public class ImageBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int imgId;
    private String imgName;
    private String imgUrl;
}
