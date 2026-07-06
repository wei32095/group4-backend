package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AssignmentDetailVO {

    private Long assignmentId;
    private Long courseId;
    private String assignmentTitle;
    private LocalDateTime deadline;
    private Integer maxScore;
    private String status;
    private Integer autoScore;
    private Integer subjectiveScore;
    private Integer totalScore;
    private List<QuestionDetailVO> questions;

    // ========== 新增 ==========
    private List<WeakPointVO> weakPoints;

    @Data
    @Builder
    public static class WeakPointVO {
        private String knowledgePoint;
        private String explanation;
        private Integer wrongCount;
        private List<String> wrongQuestions;
    }

    @Data
    @Builder
    public static class QuestionDetailVO {
        private Long questionId;
        private Integer type;
        private String stem;
        private List<String> options;
        private Integer perscore;
        private Integer sortOrder;
        private String myAnswer;
        private Boolean isCorrect;
        private Integer score;
        private String explanation;
        private String teacherComment;
        private String imageUrl;
        private String knowledgePoint;  // ← 新增：题目对应的知识点
    }
}