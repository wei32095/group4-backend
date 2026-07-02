package com.jycz.qingyun.service;

import com.jycz.qingyun.model.vo.StudentAnalysisVO;

public interface AnalysisService {

    StudentAnalysisVO getStudentAnalysis(Long userId, String periodType);

}