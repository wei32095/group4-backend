package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassStudentVO {

    private Long id;
    private String classTitle;
    private String status;
    private Integer checkinStatus;
    private LocalDateTime createTime;  // ← 新增字段
}