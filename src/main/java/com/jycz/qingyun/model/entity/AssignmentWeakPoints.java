package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("assignment_weak_points")
public class AssignmentWeakPoints {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assignmentId;

    private Long userId;

    // ========== 新字段（替换 weakPoints） ==========
    private String knowledgePoint;   // 知识点名称

    private String explanation;      // 知识点讲解

    private Integer wrongCount;      // 错题数量

    private String wrongQuestions;   // 错题题干列表（JSON）

    // ========== 原有字段 ==========
    private Integer status;          // 0-待练习，1-已完成

    private Integer practiceCount;   // 练习次数

}