package com.jycz.qingyun.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class CourseCodeGenerator {
    private static final AtomicInteger counter = new AtomicInteger(1);

    public static String generateCode() {
        // 日期前缀：MATH20240627
        String datePrefix = "MATH" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 序号：001, 002, 003...
        String seq = String.format("%03d", counter.getAndIncrement());
        return datePrefix + seq;
    }
}