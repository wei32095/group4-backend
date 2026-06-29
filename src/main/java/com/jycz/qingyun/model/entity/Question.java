package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("question")
public class Question {
//
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assignmentId;

    private Integer type;

    private String stem;

    private String answer;

    private String explanation;

    private Integer perscore;

    private Integer sortOrder;
}