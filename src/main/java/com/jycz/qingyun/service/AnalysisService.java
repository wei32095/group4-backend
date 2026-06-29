package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.AnalysisReportVO;

public interface AnalysisService {
    AnalysisReportVO getReport(Long userId);
    void addStudyDuration(Long userId, int durationSeconds);
}
