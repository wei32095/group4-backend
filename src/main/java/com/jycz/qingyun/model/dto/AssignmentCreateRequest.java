package com.jycz.qingyun.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentCreateRequest {
//
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotBlank(message = "作业标题不能为空")
    private String assignmentTitle;


    @NotNull(message = "截止时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Shanghai")  // ← 新增
    private LocalDateTime deadline;

    @NotNull(message = "满分分值不能为空")
    private Integer maxScore;

    @NotNull(message = "题目列表不能为空")
    private List<QuestionRequest> questions;

    @Data
    public static class QuestionRequest {
        @NotNull(message = "题型不能为空")
        private Integer type;

        @NotBlank(message = "题干不能为空")
        private String stem;

        private List<String> options;

        private String answer;

        private String explanation;

        @NotNull(message = "分值不能为空")
        private Integer perscore;

        private Integer sortOrder;
    }
}