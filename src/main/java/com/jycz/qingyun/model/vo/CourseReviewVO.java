package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CourseReviewVO {
//
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer star;
    private String reviewContent;
    private Integer likecount;
    private LocalDateTime reviewCreateTime;
}