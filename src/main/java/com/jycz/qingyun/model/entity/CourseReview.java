package com.jycz.qingyun.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_review")
public class CourseReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Long userId;

    private Integer star;

    private String reviewContent;

    private Integer likecount;

    private LocalDateTime reviewCreateTime;
}