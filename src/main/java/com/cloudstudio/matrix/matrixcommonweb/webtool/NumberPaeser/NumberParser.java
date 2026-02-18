package com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser;

import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import lombok.Getter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName：NumberParser
 * @Author: matrix
 * @Date: 2026/2/18 20:54
 * @Description:
 */
public class NumberParser {
//    @Getter
//    private static final Map<Integer, Double> resultMap = new HashMap<>();

    public static class ParsedResult {
        public List<Integer> numbers;
        public double value;

        public ParsedResult(List<Integer> numbers, double value) {
            this.numbers = numbers;
            this.value = value;
        }
    }

    public static ParsedResult parseNumberString(String str) throws Exception {
        String cleaned = str
                .replaceAll("[（）]", "")                // 删除中文括号
                .replaceAll("，", ",")                   // 中文逗号转英文逗号
                .replaceAll("\\s+", ",")                 // 空白（包括空格、制表符等）替换为逗号
                .replaceAll(",+", ",")                    // 合并多个连续逗号为单个逗号
                .replaceAll(",$", "")                      // 去掉末尾可能多余的逗号
                .replaceAll("各个", "各")                   // 将“各个”替换为“各”（处理“各（个）”的情况）
                .replaceAll("个个", "个")                   // 将“个个”替换为“个”（处理“个（个）”的情况）
                .trim();
        Pattern eachPattern = Pattern.compile("^([\\d,]+)[各|个](\\d+)米$");
        Matcher eachMatcher = eachPattern.matcher(cleaned);
        if (eachMatcher.find()) {
            String numbersStr = eachMatcher.group(1);
            double value = Double.parseDouble(eachMatcher.group(2));
            List<Integer> numbers = parseNumbersString(numbersStr);
            return new ParsedResult(numbers, value);
        }
        Pattern totalPattern = Pattern.compile("^([\\d,]+)共(\\d+)米$");
        Matcher totalMatcher = totalPattern.matcher(cleaned);
        if (totalMatcher.find()) {
            String numbersStr = totalMatcher.group(1);
            double totalValue = Double.parseDouble(totalMatcher.group(2));
            List<Integer> numbers = parseNumbersString(numbersStr);
            if (numbers.isEmpty()) {
                throw new Exception("没有找到数字: " + str);
            }
            double valuePerNumber = totalValue / numbers.size();
            double roundedValue = Math.round(valuePerNumber * 100.0) / 100.0;
            return new ParsedResult(numbers, roundedValue);
        }
        throw new Exception("无法识别的格式: " + str + "，应该包含\"各\"、\"个\"或\"共\"关键字");
    }

    private static List<Integer> parseNumbersString(String numbersStr) {
        List<Integer> numbers = new ArrayList<>();
        String[] parts = numbersStr.split(",");

        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                try {
                    numbers.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException e) {
                    // 忽略无效数字
                }
            }
        }

        return numbers;
    }

    public static Map<Integer, Double> processStrings(List<String> strings) {
        Map<Integer, Double> resultMap = new HashMap<>();
        for (String str : strings) {
            try {
                ParsedResult parsed = parseNumberString(str);
                System.out.println(TimeUtil.GetTime(true)+"解析成功: \"" + str + "\" -> 数字: " +parsed.numbers + ", 每个值: " + parsed.value + "米");
                for (Integer num : parsed.numbers) {
                    double currentValue = parsed.value;
                    double existingValue = resultMap.getOrDefault(num, 0.0);
                    resultMap.put(num, existingValue + currentValue);
                }
            } catch (Exception error) {
                System.out.println(TimeUtil.GetTime(true)+"无法解析字符串: \"" + str + "\" - " + error.getMessage());
            }
        }

        return resultMap;
    }

    public static void printResult(Map<Integer, Double> resultMap) {
        System.out.println(TimeUtil.GetTime(true)+"\n累加结果:\t数字\t总米数");
        System.out.println("----------------");
        List<Integer> sortedKeys = new ArrayList<>(resultMap.keySet());
        Collections.sort(sortedKeys);
        for (Integer num : sortedKeys) {
            double value = resultMap.get(num);
            String displayValue;
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                displayValue = String.format("%.0f", value);
            } else {
                displayValue = String.format("%.2f", value);
            }
            System.out.println("\t"+num + "\t" + displayValue);
        }
    }
}
