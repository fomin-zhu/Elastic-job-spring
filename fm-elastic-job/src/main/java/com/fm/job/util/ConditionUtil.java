package com.fm.job.util;

/**
 * @author fomin
 * @date 2019-11-02
 */
public final class ConditionUtil {

    /**
     * 判断参数是否为null
     *
     * @param reference    obj
     * @param errorMessage 抛出错误信息
     * @throws NullPointerException if the obj is null
     */
    public static <T> void assertNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
    }

    /**
     * 判断参数是否为null或者空
     *
     * @param checking string
     * @param field    抛出错误信息
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void assertNotNullAndEmpty(String checking, String field) {
        if (checking == null || "".equals(checking)) {
            throw new IllegalArgumentException(field + " is null or empty.");
        }
    }

    /**
     * 判断参数是否true
     *
     * @param expression  true
     * @param errorMessage 抛出错误信息
     * @throws IllegalArgumentException if {@code expression} is true
     */
    public static void assertTrue(boolean expression, Object errorMessage) {
        if (expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }
}
