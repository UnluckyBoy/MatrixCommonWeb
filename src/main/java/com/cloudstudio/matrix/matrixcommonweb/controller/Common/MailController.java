package com.cloudstudio.matrix.matrixcommonweb.controller.Common;

import com.cloudstudio.matrix.matrixcommonweb.model.requestBody.EmailBody.EmailRequestBody;
import com.cloudstudio.matrix.matrixcommonweb.service.EmailService.EmailService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.google.gson.Gson;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @ClassName：MailController
 * @Author: matrix
 * @Date: 2026/2/19 22:49
 * @Description:邮箱验证码控制类
 */
@Controller
@RequestMapping("/mailApi")
public class MailController {
    @Autowired
    private EmailService emailService;

    private static final Gson gson=new Gson();

    @GetMapping("/sendCode")
    public final void sendCode(HttpServletResponse response, @RequestParam String email) throws IOException, MessagingException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(gson.toJson(emailService.sendVerificationCode(email)));
    }

    @PostMapping("/verifyCode")
    public final void verifyCode(HttpServletResponse response, @RequestBody EmailRequestBody body) throws IOException, MessagingException {
        response.setContentType("application/json;charset=UTF-8");
        System.out.println(TimeUtil.GetTime(true)+"入参->>>email:"+body.getEmail()+"\tpass:"+body.getPassword());
        response.getWriter().write(gson.toJson(emailService.verifyCode(body.getEmail(),body.getInputCode())));
    }
}
