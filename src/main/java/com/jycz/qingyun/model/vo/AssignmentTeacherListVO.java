package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AssignmentTeacherListVO {

    private Long assignmentId;
    private String assignmentTitle;
    private LocalDateTime deadline;
    private Integer maxScore;
    private Integer submitCount;
    private Integer totalStudents;
    private Double submissionRate;
    private Double avgScore;
    private LocalDateTime createdAt;

    // ========== 新增 ==========
    private List<QuestionStatVO> questionStats;

    @Data
    @Builder
    public static class QuestionStatVO {
        private Integer sortOrder;
        private Double correctRate;  // 正确率（百分比）
        private Integer totalCount;   // 提交人数
        private Integer correctCount; // 正确人数
    }
}