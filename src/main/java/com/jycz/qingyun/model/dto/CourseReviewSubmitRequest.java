package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseReviewSubmitRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "星级不能为空")
    @Min(value = 1, message = "星级最小为1")
    @Max(value = 5, message = "星级最大为5")
    private Integer star;

    private String reviewContent;//
}