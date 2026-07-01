package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseResourceUploadRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    private String description;
}