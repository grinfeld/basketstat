package com.mikerusoft.euroleague.utils;

import java.sql.Date;
import java.util.Calendar;
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

    public static Integer parseIntWithEmptyToNull(String val) {
        return isEmptyTrimmed(val) ? null : Integer.parseInt(val);
    }

    public static <T> String deNull(T obj, Function<T, String> func) {
        return obj == null ? null : func.apply(obj);
    }

    public static <T, R> R deNullObject(T obj, Function<T, R> func) {
        return obj == null ? null : func.apply(obj);
    }

    public static String deNull(String str) {
        return str == null ? "" : str;
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

    public static boolean isEmptyTrimmed(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String extractSeason(Date now) {
        Calendar calendarForNextYear = Calendar.getInstance();
        calendarForNextYear.setTime(now);
        int month = calendarForNextYear.get(Calendar.MONTH);
        int nextYearBuilder = calendarForNextYear.get(Calendar.YEAR);
        nextYearBuilder = month > 1 && month <= 6 ? nextYearBuilder - 1 : nextYearBuilder;
        calendarForNextYear.set(Calendar.YEAR, nextYearBuilder + 1);
        return nextYearBuilder + String.valueOf(calendarForNextYear.get(Calendar.YEAR));
    }
}
