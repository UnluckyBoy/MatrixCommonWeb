package com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName：BigNumbertoSimpleTool
 * @Author: matrix
 * @Date: 2026/3/21 16:33
 * @Description:转换字符串中，中文或者大写数字为阿拉伯数字
 */
public class BigNumberToSimpleTool {
    private static final Map<Character, Integer> DIGIT_MAP = new HashMap<>();
    private static final Map<Character, Integer> UNIT_MAP = new HashMap<>();

    static {
        // 数字映射（小写 + 大写）
        DIGIT_MAP.put('零', 0); DIGIT_MAP.put('〇', 0);
        DIGIT_MAP.put('一', 1); DIGIT_MAP.put('壹', 1);
        DIGIT_MAP.put('二', 2); DIGIT_MAP.put('贰', 2); DIGIT_MAP.put('两', 2);
        DIGIT_MAP.put('三', 3); DIGIT_MAP.put('叁', 3);
        DIGIT_MAP.put('四', 4); DIGIT_MAP.put('肆', 4);
        DIGIT_MAP.put('五', 5); DIGIT_MAP.put('伍', 5);
        DIGIT_MAP.put('六', 6); DIGIT_MAP.put('陆', 6);
        DIGIT_MAP.put('七', 7); DIGIT_MAP.put('柒', 7);
        DIGIT_MAP.put('八', 8); DIGIT_MAP.put('捌', 8);
        DIGIT_MAP.put('九', 9); DIGIT_MAP.put('玖', 9);

        // 单位映射
        UNIT_MAP.put('十', 10);   UNIT_MAP.put('拾', 10);
        UNIT_MAP.put('百', 100);  UNIT_MAP.put('佰', 100);
        UNIT_MAP.put('千', 1000); UNIT_MAP.put('仟', 1000);
        UNIT_MAP.put('万', 10000); UNIT_MAP.put('萬', 10000);
        UNIT_MAP.put('亿', 100000000); UNIT_MAP.put('億', 100000000);
    }

    /**
     * 判断字符是否为中文数字相关字符
     */
    private static boolean isChineseNumberChar(char c) {
        return DIGIT_MAP.containsKey(c) || UNIT_MAP.containsKey(c);
    }

    /**
     * 转换单个中文数字子串（仅支持简单形式）
     *
     * @param s 中文数字子串，如 "五十" 或 "五"
     * @return 对应的数值，若无法解析则返回 null
     */
    private static Long parseSimpleChineseNumber(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        int len = s.length();

        // 情况1：单个数字，如 "五" -> 5
        if (len == 1) {
            Integer digit = DIGIT_MAP.get(s.charAt(0));
            return digit != null ? (long) digit : null;
        }

        // 情况2：两个字符，且第一个是数字、第二个是单位，如 "五十" -> 5*10 = 50
        if (len == 2) {
            char first = s.charAt(0);
            char second = s.charAt(1);
            Integer digit = DIGIT_MAP.get(first);
            Integer unit = UNIT_MAP.get(second);
            if (digit != null && unit != null) {
                return (long) digit * unit;
            }
        }

        // 其他复杂形式不予处理（可根据需要扩展）
        return null;
    }

    /**
     * 将字符串中的所有简单中文数字转换为阿拉伯数字
     */
    public static String convertSimpleChineseNumbers(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        int n = input.length();

        while (i < n) {
            char c = input.charAt(i);
            if (isChineseNumberChar(c)) {
                int start = i;
                // 连续收集最多2个字符（简单形式只考虑长度≤2）
                // 若你想支持更长的简单组合，可以增加长度限制，但这里为了简单只取2个
                while (i < n && isChineseNumberChar(input.charAt(i))) {
                    i++;
                    // 限制长度，避免无限收集（简单情况下最多2个字符）
                    if (i - start >= 2) {
                        break;
                    }
                }
                String chineseNum = input.substring(start, i);
                Long number = parseSimpleChineseNumber(chineseNum);
                if (number != null) {
                    result.append(number);
                } else {
                    // 无法解析则保留原样
                    result.append(chineseNum);
                }
            } else {
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }
}
