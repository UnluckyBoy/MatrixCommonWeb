package com.cloudstudio.matrix.matrixcommonweb.service.Applet.Impl;

import com.cloudstudio.matrix.matrixcommonweb.model.Applet.FeedbackBean;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.NewsDetail;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.OrganizationBean;
import com.cloudstudio.matrix.matrixcommonweb.model.mapper.AppletMapper;
import com.cloudstudio.matrix.matrixcommonweb.service.Applet.AppletDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName：AppletDataServiceImpl
 * @Author: matrix
 * @Date: 2026/1/10 19:28
 * @Description:小程序数据服务接口实现
 */
@Service("AppletDataService")
public class AppletDataServiceImpl implements AppletDataService {
    @Autowired
    private AppletMapper appletMapper;

    //  @DS("oracle") // 多数据切换
    @Override
    public boolean cFeedBack(FeedbackBean feedbackBean) {
        return appletMapper.cFeedBack(feedbackBean);
    }

    @Override
    public OrganizationBean queryOrganization(String organization_Id) {
        return appletMapper.queryOrganization(organization_Id);
    }

    @Override
    public NewsDetail queryNewDetail(int newId) {
        return appletMapper.queryNewDetail(newId);
    }

    @Override
    public List<NewsDetail> queryNewsList() {
        return appletMapper.queryNewsList();
    }
}
