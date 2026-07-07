package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WeakPointVO {

    private Long weakPointId;
    private Long assignmentId;
    private String assignmentTitle;
    private String knowledgePoint;
    private String explanation;
    private Integer wrongCount;
    private Integer practiceCount;
    private String status;  // "pending" / "completed"
    private List<String> wrongQuestions;
}