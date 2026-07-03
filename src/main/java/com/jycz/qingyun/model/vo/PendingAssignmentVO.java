package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PendingAssignmentVO {

    private Long assignmentId;
    private String assignmentTitle;
    private Long courseId;
    private String courseName;
    private LocalDateTime deadline;
    private Integer maxScore;
    private LocalDateTime submitTime;
    private List<SubjectiveQuestionVO> subjectiveQuestions;

    @Data
    @Builder
    public static class SubjectiveQuestionVO {

        private Integer sortOrder;
        private String stem;
        private Integer perscore;
        private String answerPicture;
        private Integer gradingStatus;  // 1-待批改，2-已批改
    }
}