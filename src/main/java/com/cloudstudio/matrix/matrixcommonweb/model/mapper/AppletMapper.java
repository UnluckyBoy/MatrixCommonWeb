package com.cloudstudio.matrix.matrixcommonweb.model.mapper;

import com.cloudstudio.matrix.matrixcommonweb.model.Applet.FeedbackBean;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.NewsDetail;
import com.cloudstudio.matrix.matrixcommonweb.model.Applet.OrganizationBean;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName：AppletMapper
 * @Author: matrix
 * @Date: 2026/1/10 19:15
 * @Description:小程序SQL接口
 */
@Service
@Mapper
@Repository
public interface AppletMapper {
    boolean cFeedBack(FeedbackBean feedbackBean); //写入反馈数据
    OrganizationBean queryOrganization(String organization_Id);//获取机构信息

    NewsDetail queryNewDetail(int newId);// 查询新闻信息
    List<NewsDetail> queryNewsList();// 查询新闻信息
}
