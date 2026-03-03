package com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser;

import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName：MatrixNumberPareser
 * @Author: matrix
 * @Date: 2026/2/28 19:59
 * @Description:重构分析算法
 */
public class MatrixNumberParser {
    // 保留的中文字符集合(包含数字汉字、生肖、单位、关键词)
    private static final Set<Character> KEEP_CHINESE = new HashSet<>(Arrays.asList(
            '零','一','二','三','四','五','六','七','八','九','十','百',
            '鼠','牛','虎','兔','龙','蛇','马','羊','猴','鸡','狗','猪', '肖',
            '元','斤','米','各','个','共','总','合','计','红','绿','蓝','单','双',
            '号','尾'
    )
    );

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
        Map<Integer, Double> resultMap = new TreeMap<>();
        for (String str : strings) {
            /*去除多余字符**/
            String filtered = filterChineseCharacters(str);
            System.out.println(TimeUtil.GetTime(true)+"\t去除多余字符后:"+filtered);
            String strRemoveEnd=removeSummary(filtered);
            System.out.println(TimeUtil.GetTime(true)+"\t排除末尾:"+strRemoveEnd);

            List<String> subStrings = splitByMiKeepingMi(strRemoveEnd);
            for (String subStr : subStrings) {
                System.out.println(TimeUtil.GetTime(true)+"\t截取米子串:"+subStr);
                Map<Integer, Double> partial = parseEnd(subStr);


                if (partial != null) {
                    System.out.println(TimeUtil.GetTime(true) + "\t结果: " + partial);
                    for (Map.Entry<Integer, Double> entry : partial.entrySet()) {
                        resultMap.put(entry.getKey(), resultMap.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
                    }
                } else {
                    System.out.println(TimeUtil.GetTime(true) + "\t无法解析: \"" + subStr + "\"");
                }
            }
        }
        return resultMap;
    }

    /**
     * 把米分割成多个独立子串
     * @param input
     * @return
     */
    private static List<String> splitByMiKeepingMi(String input) {
        // 匹配每个“米”之后的位置，分割时保留“米”在前一部分
        String[] array = input.split("(?<=米)");
        return Arrays.asList(array);
    }

    /**
     * 最后一个米字如果跟随的是:"总", "合", "共", "合计", "总共", "共计",则去掉这些后边内容
     * @param input
     * @return
     */
    private static String removeSummary(String input) {
        int index = input.length();
        while (true) {
            index = input.lastIndexOf("米", index - 1);
            if (index == -1) break;
            String after = input.substring(index + 1);
            for (String kw : new String[]{"总", "合", "共", "合计", "总共", "共计"}) {
                if (after.startsWith(kw)) {
                    return input.substring(0, index + 1);
                }
            }
        }
        return input;
    }

    /**
     * 过滤字符串：只保留保留集中的中文字符，其他中文字符移除；
     * 非中文字符(数字、字母、标点等)全部保留。
     *
     * @param input 原始字符串
     * @return 过滤后的字符串，若输入为 null 则返回 null
     */
    private static String filterChineseCharacters(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (isChinese(c)) {
                if (KEEP_CHINESE.contains(c)) {
                    sb.append(c);
                }
            } else {
                sb.append(c); // 非中文字符保留
            }
        }
        return sb.toString();
    }
    /**
     * 判断字符是否为中文字符（基于Unicode基本区范围）
     */
    private static boolean isChinese(char c) {
        // Unicode基本汉字区(4E00-9FFF)包含大多数常用汉字
        return c >= 0x4E00 && c <= 0x9FFF;
    }

    /**
     * 解析字符串，例如 "0尾各20米" 或 "5尾个30米"
     * @param input 输入字符串
     * @return 数字 -> 值的映射
     * @throws IllegalArgumentException 如果格式无法解析
     */
    private static Map<Integer, Double> parseEnd(String input) {
        // 去除所有空格
        String trimmed = input.replaceAll("\\s+", "");

        // 正则表达式： 尾数部分（数字和分隔符） + "尾" + "各/个" + 数值 + 可选单位
        // 分隔符允许： 、 . ; ， 等，这里用字符类 [、.;，] 加上数字
        // Pattern pattern = Pattern.compile("(\\d)尾[各个](\\d+)[元斤米]?$");
//        Pattern pattern = Pattern.compile("^([\\d、.;，]+)尾[各个](\\d+(?:\\.\\d+)?)(?:[元斤米])?$");
//        Matcher matcher = pattern.matcher(trimmed);
//        if (!matcher.matches()) {
//            System.out.println(TimeUtil.GetTime(true)+"\t解析失败");
//            return null;
//        }
//
//        int tail = Integer.parseInt(matcher.group(1));   // 尾数 x
//        double value = Integer.parseInt(matcher.group(2));  // 对应值 y
//        // 生成映射
//        Map<Integer, Double> result = new HashMap<>();
//        for (int i = 1; i <= 49; i++) {
//            if (i % 10 == tail) {
//                result.put(i, value);
//            }
//        }
//        return result;

        // 正则：捕获"尾"前面的所有字符，然后数值部分，可选单位
        Pattern pattern = Pattern.compile("^(.*?)尾[各个](\\d+(?:\\.\\d+)?)(?:[元斤米])?$");
        Matcher matcher = pattern.matcher(trimmed);
        if (!matcher.matches()) {
            System.out.println(TimeUtil.GetTime(true) + "\t解析失败: " + input);
            return null;
        }

        String tailPart = matcher.group(1);   // "尾"前面的部分
        double value = Double.parseDouble(matcher.group(2));

        // 从tailPart中提取所有数字序列
        Pattern digitPattern = Pattern.compile("\\d+");
        Matcher digitMatcher = digitPattern.matcher(tailPart);
        Set<Integer> tails = new HashSet<>(); // 使用Set去重
        while (digitMatcher.find()) {
            String numStr = digitMatcher.group();
            // 取最后一位作为尾数（个位数）
            int lastDigit = numStr.charAt(numStr.length() - 1) - '0';
            tails.add(lastDigit);
            // 如果数字有多位，可以给出提示
            if (numStr.length() > 1) {
                System.out.println(TimeUtil.GetTime(true) + "\t提示: 提取到多位数字 " + numStr + "，取其个位数 " + lastDigit + " 作为尾数");
            }
        }

        if (tails.isEmpty()) {
            System.out.println(TimeUtil.GetTime(true) + "\t错误: 未提取到有效尾数");
            return null;
        }

        // 生成映射
        Map<Integer, Double> result = new HashMap<>();
        for (int tail : tails) {
            for (int i = 1; i <= 49; i++) {
                if (i % 10 == tail) {
                    result.put(i, value);
                }
            }
        }
        return result;
    }
}
