package com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser;

import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import lombok.Getter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName：NumberParser
 * @Author: matrix
 * @Date: 2026/2/18 20:54
 * @Description:
 */
public class NumberParser {

    // 中文字符集
    private static final String CHINESE_NUMBERS = "零一二三四五六七八九十百";

    /**
     * 对外接口：处理前端传来的多行字符串（含换行符）
     */
    public static Map<Integer, Double> processMultiLineString(String multiLineString) {
        List<String> lines = splitIntoLines(multiLineString);
        return processStrings(lines);
    }

    /**
     * 按行分割字符串
     */
    private static List<String> splitIntoLines(String multiLineString) {
        if (multiLineString == null || multiLineString.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(multiLineString.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 处理字符串列表，累计结果
     */
    public static Map<Integer, Double> processStrings(List<String> strings) {
        Map<Integer, Double> resultMap = new HashMap<>();
        for (String str : strings) {
            String cleaned = preprocess(str);
            Map<Integer, Double> partial = null;

            // 1. 尝试键值对解析（包括单个和多个）
            partial = parseKeyValuePattern(cleaned);
            if (partial != null) {
                System.out.println(TimeUtil.GetTime(true)+"\t键值对解析成功: " + str);
            } else {
                // 2. 尝试“各”模式
                partial = parseEachPattern(cleaned, str);
                if (partial == null) {
                    // 3. 尝试“个”模式
                    partial = parseSinglePattern(cleaned, str);
                }
                if (partial == null) {
                    // 4. 尝试“共”模式（总值均分）
                    partial = parseTotalPattern(cleaned, str);
                }
                if (partial == null) {
                    // 5. 通用解析（回退）
                    partial = parseGeneralPattern(cleaned, str);
                }
            }

            if (partial != null) {
                System.out.println("  结果: " + partial);
                for (Map.Entry<Integer, Double> entry : partial.entrySet()) {
                    resultMap.put(entry.getKey(),
                            resultMap.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
                }
            } else {
                System.out.println("无法解析: \"" + str + "\"");
            }
        }
        return resultMap;
    }

    /**
     * 预处理字符串：统一分隔符、去除多余字符
     */
    private static String preprocess(String str) {
        return str
                .replaceAll("[（）]", "")                // 删除中文括号
                .replaceAll("[，、\\s]+", ",")            // 中文逗号、顿号、空白统一为英文逗号
                .replaceAll("[\\.\\-\\/]+", ",")         // 点号、短横线、斜杠转为逗号
                .replaceAll(",+", ",")                    // 合并多个逗号
                .replaceAll("^,|,$", "")                  // 去掉首尾逗号
                .replace("一个", "个")                     // 统一“一个”为“个”
                .replace("一共", "共")                     // 统一“一共”为“共”
                .trim();
    }

    // ==================== 新增方法：解析单个键值对 ====================
    /**
     * 解析单个键值对格式
     * 支持：06/70米 或 08号10
     */
    private static Map<Integer, Double> parseSingleKeyValue(String cleaned) {
        // 匹配 "数字/数值" 可能带单位
        Pattern pattern1 = Pattern.compile("^(\\d+)/([\\d.]+)(?:元|斤|米)?$");
        Matcher m1 = pattern1.matcher(cleaned);
        if (m1.matches()) {
            int num = parseSingleNumber(m1.group(1));
            double val = Double.parseDouble(m1.group(2));
            return Collections.singletonMap(num, val);
        }

        // 匹配 "数字号数值"
        Pattern pattern2 = Pattern.compile("^(\\d+)号([\\d.]+)(?:元|斤|米)?$");
        Matcher m2 = pattern2.matcher(cleaned);
        if (m2.matches()) {
            int num = parseSingleNumber(m2.group(1));
            double val = Double.parseDouble(m2.group(2));
            return Collections.singletonMap(num, val);
        }

        return null;
    }

    /**
     * 解析键值对列表格式（多个键值对用逗号分隔）
     * 支持：30号30米，06号60米 或 15/40米、21/30米共70
     */
    private static Map<Integer, Double> parseKeyValuePattern(String cleaned) {
        // 先尝试单个键值对，如果匹配则直接返回
        Map<Integer, Double> single = parseSingleKeyValue(cleaned);
        if (single != null) {
            return single;
        }

        // 如果包含“共”，截取共之前的部分作为键值对主体
        int totalIndex = cleaned.indexOf("共");
        String kvPart = cleaned;
        if (totalIndex != -1) {
            kvPart = cleaned.substring(0, totalIndex);
            // 可在此处解析总值用于校验，暂忽略
        }

        String[] parts = kvPart.split(",");
        Map<Integer, Double> result = new HashMap<>();

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            // 尝试用单个键值对解析每个部分
            Map<Integer, Double> partMap = parseSingleKeyValue(part);
            if (partMap != null) {
                for (Map.Entry<Integer, Double> entry : partMap.entrySet()) {
                    result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
                }
            } else {
                // 如果某个部分解析失败，整个字符串不是有效的键值对列表
                return null;
            }
        }

        return result.isEmpty() ? null : result;
    }

    // ==================== 原有解析方法（略作调整） ====================

    /**
     * 解析“各”模式：数字列表各X元/斤/米
     */
    private static Map<Integer, Double> parseEachPattern(String cleaned, String original) {
        // 阿拉伯数字
        Pattern pattern = Pattern.compile("^([\\d,]+)各([\\d.]+)(?:元|斤|米)?$");
        Matcher matcher = pattern.matcher(cleaned);
        if (matcher.find()) {
            List<Integer> numbers = parseNumbers(matcher.group(1));
            double value = Double.parseDouble(matcher.group(2));
            return buildMap(numbers, value);
        }
        // 中文数字
        Pattern chinesePattern = Pattern.compile("^([\\d,]+)各([零一二三四五六七八九十百]+)(?:元|斤|米)?$");
        Matcher chineseMatcher = chinesePattern.matcher(cleaned);
        if (chineseMatcher.find()) {
            List<Integer> numbers = parseNumbers(chineseMatcher.group(1));
            double value = chineseToNumber(chineseMatcher.group(2));
            return buildMap(numbers, value);
        }
        return null;
    }

    /**
     * 解析“个”模式：数字列表个X元/斤/米
     */
    private static Map<Integer, Double> parseSinglePattern(String cleaned, String original) {
        // 阿拉伯数字
        Pattern pattern = Pattern.compile("^([\\d,]+)个([\\d.]+)(?:元|斤|米)?$");
        Matcher matcher = pattern.matcher(cleaned);
        if (matcher.find()) {
            List<Integer> numbers = parseNumbers(matcher.group(1));
            double value = Double.parseDouble(matcher.group(2));
            return buildMap(numbers, value);
        }
        // 中文数字
        Pattern chinesePattern = Pattern.compile("^([\\d,]+)个([零一二三四五六七八九十百]+)(?:元|斤|米)?$");
        Matcher chineseMatcher = chinesePattern.matcher(cleaned);
        if (chineseMatcher.find()) {
            List<Integer> numbers = parseNumbers(chineseMatcher.group(1));
            double value = chineseToNumber(chineseMatcher.group(2));
            return buildMap(numbers, value);
        }
        return null;
    }

    /**
     * 解析“共”模式：数字列表共X元/斤/米(总值均分)
     */
    private static Map<Integer, Double> parseTotalPattern(String cleaned, String original) {
        // 阿拉伯数字
        Pattern pattern = Pattern.compile("^([\\d,]+)共([\\d.]+)(?:元|斤|米)?$");
        Matcher matcher = pattern.matcher(cleaned);
        if (matcher.find()) {
            List<Integer> numbers = parseNumbers(matcher.group(1));
            double totalValue = Double.parseDouble(matcher.group(2));
            if (!numbers.isEmpty()) {
                double valuePerNumber = totalValue / numbers.size();
                double roundedValue = Math.round(valuePerNumber * 100.0) / 100.0;
                return buildMap(numbers, roundedValue);
            }
        }
        // 中文数字
        Pattern chinesePattern = Pattern.compile("^([\\d,]+)共([零一二三四五六七八九十百]+)(?:元|斤|米)?$");
        Matcher chineseMatcher = chinesePattern.matcher(cleaned);
        if (chineseMatcher.find()) {
            List<Integer> numbers = parseNumbers(chineseMatcher.group(1));
            double totalValue = chineseToNumber(chineseMatcher.group(2));
            if (!numbers.isEmpty()) {
                double valuePerNumber = totalValue / numbers.size();
                double roundedValue = Math.round(valuePerNumber * 100.0) / 100.0;
                return buildMap(numbers, roundedValue);
            }
        }
        return null;
    }

    /**
     * 通用解析：处理混合情况(如数字列表后跟多个关键字)
     */
    private static Map<Integer, Double> parseGeneralPattern(String cleaned, String original) {
        int firstChineseIndex = -1;
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (isChineseChar(c)) {
                firstChineseIndex = i;
                break;
            }
        }
        if (firstChineseIndex == -1) return null;

        String numbersPart = cleaned.substring(0, firstChineseIndex);
        String restPart = cleaned.substring(firstChineseIndex);

        List<Integer> numbers = parseNumbers(numbersPart);
        if (numbers.isEmpty()) return null;

        double value = extractValue(restPart, numbers.size());
        if (value < 0) return null;

        return buildMap(numbers, value);
    }

    /**
     * 从关键字部分提取数值
     */
    private static double extractValue(String restPart, int numberCount) {
        // 尝试“各X”
        Pattern eachPattern = Pattern.compile("各([\\d.]+|[" + CHINESE_NUMBERS + "]+)");
        Matcher eachMatcher = eachPattern.matcher(restPart);
        if (eachMatcher.find()) {
            String valueStr = eachMatcher.group(1);
            if (valueStr.matches("\\d+(\\.\\d+)?")) {
                return Double.parseDouble(valueStr);
            } else {
                return chineseToNumber(valueStr);
            }
        }

        // 尝试“个X”
        Pattern singlePattern = Pattern.compile("[个一]([\\d.]+|[" + CHINESE_NUMBERS + "]+)");
        Matcher singleMatcher = singlePattern.matcher(restPart);
        if (singleMatcher.find()) {
            String valueStr = singleMatcher.group(1);
            if (valueStr.matches("\\d+(\\.\\d+)?")) {
                return Double.parseDouble(valueStr);
            } else {
                return chineseToNumber(valueStr);
            }
        }

        // 尝试“共X”（总值均分）
        Pattern totalPattern = Pattern.compile("共([\\d.]+|[" + CHINESE_NUMBERS + "]+)");
        Matcher totalMatcher = totalPattern.matcher(restPart);
        if (totalMatcher.find()) {
            String valueStr = totalMatcher.group(1);
            double totalValue;
            if (valueStr.matches("\\d+(\\.\\d+)?")) {
                totalValue = Double.parseDouble(valueStr);
            } else {
                totalValue = chineseToNumber(valueStr);
            }
            return totalValue / numberCount;
        }

        return -1;
    }

    /**
     * 将数字列表和公共值转换为Map
     */
    private static Map<Integer, Double> buildMap(List<Integer> numbers, double value) {
        Map<Integer, Double> map = new HashMap<>();
        for (int num : numbers) {
            map.put(num, value);
        }
        return map;
    }

    /**
     * 解析逗号分隔的数字列表
     */
    private static List<Integer> parseNumbers(String numbersStr) {
        List<Integer> numbers = new ArrayList<>();
        if (numbersStr == null || numbersStr.isEmpty()) return numbers;
        String[] parts = numbersStr.split(",");
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                try {
                    String numStr = part.trim().replaceFirst("^0+", "");
                    if (numStr.isEmpty()) numStr = "0";
                    numbers.add(Integer.parseInt(numStr));
                } catch (NumberFormatException e) {
                    // 忽略无效数字
                }
            }
        }
        return numbers;
    }

