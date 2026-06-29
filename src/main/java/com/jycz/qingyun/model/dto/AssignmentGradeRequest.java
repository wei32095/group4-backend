package com.jycz.qingyun.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignmentGradeRequest {
//
    @NotNull(message = "作业ID不能为空")
    private Long assignmentId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "批改列表不能为空")
    private List<GradeRequest> grades;

    @Data
    public static class GradeRequest {
        @NotNull(message = "题目ID不能为空")
        private Long questionId;

        @NotNull(message = "得分不能为空")
        private Integer score;

        private String teacherComment;
    }
}