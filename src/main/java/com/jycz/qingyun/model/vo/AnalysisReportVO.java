package com.jycz.qingyun.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnalysisReportVO {
    private Integer totalStudyDuration;
    private BigDecimal assignmentCorrectRate;
    private Integer weekStudyDuration;
}
