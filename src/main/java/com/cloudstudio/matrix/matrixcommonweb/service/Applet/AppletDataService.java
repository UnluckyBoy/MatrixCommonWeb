package com.cloudstudio.matrix.matrixcommonweb.service.Applet;

import com.cloudstudio.matrix.matrixcommonweb.model.Applet.FeedbackBean;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.NewsDetail;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.OrganizationBean;

import java.util.List;

/**
 * @ClassName：AppletDataService
 * @Author: matrix
 * @Date: 2026/1/10 19:27
 * @Description:小程序数据服务接口
 */
public interface AppletDataService {
    boolean cFeedBack(FeedbackBean feedbackBean);
    OrganizationBean queryOrganization(String organization_Id);
    NewsDetail queryNewDetail(int newId);

    List<NewsDetail> queryNewsList();
}
