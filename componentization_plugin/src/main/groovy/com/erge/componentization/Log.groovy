package com.erge.componentization;

import org.gradle.api.logging.Logger;

/**
 * 日志类
 */
class Log {
    static final String TAG = "com.erge.component: "
    private static Logger log

    static void i(Object value) {
        log.info "$TAG$value"
    }

    static void e(Object value) {
        log.error "$TAG$value"
    }

    static void setLogger(Logger logger) {
        log = logger
    }
}