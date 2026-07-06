package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AssignmentSubmitVO {

    private Long assignmentId;
    private String status;
    private LocalDateTime submitTime;
    private Integer autoScore;
    private Integer maxScore;
    private Boolean subjectivePending;

    // ========== 新增 ==========
    private List<WeakPointVO> weakPoints;  // 薄弱知识点列表

    @Data
    @Builder
    public static class WeakPointVO {
        private String knowledgePoint;   // 知识点名称
        private String explanation;      // 知识点讲解
        private Integer wrongCount;      // 错题数量
        private List<String> wrongQuestions; // 错题题干列表
    }
}