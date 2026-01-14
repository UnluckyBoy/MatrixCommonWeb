package com.cloudstudio.matrix.matrixcommonweb.service.Applet;

import com.cloudstudio.matrix.matrixcommonweb.model.Applet.FeedbackBean;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.NewsDetail;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;

/**
 * @ClassName：AppletService
 * @Author: matrix
 * @Date: 2026/1/10 18:10
 * @Description:小程序接口服务
 */
public interface AppletService {
    WebServerResponse submitFeedBack(FeedbackBean feedbackBean);
    WebServerResponse queryOrganization(String organization_Id);
    WebServerResponse queryNewDetail(int newId);
    WebServerResponse queryNewsList();
}
