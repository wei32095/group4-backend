package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassCreateVO {

    private Long id;
    private Long courseId;
    private Long userId;
    private String classTitle;
    private String fileUrl;
    private String status;
    private LocalDateTime createTime;  // ← 新增字段
}