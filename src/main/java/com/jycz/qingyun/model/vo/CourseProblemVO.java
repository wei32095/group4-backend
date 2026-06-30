package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseProblemVO {

    private Long problemId;
    private Long courseId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer userRole;
    private String title;
    private String content;
    private Integer replyCount;
    private LocalDateTime createdAt;
}