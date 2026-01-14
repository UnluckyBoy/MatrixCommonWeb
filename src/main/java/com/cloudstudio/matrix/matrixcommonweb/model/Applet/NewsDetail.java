package com.cloudstudio.matrix.matrixcommonweb.model.Applet;

import com.cloudstudio.matrix.matrixcommonweb.model.Common.JsonToListTypeHandler;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName：NewsDetail
 * @Author: matrix
 * @Date: 2026/1/13 19:43
 * @Description:新闻详情
 */
@Data
public class NewsDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String newTitle;
    private String publishTime;
    private String source;
    private String author;
    private Integer readCount;
    private String coverImage;// 封面图片,可为null
    private String contentParagraphs;// 正文段落数组
    private String contentImages;// 内容图片，可为null
}
