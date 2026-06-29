package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AssignmentStudentGradeVO {
//
    private Long assignmentId;
    private String assignmentTitle;
    private Integer maxScore;
    private List<StudentGradeVO> list;

    @Data
    @Builder
    public static class StudentGradeVO {
        private Long studentId;
        private String studentName;
        private String status;   // PENDING / SUBMITTED / GRADED / OVERDUE
        private Integer score;
        private Integer objectScore;
        private Integer subjectScore;
        private Boolean isGraded;
    }
}