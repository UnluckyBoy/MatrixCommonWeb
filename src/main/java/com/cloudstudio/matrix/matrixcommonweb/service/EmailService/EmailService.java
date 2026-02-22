package com.cloudstudio.matrix.matrixcommonweb.service.EmailService;

import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import jakarta.mail.MessagingException;

/**
 * @ClassName：EmailService
 * @Author: matrix
 * @Date: 2026/2/19 22:35
 * @Description:邮箱发送验证码
 */
public interface EmailService {
    WebServerResponse sendVerificationCode(String to) throws MessagingException;
    WebServerResponse verifyCode(String email, String inputCode);
}
