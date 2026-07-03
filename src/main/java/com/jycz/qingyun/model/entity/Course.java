package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String courseTitle;

    private String description;

    private String cover;

    private Integer studentCount;

    private String courseCode;

    private String status;

    private Integer auditStatus;

    private String auditRemark;

    private LocalDateTime auditTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}