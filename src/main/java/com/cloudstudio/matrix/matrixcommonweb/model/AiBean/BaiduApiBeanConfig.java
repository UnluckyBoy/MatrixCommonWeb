package com.cloudstudio.matrix.matrixcommonweb.model.AiBean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName：BaiduApiBean
 * @Author: matrix
 * @Date: 2026/1/17 11:36
 * @Description:百度千帆Api实体
 */
@Data
@Component
@ConfigurationProperties(prefix = "qianfan")
public class BaiduApiBeanConfig {
    private String apiUrl;
    private String apiKey;
    private String model;
    private Integer timeoutSeconds;
}
