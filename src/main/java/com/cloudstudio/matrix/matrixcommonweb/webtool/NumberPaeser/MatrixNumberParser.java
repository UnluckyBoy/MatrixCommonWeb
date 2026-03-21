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
            '壹','贰','叁','肆','伍','陆','柒','捌','玖','拾','佰','仟','萬',
            '鼠','牛','虎','兔','龙','蛇','马','羊','猴','鸡','狗','猪', '肖',
            '元','斤','米','块','各','个','共','总','合','计','红','绿','蓝','单','双',
            '号','尾','头')
    );

    // 波色数字集合
    private static final Set<Integer> RED_WAVE = new HashSet<>(Arrays.asList(
            1, 2, 7, 8, 12, 13, 18, 19, 23, 24, 29, 30, 34, 35, 40, 45, 46
    ));
    private static final Set<Integer> BLUE_WAVE = new HashSet<>(Arrays.asList(
            3, 4, 9, 10, 14, 15, 20, 25, 26, 31, 36, 37, 41, 42, 47, 48
    ));
    private static final Set<Integer> GREEN_WAVE = new HashSet<>(Arrays.asList(
            5, 6, 11, 16, 17, 21, 22, 27, 28, 32, 33, 38, 39, 43, 44, 49
    ));

    // 波色映射
    private static final Map<String, Set<Integer>> WAVE_MAP = new HashMap<>();
    static {
        WAVE_MAP.put("红", RED_WAVE);
        WAVE_MAP.put("蓝", BLUE_WAVE);
        WAVE_MAP.put("绿", GREEN_WAVE);
    }

    // 生肖对应的号码映射
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
            /* 去除多余字符 */
            String filtered = filterChineseCharacters(str);
            System.out.println(TimeUtil.GetTime(true) + "\t去除多余字符后:" + filtered);
            String strRemoveEnd = removeSummary(filtered);
            System.out.println(TimeUtil.GetTime(true) + "\t排除末尾:" + strRemoveEnd);

            List<String> subStrings = splitByMiKeepingMi(strRemoveEnd);
            for (String subStr : subStrings) {
                System.out.println(TimeUtil.GetTime(true) + "\t截取米子串:" + subStr);

                String subStrTrimToValidStart = trimToValidStart(subStr);
                System.out.println(TimeUtil.GetTime(true) + "\t排除首字符:" + subStrTrimToValidStart);

                String toSimpleNumStr=BigNumberToSimpleTool.convertSimpleChineseNumbers(subStrTrimToValidStart);
                System.out.println(TimeUtil.GetTime(true) + "\t转小写:" + toSimpleNumStr);

                Map<Integer, Double> partial;

                // 1. 最高优先级：头解析（parseHeadNum）
                Map<Integer, Double> headResult = parseHeadNum(toSimpleNumStr);
                if (headResult != null) {
                    partial = headResult;
                    System.out.println(TimeUtil.GetTime(true) + "\t头解析-parseHeadNum解析成功");
                } else {
                    // 2. 尾解析（parseEndNum）
                    Map<Integer, Double> endNumResult = parseEndNum(toSimpleNumStr);
                    if (endNumResult != null) {
                        partial = endNumResult;
                        System.out.println(TimeUtil.GetTime(true) + "\t尾解析-parseEndNum解析成功");
                    } else {
                        // 3. 波色解析
                        Map<Integer, Double> colorResult = parseColorNum(toSimpleNumStr);
                        if (colorResult != null && !colorResult.isEmpty()) {
                            partial = colorResult;
                            System.out.println(TimeUtil.GetTime(true) + "\t波色解析-parseColorNum解析成功");
                        } else {
                            // 4. 生肖解析
                            Map<Integer, Double> zodiacResult = parseZodiacValue(toSimpleNumStr);
                            if (!zodiacResult.isEmpty()) {
                                partial = zodiacResult;
                                System.out.println(TimeUtil.GetTime(true) + "\t生肖解析-parseZodiacValue解析成功");
                            } else {
                                // 5. 根据是否包含“各/个”选择基础解析器
                                boolean hasGe = containsGeOrGe(toSimpleNumStr);
                                if (!hasGe) {
                                    partial = parseSingle(toSimpleNumStr);
                                    System.out.println(TimeUtil.GetTime(true) + "\t“各/个”选择基础解析-parseSingle解析");
                                } else {
                                    partial = parseCommon(toSimpleNumStr);
                                    System.out.println(TimeUtil.GetTime(true) + "\t使用parseCommon解析");
                                }
                            }
                        }
                    }
                }

                // 如果解析结果为空，则警告并跳过
                if (partial.isEmpty()) {
                    System.out.println(TimeUtil.GetTime(true) + "\t警告: 无法解析:" + toSimpleNumStr );
                    continue;
                }

                System.out.println(TimeUtil.GetTime(true) + "\t结果: " + partial);
                for (Map.Entry<Integer, Double> entry : partial.entrySet()) {
                    resultMap.put(entry.getKey(), resultMap.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
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
        // 匹配每个“米”或“块”之后的位置，分割时保留“米”或“块”在前一部分
        String[] array = input.split("(?<=米|块)");
        return Arrays.asList(array);
    }

    /**
     * 去除字符串开头不属于保留中文字符集且不是数字的字符，直到首字符符合条件。
     * @param str 输入字符串
     * @return 处理后的字符串，如果全部字符均被去除则返回空字符串
     */
    private static String trimToValidStart(String str) {
        if (str == null || str.isEmpty()) {
            return str; // 返回原样（null或空串）
        }
        int index = 0;
        while (index < str.length()) {
            char ch = str.charAt(index);
            // 判断是否为ASCII数字或保留中文字符
            if ((ch >= '0' && ch <= '9') || KEEP_CHINESE.contains(ch)) {
                return str.substring(index).replaceAll("号","-"); // 从合法位置开始截取,同时把'号'字调整为'-'
            }
            index++; // 否则跳过当前字符
        }
        return null; // 全部字符均非法
    }

    /**
     * 最后一个米字如果跟随的是:"总", "合", "共", "合计", "总共", "共计",则去掉这些后边内容
     * @param input
     * @return
     */
    private static String removeSummary(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // 如果最后一个字符是符号（非字母数字且非空格），则去掉
        char lastChar = input.charAt(input.length() - 1);
        if (!Character.isLetterOrDigit(lastChar) && !Character.isWhitespace(lastChar)) {
            input = input.substring(0, input.length() - 1);
        }
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
     * 解析尾字符串，例如 "0尾各20米" 或 "5尾个30米"
     * @param input 输入字符串
     * @return 数字 -> 值的映射
     * @throws IllegalArgumentException 如果格式无法解析
     */
    private static Map<Integer, Double> parseEndNum(String input) {
        // 去除所有空格
        String trimmed = input.replaceAll("\\s+", "");

        // 正则表达式： 尾数部分（数字和分隔符） + "尾" + "各/个" + 数值 + 可选单位
        // 分隔符允许： 、 . ; ， 等，这里用字符类 [、.;，] 加上数字
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

    /**
     * 解析头字符串
     * @param input
     * @return
     */
    private static Map<Integer, Double> parseHeadNum(String input) {
        // 去除所有空格
        String trimmed = input.replaceAll("\\s+", "");

        // 正则表达式： 头部数字部分 + "头" + "各/个" + 数值 + 可选单位
        Pattern pattern = Pattern.compile("^(.*?)头[各个](\\d+(?:\\.\\d+)?)(?:[元斤米])?$");
        Matcher matcher = pattern.matcher(trimmed);
        if (!matcher.matches()) {
            System.out.println(TimeUtil.GetTime(true) + "\t解析失败: " + input);
            return null;
        }

        String headPart = matcher.group(1);   // "头"前面的部分
        double value = Double.parseDouble(matcher.group(2));

        // 从 headPart 中提取所有数字序列（十位数值）
        Pattern digitPattern = Pattern.compile("\\d+");
        Matcher digitMatcher = digitPattern.matcher(headPart);
        Set<Integer> heads = new HashSet<>(); // 使用Set去重
        while (digitMatcher.find()) {
            String numStr = digitMatcher.group();
            int head = Integer.parseInt(numStr); // 直接作为十位数值
            heads.add(head);
            // 如果数字有多位，可以给出提示（但头通常是0-4，不超过9）
            if (numStr.length() > 1 && head > 9) {
                System.out.println(TimeUtil.GetTime(true) + "\t提示: 提取到多位数字 " + numStr + "，其值 " + head + " 作为头（十位）");
            }
        }

        if (heads.isEmpty()) {
            System.out.println(TimeUtil.GetTime(true) + "\t错误: 未提取到有效头数");
            return null;
        }

        // 生成映射：1~49 中所有十位等于 head 的数字
        Map<Integer, Double> result = new HashMap<>();
        for (int head : heads) {
            for (int i = 1; i <= 49; i++) {
                if (i / 10 == head) {
                    result.put(i, value);
                }
            }
        }
        return result;
    }

    /**
     * 解析无'各'和'个'的类型
     * @param input
     * @return
     */
    private static Map<Integer, Double> parseSingle(String input) {
        Map<Integer, Double> map = new HashMap<>();
        if (input == null || input.isEmpty()) return map;
        // 正则：匹配整数x，然后一个或多个非数字，然后数字y（可能小数）
        Pattern pattern = Pattern.compile("(\\d+)\\D+(\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            double y = Double.parseDouble(matcher.group(2));
            map.put(x, y);
        }
        return map;
    }

    /**
     *解析通类
     * @param input
     * @return
     */
    private static Map<Integer, Double> parseCommon(String input) {
        Map<Integer, Double> result = new HashMap<>();
        if (input == null || input.isEmpty()) {
            return result;
        }
        // 查找“各”或“个”的位置
        int index = -1;
        String[] indicators = {"各", "个"};
        for (String ind : indicators) {
            index = input.indexOf(ind);
            if (index != -1) {
                break;
            }
        }
        if (index == -1) {
            return result; // 未找到指示符
        }

        String numbersPart = input.substring(0, index).trim();
        String valuePart = input.substring(index + 1).trim();

        // 正则添加可选负号，匹配 -50、50、-50.5 等形式
        Pattern valuePattern = Pattern.compile("^(-?\\d+(?:\\.\\d+)?)");
        Matcher valueMatcher = valuePattern.matcher(valuePart);
        if (!valueMatcher.find()) {
            return result;
        }
        double rawValue = Double.parseDouble(valueMatcher.group(1));

        // 取绝对值,因为此时'各'、'个'的后面可能还有'-'
        double y = Math.abs(rawValue);

        // 从numbersPart提取所有整数
        Pattern numPattern = Pattern.compile("\\d+");
        Matcher numMatcher = numPattern.matcher(numbersPart);
        while (numMatcher.find()) {
            int x = Integer.parseInt(numMatcher.group());
            result.put(x, y);
        }
        return result;
    }

    /**
     * 解析字符串中所有“生肖组合 + 各/个 + 数字”的模式，返回号码到数值的映射。
     * 例如输入 "鸡龙各20" 或 "牛狗蛇肖个30"，会将鸡、龙对应的号码赋值为20，牛、狗、蛇对应的号码赋值为30。
     * @param input 原始输入字符串
     * @return Map<Integer, Integer> 键为号码，值为数值
     */
    private static Map<Integer, Double> parseZodiacValue(String input) {
        Map<Integer, Double> result = new HashMap<>();
        if (input == null || input.isEmpty()) {
            return result;
        }
        // String text = trimToValidStart(input.replaceAll("，", ""));
        String text = trimToValidStart(input.replaceAll("\\p{P}", ""));
        if (text.isEmpty()) {
            return result;
        }

        int i = 0;
        while (i < text.length()) {
            // 跳过非生肖字符，找到第一个生肖
            while (i < text.length() && !ZODIAC_MAP.containsKey(String.valueOf(text.charAt(i)))) {
                i++;
            }
            if (i >= text.length()) break;

            // 收集连续的生肖
            StringBuilder zodiacs = new StringBuilder();
            while (i < text.length() && ZODIAC_MAP.containsKey(String.valueOf(text.charAt(i)))) {
                zodiacs.append(text.charAt(i));
                i++;
            }
            // 此时i指向第一个非生肖字符,从i开始向后查找,跳过无关字符,直到遇到“各”、“个”或新的生肖
            while (i < text.length()) {
                char ch = text.charAt(i);
                String s = String.valueOf(ch);
                if (ZODIAC_MAP.containsKey(s)) {
                    // 遇到新生肖，说明前一组没有数值，直接结束内层循环，让外层处理新生肖
                    break;
                } else if (ch == '各' || ch == '个') {
                    // 找到分隔符
                    i++; // 跳过分隔符
                    // 跳过可能的空格
                    while (i < text.length() && text.charAt(i) == ' ') i++;
                    // 提取数字（支持可选负号和小数点）
                    int numStart = i;
                    // 检查是否有负号
                    if (i < text.length() && text.charAt(i) == '-') {
                        i++;
                    }
                    // 提取数字和小数点
                    while (i < text.length() && (Character.isDigit(text.charAt(i)) || text.charAt(i) == '.')) {
                        i++;
                    }
                    if (numStart < i) {
                        try {
                            String numStr = text.substring(numStart, i);
                            double rawValue = Double.parseDouble(numStr);
                            double value = Math.abs(rawValue); // 取绝对值
                            // 为每个生肖对应的所有号码赋值
                            for (int j = 0; j < zodiacs.length(); j++) {
                                String zodiac = String.valueOf(zodiacs.charAt(j));
                                List<Integer> numbers = ZODIAC_MAP.get(zodiac);
                                if (numbers != null) {
                                    for (int num : numbers) {
                                        result.put(num, value);
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            // 数字格式错误，忽略该组
                        }
                    }
                    break; // 处理完当前组，退出内层循环
                } else {
                    // 无关字符（如“肖”、“米”等），跳过
                    i++;
                }
            }
        }
        return result;
    }

    /**
     * 判断字符串是否有'各'或'个'
     * @param str
     * @return
     */
    private static boolean containsGeOrGe(String str) {
        if (str == null) {
            return false;
        }
        return str.contains("个") || str.contains("各");
    }

    /**
     * 解析波板格式
     */
    private static Map<Integer, Double> parseColorNum(String clause) {
        // 正则匹配两种格式：
        // 1. (红|蓝|绿)(双|单)(各|个)(\d+)米
        // 2. (红|蓝|绿)(各|个)(\d+)米
        // 使用非捕获组 (?:各|个) 避免改变捕获组索引
        Pattern pattern = Pattern.compile("(红|蓝|绿)(双|单)?(?:各|个)(\\d+)米");
        Map<Integer, Double> result = new HashMap<>();
        clause = clause.trim();
        if (clause.isEmpty()) {
            return null;
        }
        Matcher matcher = pattern.matcher(clause);
        if (matcher.find()) {
            String waveName = matcher.group(1);
            String parity = matcher.group(2); // 可能为 null
            double value = Double.parseDouble(matcher.group(3));

            Set<Integer> numbers = WAVE_MAP.get(waveName);
            if (numbers != null) {
                if (parity == null) {
                    // 无单双，所有数字都加入
                    for (int num : numbers) {
                        result.put(num, value);
                    }
                } else {
                    // 有单双，按奇偶筛选
                    for (int num : numbers) {
                        boolean isEven = (num % 2 == 0);
                        if (("双".equals(parity) && isEven) || ("单".equals(parity) && !isEven)) {
                            result.put(num, value);
                        }
                    }
                }
            }
            return result;
        } else {
            return null;
        }
    }
}
