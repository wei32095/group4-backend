package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseAuditVO {

    private Long courseId;
    private String courseTitle;
    private Integer auditStatus;
    private String auditRemark;
    private LocalDateTime auditTime;
}