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

    private String weakPoints;

    private Integer status;        // 0-待练习，1-已完成

    private Integer practiceCount; // 练习次数


}