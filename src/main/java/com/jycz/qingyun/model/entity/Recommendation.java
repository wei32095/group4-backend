package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("recommendation")
public class Recommendation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long assignmentId;

    private String questions;  // JSON 格式存储

    private Integer status;    // 0-待练习，1-已完成

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}