package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("assignment")
public class Assignment {
//
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String assignmentTitle;

    private LocalDateTime deadline;

    private Integer maxScore;


    private LocalDateTime assignmentCreateTime;

    private LocalDateTime updatedAt;

    private String weakPoints;
}