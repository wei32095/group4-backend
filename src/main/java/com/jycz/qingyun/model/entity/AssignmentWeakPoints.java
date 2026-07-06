package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("assignment_weak_points")
public class AssignmentWeakPoints {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assignmentId;

    private Long userId;

    private String weakPoints;  // JSON

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}