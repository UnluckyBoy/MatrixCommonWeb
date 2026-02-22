package com.cloudstudio.matrix.matrixcommonweb.controller;

import com.cloudstudio.matrix.matrixcommonweb.model.requestBody.userRequest.LoginRequestBody;
import com.cloudstudio.matrix.matrixcommonweb.service.userhandle.LoginService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.MatrixEncodeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName：UserController
 * @Author: matrix
 * @Date: 2024/12/17 22:09
 * @Description:登录用户控制类
 */
@Controller
@RequestMapping("/userApi")
@Slf4j
public class UserController {
//    @Resource
//    private RedisTemplate<String,Map<String,Object>> redisTemplate;

//    @Autowired
//    UserInfoService userInfoService;
    @Autowired
    LoginService loginService;

    // private static final Gson gson=new Gson();//Json数据对象
    private static final Gson gson=new GsonBuilder().serializeNulls().create();//Json数据对象,强制将NULL返回


    /***********************查询逻辑:MySql库********************/
    /**
     * 登录查询
     * @param response
     * @param requestBody
     * @throws IOException
     */
    @PostMapping("/login")
    public final void Login(HttpServletResponse response, @RequestBody LoginRequestBody requestBody) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        System.out.println(TimeUtil.GetTime(true)+" -->入参:"+requestBody.toString());
        response.getWriter().write(gson.toJson(loginService.login(requestBody.getAccount(),requestBody.getPass())));
    }

    /**
     * token获取用户信息
     * @param response
     * @param token
     * @throws IOException
     */
    @PostMapping("/getLoginInfo")
    public final void getLoginInfo(HttpServletResponse response,
                                   @RequestHeader("Authorization") String token) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        System.out.println(TimeUtil.GetTime(true)+" -->入参:"+token);
        response.getWriter().write(gson.toJson(loginService.getLoginInfo(token)));
    }




    @RequestMapping("/test")
    public final void Test(HttpServletResponse response, @RequestParam("account") String account,
                           @RequestParam("pass") String pass) throws IOException {
        String encode= MatrixEncodeUtil.encodeTwice(pass);
        response.setContentType("application/json;charset=UTF-8");
        Map<String,Object> requestMap=new HashMap<>();
        requestMap.put("encode",encode);
        requestMap.put("decode",MatrixEncodeUtil.decodeTwice(encode));
        requestMap.put("encodeToBase64DoublePara",MatrixEncodeUtil.encodeToBase64DoublePara(pass,account));
        requestMap.put("decodeFromBase64DoublePara",MatrixEncodeUtil.decodeFromBase64(MatrixEncodeUtil.encodeToBase64DoublePara(pass,account)));
        response.getWriter().write(gson.toJson(WebServerResponse.success("请求成功",requestMap)));
    }

    @RequestMapping("/get_test")
    public final void get_test(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(gson.toJson(WebServerResponse.success("请求成功")));
    }
    /*********************查询逻辑:MySql库********************/


    /**********************公共逻辑********************/

    /***********************公共逻辑********************/
}
