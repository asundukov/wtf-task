package com.senseisoft.exeniumbot;

import static com.senseisoft.exeniumbot.Application.log;
import java.util.UUID;

public class Log {

    public static void warn(Object o, String msg) {
        log.warn(classString(o) + msg);
    }

    public static void info(Object o, String msg) {
        log.info(classString(o) + msg);
    }

    public static void err(Object o, String msg) {
        log.error(classString(o) + msg);
    }

    public static String exception(Exception e) {
        String ex_id = UUID.randomUUID().toString();
        StringBuilder str = new StringBuilder();
        str.append(ex_id + " " + e.toString() + "\n");
        for (StackTraceElement el : e.getStackTrace()) {
            str.append("  ").append(el).append("\n");
        }
        log.error(str.toString());
        return "exception: " + ex_id;
    }

    private static String classString(Object o) {
        if (o != null) {
            if (o.getClass().getSimpleName().equals("String")) {
                return "[" + o.toString() + "] ";
            } else {
                return "[" + o.getClass().getSimpleName() + "] ";
            }
        } else {
            return "[null] ";
        }
    }
}
