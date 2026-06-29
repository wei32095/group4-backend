package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class_check")
public class ClassCheck {//

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long classId;

    private Long userId;

    private Integer checkStatus;

    private LocalDateTime checkinTime;
}