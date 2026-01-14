package com.cloudstudio.matrix.matrixcommonweb.webtool;

/**
 * @Class StringUtil
 * @Author Create By Matrix·张
 * @Date 2024/11/26 下午2:59
 * 字符串处理工具
 */
public class StringUtil {
    /**
     * 判断空字符串
     * @param str
     * @return
     */
    public static boolean isEmptyOrNull(String str){
        return str==null||str.isEmpty();
    }

    public static String getUrlPath(String str){
        return str.replace("\\", "/");
    }

    /**
     * 判断字符串是否为正整数（不包含0和负数）
     * @param str 要检查的字符串
     * @return 如果是正整数返回true，否则返回false
     */
    public static boolean isPositiveInteger(String str) {
        if (isEmptyOrNull(str)) {
            return false;
        }
        // 必须是非0开头的正整数
        return str.matches("^[1-9]\\d*$");
    }
}
