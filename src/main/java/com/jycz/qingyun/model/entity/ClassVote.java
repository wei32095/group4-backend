package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class_vote")
public class ClassVote {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long classId;

    private String heading;

    private String options;

    private String correctOption;

    private Integer duration;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime endedAt;
}