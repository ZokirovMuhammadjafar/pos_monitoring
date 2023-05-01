package com.pos.monitoring.utils;

public class ServerUtils {
    public static String tracer(StackTraceElement[] traceElements) {
        StringBuilder stringBuilder = new StringBuilder("\n");
        for (StackTraceElement traceElement : traceElements) {
            stringBuilder.append(traceElement.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