    /**
     * 解析单个数字（处理前导零）
     */
    private static int parseSingleNumber(String numStr) {
        String cleaned = numStr.replaceFirst("^0+", "");
        if (cleaned.isEmpty()) return 0;
        return Integer.parseInt(cleaned);
    }

    /**
     * 判断是否为中文字符
     */
    private static boolean isChineseChar(char c) {
        return (c >= 0x4E00 && c <= 0x9FA5);
    }

    /**
     * 中文数字转阿拉伯数字
     */
    private static double chineseToNumber(String chinese) {
        if (chinese == null || chinese.isEmpty()) return 0;
        Map<String, Double> map = new HashMap<>();
        map.put("零", 0.0);
        map.put("一", 1.0);
        map.put("二", 2.0);
        map.put("三", 3.0);
        map.put("四", 4.0);
        map.put("五", 5.0);
        map.put("六", 6.0);
        map.put("七", 7.0);
        map.put("八", 8.0);
        map.put("九", 9.0);
        map.put("十", 10.0);
        map.put("十一", 11.0);
        map.put("十二", 12.0);
        map.put("十三", 13.0);
        map.put("十四", 14.0);
        map.put("十五", 15.0);
        map.put("十六", 16.0);
        map.put("十七", 17.0);
        map.put("十八", 18.0);
        map.put("十九", 19.0);
        map.put("二十", 20.0);
        map.put("二十一", 21.0);
        map.put("二十二", 22.0);
        map.put("二十三", 23.0);
        map.put("二十四", 24.0);
        map.put("二十五", 25.0);
        map.put("二十六", 26.0);
        map.put("二十七", 27.0);
        map.put("二十八", 28.0);
        map.put("二十九", 29.0);
        map.put("三十", 30.0);
        map.put("三十一", 31.0);
        map.put("三十二", 32.0);
        map.put("三十三", 33.0);
        map.put("三十四", 34.0);
        map.put("三十五", 35.0);
        map.put("三十六", 36.0);
        map.put("三十七", 37.0);
        map.put("三十八", 38.0);
        map.put("三十九", 39.0);
        map.put("四十", 40.0);
        map.put("四十一", 41.0);
        map.put("四十二", 42.0);
        map.put("四十三", 43.0);
        map.put("四十四", 44.0);
        map.put("四十五", 45.0);
        map.put("四十六", 46.0);
        map.put("四十七", 47.0);
        map.put("四十八", 48.0);
        map.put("四十九", 49.0);
        map.put("五十", 50.0);
        map.put("百", 100.0);
        return map.getOrDefault(chinese, 0.0);
    }

    /**
     * 打印结果
     */
    public static final void printResult(Map<Integer, Double> Map) {
        System.out.println(TimeUtil.GetTime(true)+"========== 累计结果 ==========");
        System.out.println(TimeUtil.GetTime(true)+"数字\t总数值");
        System.out.println(TimeUtil.GetTime(true)+"----------------------------");

        List<Integer> sortedKeys = new ArrayList<>(Map.keySet());
        Collections.sort(sortedKeys);

        for (Integer num : sortedKeys) {
            double value = Map.get(num);
            String displayValue;
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                displayValue = String.format("%.0f", value);
            } else {
                displayValue = String.format("%.2f", value);
            }
            System.out.println(TimeUtil.GetTime(true)+"\t"+num +"\t" + displayValue);
        }
    }
}
