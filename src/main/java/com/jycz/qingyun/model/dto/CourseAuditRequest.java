package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseAuditRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;  // 1-通过，2-驳回

    private String auditRemark;
}