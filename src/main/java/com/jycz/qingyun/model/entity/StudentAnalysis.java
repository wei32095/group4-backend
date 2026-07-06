package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("student_analysis")
public class StudentAnalysis {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer totalStudyDuration;

    private BigDecimal assignmentCorrectRate;

    private Integer weekStudyDuration;

    private LocalDateTime updatedAt;
}