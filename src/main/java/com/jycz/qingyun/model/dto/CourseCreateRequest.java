package com.jycz.qingyun.model.dto;

import lombok.Data;

@Data
public class CourseCreateRequest {
    private String name;
    private String description;
    private String coverImage;
}