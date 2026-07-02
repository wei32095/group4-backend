package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_resource")
public class CourseResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Long userId;

    private String fileName;

    private String fileUrl;

    private Long fileSize;

    private String description;

    private Integer downloadCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}