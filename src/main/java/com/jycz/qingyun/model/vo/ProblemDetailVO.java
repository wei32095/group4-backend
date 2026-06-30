package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProblemDetailVO {

    private Long problemId;
    private Long courseId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer userRole;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private List<ReplyVO> replies;

    @Data
    @Builder
    public static class ReplyVO {
        private Long replyId;
        private Long userId;
        private String userName;
        private String userAvatar;
        private Integer userRole;
        private String content;
        private Boolean isTeacher;
        private LocalDateTime createdAt;
    }
}