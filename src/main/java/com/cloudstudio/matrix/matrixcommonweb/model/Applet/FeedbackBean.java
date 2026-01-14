package com.cloudstudio.matrix.matrixcommonweb.model.Applet;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName：FeedbackBean
 * @Author: matrix
 * @Date: 2026/1/9 23:02
 * @Description:投诉实体类
 */
@Data
public class FeedbackBean implements Serializable {
    private String complaintTarget;//投诉对象
    private String contactName;//反馈人
    private String contactPhone;//联系电话
    private String contactWechat;//联系微信
    private String description;//描述
    private int feedbackType;//反馈类型
    private String feedbackTypeText;
    private int imagesCount;//反馈图片数
    private String imagesUrlList;//反馈图片链接
    private int isAnonymous;//是否匿名
    private String selectedCategory;//问题分类
    private String selectedCategoryText;//问题分类
    private String submitTime;//提交时间
    private long submitTimestamp;//提交时间
    private String title;//反馈主题
    private String targetDetail;//投诉对象
}
