package com.cloudstudio.matrix.matrixcommonweb.model.Applet;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName：NewsDetailViewObject
 * @Author: matrix
 * @Date: 2026/1/14 13:18
 * @Description:返回及接收前端新闻实体类
 */
@Data
public class NewsDetailViewObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String newTitle;
    private String publishTime;
    private String source;
    private String author;
    private Integer readCount;
    private String coverImage;// 封面图片,可为null
    private List<String> contentParagraphs;// 正文段落数组
    private List<String> contentImages;// 内容图片，可为null
}
