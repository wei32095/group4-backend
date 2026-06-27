package com.jycz.qingyun.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.jycz.qingyun.mapper")
public class MybatisConfig {
}