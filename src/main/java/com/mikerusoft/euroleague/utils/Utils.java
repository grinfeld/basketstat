package com.mikerusoft.euroleague.utils;

import java.util.function.Function;

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

    public static String toStringWithDeNull(Integer val) {
        return val == null ? null : String.valueOf(val);
    }

    public static Integer parseIntWithDeNull(String val) {
        return val == null ? null : Integer.parseInt(val);
    }

    public static <T> String deNull(T obj, Function<T, String> func) {
        return obj == null ? null : func.apply(obj);
    }

    public static String deNullTemp(String str) {
        return str;
    }

    public static <T> T rethrowRuntime(Throwable t) {
        if (t instanceof Error)
            throw (Error)t;
        else if (t instanceof RuntimeException)
            throw (RuntimeException)t;
        else
            throw new RuntimeException(t);
    }
}
