package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class")
public class Class {//

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Long userId;

    private String classTitle;

    private String status;

    private LocalDateTime createTime;
    private LocalDateTime endTime;// ← 新增字段
}