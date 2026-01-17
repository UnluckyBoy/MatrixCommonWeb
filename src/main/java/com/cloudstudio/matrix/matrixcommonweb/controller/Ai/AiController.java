package com.cloudstudio.matrix.matrixcommonweb.controller.Ai;

import com.cloudstudio.matrix.matrixcommonweb.model.AiBean.ChatRequest;
import com.cloudstudio.matrix.matrixcommonweb.service.Ai.AiService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.Ai.BaiduApiUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @ClassName：AiController
 * @Author: matrix
 * @Date: 2026/1/17 11:50
 * @Description:Ai控制类
 */
@RestController
@RequestMapping("/aiApi")
public class AiController {
    @Autowired
    private AiService AiService;

    private static final Gson gson=new GsonBuilder().serializeNulls().create();

    @RequestMapping("/baidu/chat")
    public void chat(HttpServletResponse response, @RequestBody ChatRequest request) throws IOException{
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(gson.toJson(AiService.chat(request)));
    }
}
