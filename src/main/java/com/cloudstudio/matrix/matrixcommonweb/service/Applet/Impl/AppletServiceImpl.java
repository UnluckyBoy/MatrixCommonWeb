package com.cloudstudio.matrix.matrixcommonweb.service.Applet.Impl;

import com.cloudstudio.matrix.matrixcommonweb.model.Applet.FeedbackBean;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.NewsDetail;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.NewsDetailViewObject;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.OrganizationBean;
import com.cloudstudio.matrix.matrixcommonweb.model.Common.JsonToListHandler;
import com.cloudstudio.matrix.matrixcommonweb.service.Applet.AppletDataService;
import com.cloudstudio.matrix.matrixcommonweb.service.Applet.AppletService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.StringUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName：AppletServiceImpl
 * @Author: matrix
 * @Date: 2026/1/10 18:31
 * @Description:小程序服务实现类
 */
@Service("AppletService")
public class AppletServiceImpl implements AppletService {
    @Autowired
    private AppletDataService appletDataService;

    private final JsonToListHandler jsonToListHandler = new JsonToListHandler();

    private static final String serverImgBaseUrl="https://matrix.cpolar.cn/image";

    /**
     * 反馈信息逻辑
     * @param feedbackBean
     * @return
     */
    @Override
    public WebServerResponse submitFeedBack(FeedbackBean feedbackBean) {
        if (feedbackBean == null) {
            System.out.println(TimeUtil.GetTime(true)+" -->入参为空");
            return WebServerResponse.paramError();
        }

        boolean result=appletDataService.cFeedBack(feedbackBean);
        if (result) {
            System.out.println(TimeUtil.GetTime(true)+" 写入成功-->入参:"+feedbackBean);
            return WebServerResponse.success();
        }
        System.out.println(TimeUtil.GetTime(true)+" 写入失败-->入参:"+feedbackBean);
        return WebServerResponse.failure();
    }

    /**
     * 查询机构信息逻辑
     * @param organization_Id
     * @return
     */
    @Override
    public WebServerResponse queryOrganization(String organization_Id) {
        if (StringUtil.isEmptyOrNull(organization_Id)) {
            System.out.println(TimeUtil.GetTime(true)+" -->入参为空");
            return WebServerResponse.paramError();
        }
        OrganizationBean resultBean=appletDataService.queryOrganization(organization_Id);
        if (resultBean == null) {
            System.out.println(TimeUtil.GetTime(true)+" 查询失败-->入参:"+organization_Id);
            return WebServerResponse.failure();
        }
        System.out.println(TimeUtil.GetTime(true)+" 查询成功-->入参:"+organization_Id+"--->>>查询结果:"+resultBean);
        return WebServerResponse.success(resultBean);
    }

    /**
     * 查询新内容
     * @param newId
     * @return
     */
    @Override
    public WebServerResponse queryNewDetail(int newId) {
        if(newId<0){
            System.out.println(TimeUtil.GetTime(true)+" -->入参异常");
            return WebServerResponse.paramError();
        }
        NewsDetail tempBean=appletDataService.queryNewDetail(newId);
        if (tempBean == null) {
            System.out.println(TimeUtil.GetTime(true)+" 查询失败-->入参:"+newId);
            return WebServerResponse.failure();
        }
        NewsDetailViewObject resultViewObject=new NewsDetailViewObject();
        resultViewObject.setId(tempBean.getId());
        resultViewObject.setNewTitle(tempBean.getNewTitle());
        resultViewObject.setSource(tempBean.getSource());
        resultViewObject.setAuthor(tempBean.getAuthor());
        resultViewObject.setPublishTime(tempBean.getPublishTime());
        resultViewObject.setReadCount(tempBean.getReadCount());
        resultViewObject.setCoverImage(serverImgBaseUrl+tempBean.getCoverImage());
        resultViewObject.setContentParagraphs(jsonToListHandler.parseParagraphs(tempBean.getContentParagraphs()));
        resultViewObject.setContentImages(jsonToListHandler.parseImages(tempBean.getContentImages()));

        System.out.println(TimeUtil.GetTime(true)+" 查询成功-->入参:"+newId+"--->>>查询结果:"+resultViewObject);
        return WebServerResponse.success(resultViewObject);
    }

    /**
     * 新闻列表
     * @return
     */
    @Override
    public WebServerResponse queryNewsList() {
        List<NewsDetail> resultBeanList=appletDataService.queryNewsList();
        if (resultBeanList == null) {
            System.out.println(TimeUtil.GetTime(true)+" 查询失败");
            return WebServerResponse.failure();
        }
        System.out.println(TimeUtil.GetTime(true)+" 查询成功--->>>查询结果:"+resultBeanList);
        return WebServerResponse.success(resultBeanList);
    }
}
