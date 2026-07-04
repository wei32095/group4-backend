package com.jycz.qingyun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync  // ← 添加
public class QingyunApplication {
    public static void main(String[] args) {
        SpringApplication.run(QingyunApplication.class, args);
    }
}