package com.cloudstudio.matrix.matrixcommonweb.controller.Applet;

import com.cloudstudio.matrix.matrixcommonweb.model.Applet.FeedbackBean;
import com.cloudstudio.matrix.matrixcommonweb.service.Applet.AppletService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * @ClassName：AppletController
 * @Author: matrix
 * @Date: 2026/1/9 23:07
 * @Description:小程序API
 */
@Controller
@RequestMapping("/AppletApi")
//@Slf4j
public class AppletController {
    @Autowired
    private AppletService appletService;

    private static final Gson getGson =new Gson();
    private static final Gson gson=new GsonBuilder().serializeNulls().create();

    @RequestMapping("/getOrganization")
    public void getOrganization(HttpServletResponse response, @RequestParam String organization_Id)throws IOException{
        response.setContentType("application/json;charset=UTF-8");

        System.out.println(TimeUtil.GetTime(true)+" -->入参:"+organization_Id);
        response.getWriter().write(gson.toJson(appletService.queryOrganization(organization_Id)));
    }

    @RequestMapping("/feedback")
    public void getFeedback(HttpServletResponse response, @RequestBody FeedbackBean feedbackBean) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        System.out.println(TimeUtil.GetTime(true)+" -->入参:"+feedbackBean.toString());
        response.getWriter().write(gson.toJson(appletService.submitFeedBack(feedbackBean)));
    }

    @RequestMapping("/getNewDetail")
    public void getNewDetail(HttpServletResponse response, @RequestParam int newId) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        System.out.println(TimeUtil.GetTime(true)+" -->入参:"+newId);
        response.getWriter().write(gson.toJson(appletService.queryNewDetail(newId)));
    }

    @RequestMapping("/getNewList")
    public void getNewList(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        System.out.println(TimeUtil.GetTime(true)+" -->查询新闻列表");
        response.getWriter().write(getGson.toJson(appletService.queryNewsList()));
    }
}
