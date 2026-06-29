package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class_chat")
public class ClassChat {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long classId;

    private Long userId;

    private Integer messageType;

    private String content;

    private LocalDateTime sentTime;
}