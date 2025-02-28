package com.cloudstudio.matrix.matrixcommonweb.service.Impl.userhandleImpl;

import com.cloudstudio.matrix.matrixcommonweb.model.UserInfoBean;
import com.cloudstudio.matrix.matrixcommonweb.service.userhandle.LoginService;
import com.cloudstudio.matrix.matrixcommonweb.service.userhandle.UserInfoService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName：LoginServiceImpl
 * @Author: matrix
 * @Date: 2025/2/6 22:41
 * @Description:登录实现类
 */
@Service("LoginService")
public class LoginServiceImpl implements LoginService {
    @Resource
    private RedisTemplate<String,Map<String,Object>> redisTemplate;

    @Autowired
    UserInfoService userInfoService;

    @Override
    public WebServerResponse login(String account, String password) {
        Map<String,Object> requestMap=new HashMap<>();
        String passTemp= MatrixEncodeUtil.decodeFromBase64(password);//先解密
        System.out.println(TimeUtil.GetTime(true)+"password:"+passTemp); // 输出原密码
        int index = passTemp.indexOf('+');
        if (index != -1) {
            String originalPass = passTemp.substring(0, index);
            System.out.println(TimeUtil.GetTime(true)+"原密码:"+originalPass); // 输出原密码
            requestMap.put("account",account);
            requestMap.put("pass",MatrixEncodeUtil.encodeTwice(originalPass));

            UserInfoBean userInfoBean=userInfoService.loginQuery(requestMap);
            if (userInfoBean==null) {
                System.out.println(TimeUtil.GetTime(true)+" ---登录失败");
                return WebServerResponse.failure("登录失败");
            }else{
                System.out.println(TimeUtil.GetTime(true)+" ---登录成功:"+userInfoBean);
                Map<String,Object> tempMap=new HashMap<>();
                tempMap.put("uAccount",userInfoBean.getUAccount());
                tempMap.put("uName",userInfoBean.getUName());
                tempMap.put("organization_name",userInfoBean.getOrganization_name());
                tempMap.put("headerImageUrl",userInfoBean.getHeaderImageUrl());
                tempMap.put("authority_key",userInfoBean.getAuthority_key());
                Map<String,Object> tokenResultMap=JwtUtil.generateToken(userInfoBean.getUAccount());
                if(!(Boolean) tokenResultMap.get("result")){
                    //存在token,删除redis缓存,使令牌无效
                    redisTemplate.delete((String)tokenResultMap.get("oldToken"));
                }
                redisTemplate.opsForValue().set((String)tokenResultMap.get("newToken"),tempMap,7, TimeUnit.DAYS);
                return WebServerResponse.success("登录成功","Bearer "+ tokenResultMap.get("newToken"));
            }
        } else {
            return WebServerResponse.failure("后台异常：密码解码ERROR!---"+passTemp);
        }
    }

    @Override
    public WebServerResponse getLoginInfo(String token) {
        /**
         * 校验token
         * 返回信息
         */
        token=token.substring(7);
        if(StringUtil.isEmptyOrNull(token)){
            return WebServerResponse.paramError();
        }else{
            Map<String,Object> strTokeMap=JwtUtil.validateToken(token);
            if(strTokeMap==null){
                return WebServerResponse.tokenError();//错误令牌
            }else{
                Map<String,Object> userInfoBean=redisTemplate.opsForValue().get(token);
                if(userInfoBean==null){
                    return WebServerResponse.tokenExpired();//令牌过期
                }
                return WebServerResponse.success(userInfoBean);
            }
        }
    }
}
