package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseCreateRequest {

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称最多100个字符")
    private String courseTitle;

    @Size(max = 500, message = "课程描述最多500个字符")
    private String description;

    @Size(max = 500, message = "封面URL最多500个字符")
    private String cover;
}