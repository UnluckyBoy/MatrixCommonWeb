package com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser;

import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import lombok.Getter;

import java.sql.Time;
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

    // 保留的中文字符集合(包含数字汉字、生肖、单位、关键词)
    private static final Set<Character> KEEP_CHINESE = new HashSet<>(Arrays.asList(
            '零','一','二','三','四','五','六','七','八','九','十','百', '鼠','牛','虎','兔',
            '龙','蛇','马','羊','猴','鸡','狗','猪', '肖','元','斤','米','各','个','共','总','合','计')
    );

    // 生肖数字映射
    private static final Map<String, List<Integer>> ZODIAC_MAP = new HashMap<>();
    static {
        ZODIAC_MAP.put("鼠", Arrays.asList(7, 19, 31, 43));
        ZODIAC_MAP.put("牛", Arrays.asList(6, 18, 30, 42));
        ZODIAC_MAP.put("虎", Arrays.asList(5, 17, 29, 41));
        ZODIAC_MAP.put("兔", Arrays.asList(4, 16, 28, 40));
        ZODIAC_MAP.put("龙", Arrays.asList(3, 15, 27, 39));
        ZODIAC_MAP.put("蛇", Arrays.asList(2, 14, 26, 38));
        ZODIAC_MAP.put("马", Arrays.asList(1, 13, 25, 37, 49));
        ZODIAC_MAP.put("羊", Arrays.asList(12, 24, 36, 48));
        ZODIAC_MAP.put("猴", Arrays.asList(11, 23, 35, 47));
        ZODIAC_MAP.put("鸡", Arrays.asList(10, 22, 34, 46));
        ZODIAC_MAP.put("狗", Arrays.asList(9, 21, 33, 45));
        ZODIAC_MAP.put("猪", Arrays.asList(8, 20, 32, 44));
    }

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
            /**去除多余字符**/
            String filtered = filterChineseCharacters(str);
            System.out.println(TimeUtil.GetTime(true)+"\t去除多余字符后:"+filtered);

            // 先按米分割成多个独立子串
            // List<String> subStrings = splitByMi(str);
            List<String> subStrings = splitByMi(filtered);
            for (String sub : subStrings) {
                // 1. 先进行尾数展开
                String strToParse = expandTail(sub);

                // 2. 尝试键值对解析
                Map<Integer, Double> partial = parseKeyValuePattern(strToParse);
                if (partial != null) {
                    System.out.println(TimeUtil.GetTime(true) + "\t键值对解析成功: " + sub);
                } else {
                    String cleaned = preprocess(strToParse);
                    // 尝试生肖模式
                    partial = parseZodiacPattern(cleaned, sub);
                    if (partial == null) {
                        // 尝试“各”模式
                        partial = parseEachPattern(cleaned, sub);
                    }
                    if (partial == null) {
                        // 尝试“个”模式
                        partial = parseSinglePattern(cleaned, sub);
                    }
                    if (partial == null) {
                        // 尝试“共”模式
                        partial = parseTotalPattern(cleaned, sub);
                    }
                    if (partial == null) {
                        // 通用解析
                        partial = parseGeneralPattern(cleaned, sub);
                    }
                }

                if (partial != null) {
                    System.out.println(TimeUtil.GetTime(true) + "\t结果: " + partial);
                    for (Map.Entry<Integer, Double> entry : partial.entrySet()) {
                        resultMap.put(entry.getKey(), resultMap.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
                    }
                } else {
                    System.out.println(TimeUtil.GetTime(true) + "\t无法解析: \"" + sub + "\"");
                }
            }
        }
        return resultMap;
    }

    /**
     * 过滤字符串：只保留保留集中的中文字符，其他中文字符移除；
     * 非中文字符(数字、字母、标点等)全部保留。
     *
     * @param input 原始字符串
     * @return 过滤后的字符串，若输入为 null 则返回 null
     */
    public static String filterChineseCharacters(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (isChinese(c)) {
                if (KEEP_CHINESE.contains(c)) {
                    sb.append(c);
                }
                // 不在保留集中的中文字符直接丢弃
            } else {
                sb.append(c); // 非中文字符保留
            }
        }
        // System.out.println(TimeUtil.GetTime(true)+"\t去除内容:"+sb.toString());
        return sb.toString();
    }
    /**
     * 判断字符是否为中文字符（基于Unicode基本区范围）
     */
    private static boolean isChinese(char c) {
        // Unicode 基本汉字区 (4E00-9FFF) 包含了绝大多数常用汉字
        return c >= 0x4E00 && c <= 0x9FFF;
    }

    /**
     * 解析生肖模式，例如 "虎鸡肖各数30米"
     * 返回每个生肖对应数字到值的映射
     */
    private static Map<Integer, Double> parseZodiacPattern(String cleaned, String original) {
        // 预处理：如果“米”后面有“共”、“一共”或“总”，则截断至该“米”（包含“米”）
        int miIndex = cleaned.indexOf("米");
        if (miIndex != -1) {
            String afterMi = cleaned.substring(miIndex + 1);
            if (afterMi.contains("共") || afterMi.contains("一共") || afterMi.contains("总")) {
                cleaned = cleaned.substring(0, miIndex + 1); // 保留“米”及之前部分
            }
        }

        String zodiacPart;
        String rest;

        // 1. 优先以“肖”为分隔
        int index = cleaned.indexOf("肖");
        if (index != -1) {
            zodiacPart = cleaned.substring(0, index);
            rest = cleaned.substring(index + 1);
        } else {
            // 2. 无“肖”，则查找“各”或“共”作为分隔符
            int eachIndex = -1;
            int eachIndex1 = cleaned.indexOf("各");
            int eachIndex2 = cleaned.indexOf("个");
            if (eachIndex1 != -1) eachIndex = eachIndex1;
            if (eachIndex2 != -1) eachIndex = (eachIndex == -1) ? eachIndex2 : Math.min(eachIndex, eachIndex2);

            int totalIndex = cleaned.indexOf("共");
            if (eachIndex != -1 && (totalIndex == -1 || eachIndex < totalIndex)) {
                // “各”在前（或只有“各”）
                zodiacPart = cleaned.substring(0, eachIndex);
                rest = cleaned.substring(eachIndex);
            } else if (totalIndex != -1) {
                // “共”在前（或只有“共”）
                zodiacPart = cleaned.substring(0, totalIndex);
                rest = cleaned.substring(totalIndex);
            } else {
                // 既无“肖”也无“各/共”，无法解析
                System.out.println(TimeUtil.GetTime(true) + "\t无法解析（缺少分隔符）: " + original);
                return null;
            }
        }

        // 3. 提取所有生肖汉字（去重）
        Set<String> zodiacSet = new LinkedHashSet<>();
        Pattern zodiacCharPattern = Pattern.compile("[鼠牛虎兔龙蛇马羊猴鸡狗猪]");
        Matcher m = zodiacCharPattern.matcher(zodiacPart);
        while (m.find()) {
            zodiacSet.add(m.group());
        }
        if (zodiacSet.isEmpty()) return null;

        // 4. 解析数值部分（支持“各”或“共”模式）
        double value = -1;
        boolean isTotal = false;

        // 先尝试匹配“各”模式（可能带“数”字）
        Pattern eachPattern = Pattern.compile("^(?:各|个)?(?:数)?([\\d.]+|[" + CHINESE_NUMBERS + "]+)(?:元|斤|米)?$");
        Matcher eachMatcher = eachPattern.matcher(rest);
        if (eachMatcher.find()) {
            String valueStr = eachMatcher.group(1);
            if (valueStr.matches("\\d+(\\.\\d+)?")) {
                value = Double.parseDouble(valueStr);
            } else {
                value = chineseToNumber(valueStr);
            }
        }
        else {
            // 尝试匹配“共”模式
            Pattern totalPattern = Pattern.compile("^共([\\d.]+|[" + CHINESE_NUMBERS + "]+)(?:元|斤|米)?$");
            Matcher totalMatcher = totalPattern.matcher(rest);
            if (totalMatcher.find()) {
                String valueStr = totalMatcher.group(1);
                if (valueStr.matches("\\d+(\\.\\d+)?")) {
                    value = Double.parseDouble(valueStr);
                } else {
                    value = chineseToNumber(valueStr);
                }
                isTotal = true;
            } else {
                System.out.println(TimeUtil.GetTime(true) + "\t无法解析数值部分: " + original);
                return null;
            }
        }

        if (value < 0) return null;

        // 5. 收集所有生肖对应的数字
        List<Integer> allNumbers = new ArrayList<>();
        for (String zodiac : zodiacSet) {
            List<Integer> nums = ZODIAC_MAP.get(zodiac);
            if (nums != null) {
                allNumbers.addAll(nums);
            }
        }
        if (allNumbers.isEmpty()) return null;
        // 去重并排序
        allNumbers = new ArrayList<>(new TreeSet<>(allNumbers));
        // 6. 根据“共”或“各”分配数值
        if (isTotal) {
            double perValue = value / allNumbers.size();
            perValue = Math.round(perValue * 100.0) / 100.0; // 保留两位小数
            System.out.println(TimeUtil.GetTime(true) + "\t生肖共模式解析成功: " + original + " -> 总值" + value + "均分得" + perValue);
            return buildMap(allNumbers, perValue);
        } else {
            System.out.println(TimeUtil.GetTime(true) + "\t生肖各模式解析成功: " + original + " -> 每个值" + value);
            return buildMap(allNumbers, value);
        }
    }

    /**
     * 按“米”分割字符串，并处理“总”/“共”截断
     * 规则：
     * 1. 每个子串以“米”结尾；
     * 2. 若“米”后紧跟“总”或“共”，则丢弃之后的内容；
     * 3. 若子串内出现“总”或“共”（在“米”之前），则只保留到第一次出现之前，并终止处理。
     */
    private static List<String> splitByMi(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        int pos = 0;
        int len = input.length();
        while (pos < len) {
            int miIndex = input.indexOf("米", pos);
            if (miIndex == -1) {
                // 没有“米”，处理剩余部分(可能包含总/共)
                String remaining = input.substring(pos);
                remaining = truncateAtTotalOrGong(remaining);
                if (!remaining.isEmpty()) {
                    result.add(remaining);
                }
                break;
            }
            // 取从pos到miIndex+1的子串（包含“米”）
            String segment = input.substring(pos, miIndex + 1);

            // 检查segment内部是否有总/共（在米之前出现）
            String truncated = truncateAtTotalOrGong(segment);
            if (!truncated.equals(segment)) {
                // 有截断，只保留截断部分，并终止处理
                if (!truncated.isEmpty()) {
                    result.add(truncated);
                }
                break;
            }

            // 检查“米”后面是否紧跟“总”或“共”
            if (miIndex + 1 < len) {
                char nextChar = input.charAt(miIndex + 1);
                if (nextChar == '总' || nextChar == '共') {
                    segment = segment.replaceAll("^[^\\d/]+", ""); // 去掉开头非数字和斜杠的字符
                    result.add(segment);// 当前segment有效，但后面内容丢弃
                    break;
                }
            }

            result.add(segment);// 正常情况，加入segment，继续处理后面
            pos = miIndex + 1;
        }
        System.out.println(TimeUtil.GetTime(true)+"\t");
        return result;
    }

    /**
     * 截断字符串中第一次出现“总”或“共”之前的部分
     * 若未出现，返回原字符串
     */
    private static String truncateAtTotalOrGong(String s) {
        if (s == null || s.isEmpty()) return s;
        int totalIdx = s.indexOf("总");
        int gongIdx = s.indexOf("共");
        int cutIdx = -1;
        if (totalIdx != -1 && gongIdx != -1) {
            cutIdx = Math.min(totalIdx, gongIdx);
        } else if (totalIdx != -1) {
            cutIdx = totalIdx;
        } else if (gongIdx != -1) {
            cutIdx = gongIdx;
        }
        if (cutIdx != -1) {
            return s.substring(0, cutIdx);
        }
        return s;
    }

    /**
     * 将字符串中的 "尾" 表达式展开为对应的数字列表（1-49范围内）
     * 支持单个尾数（如 "0尾"）或多个尾数（如 ".1.9尾" 或 "1,9尾"）
     * 示例：
     *   ".1.9尾各5米" -> "1,11,21,31,41,9,19,29,39,49各5米"
     *   "五尾共100米" -> "5,15,25,35,45共100米"
     *   "0尾各5元" -> "10,20,30,40各5元"
     */
    private static String expandTail(String input) {
        if (input == null || input.isEmpty() || !input.contains("尾")) {
            return input;
        }

        // 匹配模式：前缀 + 尾数列表（可能包含点、逗号分隔） + "尾" + 后缀
        // 尾数可以是阿拉伯数字或中文数字
        Pattern tailPattern = Pattern.compile(
                "^(.*?)([\\d零一二三四五六七八九十百]+(?:[.,、]+[\\d零一二三四五六七八九十百]+)*)尾(.*)$",
                Pattern.DOTALL
        );
        Matcher m = tailPattern.matcher(input);
        if (!m.find()) {
            return input; // 无法识别尾数，原样返回
        }

        String prefix = m.group(1);      // 可能的前缀（通常为空）
        String tailExpr = m.group(2);     // 尾数列表表达式（如 "1.9" 或 "五"）
        String suffix = m.group(3);       // 剩余部分（如 "各5米"）

        // 解析尾数列表
        List<Integer> tails = parseTailList(tailExpr);
        if (tails.isEmpty()) {
            System.out.println("警告：未解析出有效尾数，忽略展开");
            return input;
        }

        // 生成所有尾数对应的数字集合（去重、排序）
        Set<Integer> allNumbers = new TreeSet<>();
        for (int tail : tails) {
            allNumbers.addAll(generateNumbersFromTail(tail));
        }

        // 将数字列表拼接为逗号分隔的字符串
        String numbersPart = allNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // 判断剩余部分是否需要插入“各”
        String processedSuffix;
        if (suffix.trim().startsWith("共")) {
            processedSuffix = suffix;
        } else {
            // 如果剩余部分为空，则仅返回数字列表（但这种情况不应该出现）
            if (suffix.trim().isEmpty()) {
                processedSuffix = "";
            } else {
                // 否则在剩余部分前插入“各”
                processedSuffix = "各" + suffix;
            }
        }

        // 构建新字符串
        String expanded = prefix + numbersPart + processedSuffix;
        System.out.println("尾数展开: \"" + input + "\" -> \"" + expanded + "\"");
        return expanded;
    }

    /**
     * 解析尾数列表表达式，返回整数尾数列表（0-9）
     * 支持分隔符：点（.）、逗号（,）、顿号（、）
     * 支持阿拉伯数字和中文数字
     */
    private static List<Integer> parseTailList(String expr) {
        List<Integer> result = new ArrayList<>();
        // 按分隔符分割
        String[] parts = expr.split("[.,、]+");
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            int tail;
            if (part.matches("\\d+")) {
                tail = Integer.parseInt(part);
            } else {
                tail = (int) chineseToNumber(part); // 中文数字转整数
            }

            if (tail >= 0 && tail <= 9) {
                result.add(tail);
            } else {
                System.out.println("警告：尾数 " + part + " 超出 0-9 范围，忽略");
            }
        }
        return result;
    }

    /**
     * 根据尾数生成 1-49 范围内的数字列表
     */
    private static List<Integer> generateNumbersFromTail(int tail) {
        List<Integer> result = new ArrayList<>();
        if (tail == 0) {
            // 0尾：10,20,30,40
            for (int i = 10; i <= 40; i += 10) {
                result.add(i);
            }
        } else {
            // 1-9尾：tail, 10+tail, 20+tail, 30+tail, 40+tail（不超过49）
            for (int i = 0; i <= 4; i++) {
                int num = i * 10 + tail;
                if (num >= 1 && num <= 49) {
                    result.add(num);
                }
            }
        }
        return result;
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
     * 支持:06/70米 或 08号10
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
     * 解析键值对列表格式(多个键值对用逗号分隔)
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
     * 解析单个数字(处理前导零)
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
    public static void printResult(Map<Integer, Double> Map) {
        System.out.println(TimeUtil.GetTime(true)+"\t========== 累计结果 ==========");
        System.out.println(TimeUtil.GetTime(true)+"\t数字\t总数值");
        System.out.println(TimeUtil.GetTime(true)+"\t----------------------------");

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
