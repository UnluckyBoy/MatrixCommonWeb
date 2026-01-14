package com.cloudstudio.matrix.matrixcommonweb.controller;

import com.cloudstudio.matrix.matrixcommonweb.model.StatisticsBean;
import com.cloudstudio.matrix.matrixcommonweb.service.SystemInfo.SystemMonitorService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName：DataController
 * @Author: matrix
 * @Date: 2024/12/20 21:30
 * @Description:数据控制类
 */
@Controller
@RequestMapping("/dataApi")
public class DataController {
    @Autowired
    SystemMonitorService systemMonitorService;

    private static final Gson gson=new Gson();

    /**
     * *获取系统信息
     * @param response
     * @throws IOException
     */
    @PostMapping("/getSystemMonitor")
    public void getSystemMonitor(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        System.out.println(TimeUtil.GetTime(true)+" -->入参:"+null);
        response.getWriter().write(gson.toJson(systemMonitorService.getSystemInfo()));
    }
}
