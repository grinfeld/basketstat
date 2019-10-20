package com.mikerusoft.euroleague.utils;

public class Utils {
    private Utils() {}

    public static <T> void assertNotNull(T object, String message) {
        if (object == null)
            throw new NullPointerException(message);
    }

    public static <T> void assertNotNull(T object) {
        assertNotNull(object, null);
    }

    public static void assertNotEmptyTrimmed(String str, String message) {
        assertNotNull(str, message);
        if (str.trim().isEmpty())
            throw new NullPointerException(message);
    }

    public static void assertNotEmptyTrimmed(String str) {
        assertNotEmptyTrimmed(str, null);
    }
}
