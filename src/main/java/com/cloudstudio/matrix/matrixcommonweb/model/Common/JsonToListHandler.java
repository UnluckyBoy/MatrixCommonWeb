package com.cloudstudio.matrix.matrixcommonweb.model.Common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName：JsonToListHandler
 * @Author: matrix
 * @Date: 2026/1/14 13:13
 * @Description:Json工具类
 */
@Component
public class JsonToListHandler {

    private final ObjectMapper objectMapper;
    private static final String serverImgBaseUrl="https://matrix.cpolar.cn/image";

    public JsonToListHandler() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 将contentParagraphs JSON字符串转换为有序的List<String>
     */
    public List<String> parseParagraphs(String contentParagraphsJson) {

        List<String> paragraphs = new ArrayList<>();
        try {
            if (contentParagraphsJson != null && !contentParagraphsJson.isEmpty()) {
                // 将JSON解析为Map
                Map<String, String> paragraphMap = objectMapper.readValue(
                        contentParagraphsJson,
                        new TypeReference<Map<String, String>>() {}
                );

                // 按照键的顺序排序 (p1, p2, p3...)
                List<String> sortedKeys = new ArrayList<>(paragraphMap.keySet());
                sortedKeys.sort(this::compareParagraphKeys);

                // 按照排序后的键获取值
                for (String key : sortedKeys) {
                    String paragraph = paragraphMap.get(key);
                    if (paragraph != null && !paragraph.trim().isEmpty()) {
                        paragraphs.add(paragraph);
                    }
                }
            }
        } catch (Exception e) {
            // 如果JSON解析失败，返回空列表或尝试其他解析方式
            paragraphs = tryAlternativeParse(contentParagraphsJson);
        }
        return paragraphs;
    }

    /**
     * 比较段落键（p1, p2, p3...）
     */
    private int compareParagraphKeys(String key1, String key2) {
        try {
            int num1 = extractNumber(key1);
            int num2 = extractNumber(key2);
            return Integer.compare(num1, num2);
        } catch (Exception e) {
            return key1.compareTo(key2);
        }
    }

    /**
     * 从键中提取数字
     */
    private int extractNumber(String key) {
        if (key.startsWith("p")) {
            return Integer.parseInt(key.substring(1));
        }
        return Integer.parseInt(key);
    }

    /**
     * 将contentImages JSON字符串转换为List<String>
     */
    public List<String> parseImages(String contentImagesJson) {
        List<String> images = new ArrayList<>();
        try {
            if (contentImagesJson != null && !contentImagesJson.isEmpty()) {
                // 将JSON解析为Map
                Map<String, String> imageMap = objectMapper.readValue(
                        contentImagesJson,
                        new TypeReference<Map<String, String>>() {}
                );

                // 按照键的顺序排序 (img1, img2, img3...)
                List<String> sortedKeys = new ArrayList<>(imageMap.keySet());
                sortedKeys.sort(this::compareImageKeys);

                // 按照排序后的键获取值
                for (String key : sortedKeys) {
                    String image = imageMap.get(key);
                    if (image != null && !image.trim().isEmpty()) {
                        images.add(serverImgBaseUrl+image);
                    }
                }
            }
        } catch (Exception e) {
            // 如果JSON解析失败，尝试其他解析方式
            images = tryAlternativeParse(contentImagesJson);
        }
        return images;
    }

    /**
     * 比较图片键（img1, img2, img3...）
     */
    private int compareImageKeys(String key1, String key2) {
        try {
            int num1 = extractImageNumber(key1);
            int num2 = extractImageNumber(key2);
            return Integer.compare(num1, num2);
        } catch (Exception e) {
            return key1.compareTo(key2);
        }
    }

    /**
     * 从图片键中提取数字
     */
    private int extractImageNumber(String key) {
        if (key.startsWith("img")) {
            return Integer.parseInt(key.substring(3));
        }
        return Integer.parseInt(key);
    }

    /**
     * 尝试其他解析方式（如果标准的JSON解析失败）
     */
    private List<String> tryAlternativeParse(String jsonString) {
        List<String> result = new ArrayList<>();
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return result;
        }

        // 尝试简单的解析：如果字符串以{开头，以}结尾，尝试手动解析
        if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
            String content = jsonString.substring(1, jsonString.length() - 1);
            // 按逗号分割键值对
            String[] pairs = content.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String value = keyValue[1].trim();
                    // 去掉引号
                    value = value.replaceAll("\"", "").trim();
                    if (!value.isEmpty()) {
                        result.add(value);
                    }
                }
            }
        }
        return result;
    }
}
