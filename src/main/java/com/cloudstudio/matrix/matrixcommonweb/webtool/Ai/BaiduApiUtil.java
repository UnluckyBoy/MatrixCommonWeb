package com.cloudstudio.matrix.matrixcommonweb.webtool.Ai;


import com.cloudstudio.matrix.matrixcommonweb.model.AiBean.BaiduApiBeanConfig;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName：BaiduApiUtil
 * @Author: matrix
 * @Date: 2026/1/17 11:22
 * @Description:百度人工智能请求工具类
 */
@Component
public class BaiduApiUtil {
    private final OkHttpClient httpClient;
    private final BaiduApiBeanConfig qianfanConfig;
    private final ObjectMapper objectMapper;

    public BaiduApiUtil(BaiduApiBeanConfig qianfanConfig, ObjectMapper objectMapper) {
        this.qianfanConfig = qianfanConfig;
        this.objectMapper = objectMapper;

        // 构建OkHttpClient，使用配置的超时时间
        this.httpClient = new OkHttpClient().newBuilder()
            .readTimeout(qianfanConfig.getTimeoutSeconds(), TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    }

    /**
     * 通用方法,调用API
     * @param content 用户输入的内容
     * @return API响应结果
     */
    public String callQianfanAPI(String content) throws IOException {
        // 构建请求体JSON
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", qianfanConfig.getModel());

        // 创建messages数组
        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode message = objectMapper.createObjectNode();
        message.put("role", "user");
        message.put("content", content);
        messages.add(message);

        requestBody.set("messages", messages);
        // 转换为JSON字符串
        String jsonBody = requestBody.toString();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, mediaType);

        // 构建请求
        Request request = new Request.Builder()
                .url(qianfanConfig.getApiUrl())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + qianfanConfig.getApiKey())
                .build();

        // 发送请求并获取响应
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body().string();
                throw new IOException("HTTP请求失败，状态码: " + response.code() + ", 响应: " + errorBody);
            }

            String responseBody = response.body().string();
            System.out.println(TimeUtil.GetTime(true)+"API调用成功，响应内容长度:"+responseBody.length());

            // 解析响应,提取需要的内容
            return parseResponse(responseBody);
        }
    }

    /**
     * 解析API响应，提取回答内容
     */
    private String parseResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // 从不同路径提取content
            if (jsonNode.has("result")) {
                // 一些API返回格式
                return jsonNode.get("result").asText();
            } else if (jsonNode.has("choices") && jsonNode.get("choices").isArray()) {
                // OpenAI兼容格式
                JsonNode choices = jsonNode.get("choices");
                if (!choices.isEmpty()) {
                    JsonNode firstChoice = choices.get(0);
                    if (firstChoice.has("message")) {
                        JsonNode message = firstChoice.get("message");
                        if (message.has("content")) {
                            return message.get("content").asText();
                        }
                    } else if (firstChoice.has("text")) {
                        return firstChoice.get("text").asText();
                    }
                }
            } else if (jsonNode.has("data")) {
                // 其他可能的格式
                JsonNode data = jsonNode.get("data");
                if (data.has("content")) {
                    return data.get("content").asText();
                }
            }

            // 如无法解析,返回原始响应或格式化后的
            return jsonNode.toPrettyString();
        } catch (Exception e) {
            System.out.println(TimeUtil.GetTime(true)+"解析API响应失败:"+e.getMessage());
            return responseBody; // 解析失败时返回原始响应
        }
    }
}
