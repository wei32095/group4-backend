package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseEndRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;
}