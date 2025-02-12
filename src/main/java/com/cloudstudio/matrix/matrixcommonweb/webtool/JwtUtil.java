package com.cloudstudio.matrix.matrixcommonweb.webtool;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName：JWTUtil
 * @Author: matrix
 * @Date: 2025/2/6 23:12
 * @Description:JWT工具类
 */
public class JwtUtil {
    private static final String SECRET = "MATRIX9527SHUAIXIAOHAIQWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
    private static final long ACTIVITY_TIME=1000 * 3600 * 24;//有效时间1天

    // 使用线程安全的 Map 和锁来保护对共享资源的访问
    private static final Map<String, String> accountTokenMap = new HashMap<>();
    private static final Map<String, Long> tokenTimestampMap = new HashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();

    //public static String generateToken(String account) {
        //Map<String, Object> claims = new HashMap<>();
        //return createToken(claims, account);
    //}
    public static Map<String,Object> generateToken(String account) {
        Map<String,Object> resultMap = new HashMap<>();
        lock.lock();
        try {
            String existingToken = accountTokenMap.get(account);// 检查账号是否已经有一个活动的 token
            String newToken = createToken(new HashMap<>(), account);// 生成新的 token
            //if (existingToken != null && isTokenActive(existingToken)) {
            if (existingToken != null) {
                /**
                 * 如果存在token
                 * 返回存在result:false-有token;true-没有token
                 * 以及新旧token
                 */
                System.out.println("账户:" + account + " 已存在Token: " + existingToken);
                resultMap.put("result",false);
                resultMap.put("oldToken",existingToken);
                resultMap.put("newToken",newToken);
                accountTokenMap.remove(account);//去除旧token
            }else{
                resultMap.put("result",true);
                resultMap.put("oldToken",null);
                resultMap.put("newToken",newToken);
            }
            accountTokenMap.put(account, newToken);// 更新存储的 token 和时间戳
            tokenTimestampMap.put(newToken, System.currentTimeMillis());
            return resultMap;
        } finally {
            lock.unlock();
        }
    }

    // 检查 token 是否仍然有效
    private static boolean isTokenActive(String token) {
        Long timestamp = tokenTimestampMap.get(token);
        return timestamp != null && System.currentTimeMillis() - timestamp <= ACTIVITY_TIME;
    }

    /**
     * 生成token
     * @param claims
     * @param account
     * @return
     */
    private static String createToken(Map<String, Object> claims, String account) {
        return Jwts.builder()
                .claims(claims)
                .subject(account)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACTIVITY_TIME)) //一天
                .signWith(SignatureAlgorithm.HS256,SECRET)
                .compact();
    }

    /**
     * 校验token
     * @param token
     * @return
     */
    public static Map<String, Object> validateToken(String token) {
        Jwt<?, ?> parsedToken = Jwts.parser().setSigningKey(SECRET).build().parse(token);
        return (Map<String, Object>) parsedToken.getBody();
    }
}
