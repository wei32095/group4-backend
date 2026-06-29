package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_student")
public class CourseStudent {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Long userId;

    private LocalDateTime joinedAt;//
}