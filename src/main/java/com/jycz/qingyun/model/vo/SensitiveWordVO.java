package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SensitiveWordVO {

    private Long id;
    private String word;
    private LocalDateTime createdAt;
}