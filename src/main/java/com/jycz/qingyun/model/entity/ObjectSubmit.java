package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("object_submit")
public class ObjectSubmit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assignmentId;

    private Long questionId;

    private Long userId;

    private Integer objectScore;

    private String answerWord;

    private LocalDateTime submitTime;
}