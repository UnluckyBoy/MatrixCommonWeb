package com.cloudstudio.matrix.matrixcommonweb.service.EmailService.Impl;

import com.cloudstudio.matrix.matrixcommonweb.model.UserInfoBean;
import com.cloudstudio.matrix.matrixcommonweb.service.EmailService.EmailService;
import com.cloudstudio.matrix.matrixcommonweb.service.userhandle.UserInfoService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.MatrixEncodeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName：EmailServiceImpl
 * @Author: matrix
 * @Date: 2026/2/19 23:26
 * @Description:邮箱发送验证码实现
 */
@Service("EmailService")
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    UserInfoService userInfoService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /***
     * 发送验证码
     * @param toEmail
     * @return
     * @throws MessagingException
     */
    @Override
    public final WebServerResponse sendVerificationCode(String toEmail) throws MessagingException {
        String redisKey = "email:" + toEmail;
        // 检查是否已存在
        if (redisTemplate.hasKey(redisKey)) {
            return WebServerResponse.winning("5分钟内已生成验证码，请勿重复请求");
        }

        // 生成6位随机验证码
        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        System.out.println(TimeUtil.GetTime(true)+"生成验证码：->>>"+code+"->>>用户:"+toEmail);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Matrix助手验证码");
        // HTML 内容，使用 CSS 实现首行缩进2字符
        String htmlContent = "<div>"
                + "尊敬的用户：<br>"
                + "<span style='padding-left:2em;'>您的验证码是：" + code + "，有效期为2分钟。如非本人操作，请忽略。</span>"
                + "</div>";
        helper.setText(htmlContent, true);  // true 表示启用 HTML
        mailSender.send(mimeMessage);
        redisTemplate.opsForValue().set(redisKey, code, 2, TimeUnit.MINUTES);
        return WebServerResponse.success();
    }

    /**
     * 校验验证码
     * @param email
     * @param inputCode
     * @return
     */
    @Override
    public final WebServerResponse verifyCode(String email,String inputCode) {
        String redisKey = "email:" + email;
        String savedCode = redisTemplate.opsForValue().get(redisKey);
        if (savedCode != null && savedCode.equals(inputCode)) {
            System.out.println(TimeUtil.GetTime(true)+"用户:"+email+"校验验证码->>>"+inputCode+"-成功");
            /*String passTemp=MatrixEncodeUtil.decodeFromBase64(password);
            int index = passTemp.indexOf('+');
            if (index != -1) {
                String originalPass = passTemp.substring(0, index);
                Map<String,Object> mapTemp = new HashMap<>();
                mapTemp.put("email",email);
                mapTemp.put("pass",MatrixEncodeUtil.encodeTwice(originalPass));
                UserInfoBean userInfoBean=userInfoService.emailLogin(mapTemp);
                if(userInfoBean!=null){
                    System.out.println(TimeUtil.GetTime(true)+"用户:"+email+"登录->>>成功");
                    redisTemplate.delete(redisKey);//校验通过则删除
                    return WebServerResponse.success(userInfoBean);
                }
                System.out.println(TimeUtil.GetTime(true)+"用户:"+email+"登录->>>失败");
                return WebServerResponse.loginFailure();
            }*/
            Map<String,Object> mapTemp = new HashMap<>();
            mapTemp.put("email",email);
            UserInfoBean userInfoBean=userInfoService.emailLogin(mapTemp);
            if(userInfoBean==null){
                boolean regisResult=userInfoService.regisLoginInfo(email);
                if(regisResult){
                    System.out.println(TimeUtil.GetTime(true)+"用户:"+email+"注册登录->>>成功");
                    redisTemplate.delete(redisKey);//校验通过则删除
                    return WebServerResponse.success(userInfoService.emailLogin(mapTemp));
                }
                System.out.println(TimeUtil.GetTime(true)+"用户:"+email+"登录->>>失败");
                return WebServerResponse.loginFailure();
            }
            System.out.println(TimeUtil.GetTime(true)+"用户:"+email+"登录->>>成功");
            redisTemplate.delete(redisKey);//校验通过则删除
            return WebServerResponse.success(userInfoBean);
        } else {
            System.out.println(TimeUtil.GetTime(true)+"用户:"+email+"校验验证码->>>"+inputCode+"-失败");
            return WebServerResponse.failure("验证码错误或已过期");
        }
    }
}
