package com.group91.tiw23.utils;

/**
 * @author Mario Merlo
 */
public class NullChecker {
    public static boolean voidCheck(String parameter) {
        return parameter != null && !parameter.isEmpty();
    }
}
