package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassStudentVO {//

    private Long id;
    private String classTitle;
    private String status;
    private String checkinStatus;
    private LocalDateTime createTime;
    private LocalDateTime endTime;// ← 新增字段
}