package com.erge.util;

public class Logger {
    public static void i(Object obj) {
        System.out.println(obj);
    }

    public static void i(String format, Object... args) {
        System.out.println(String.format(format, args));
    }
}