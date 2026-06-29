package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseJoinRequest {

    @NotBlank(message = "课程码不能为空")
    private String courseCode;
}