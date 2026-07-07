package com.jycz.qingyun.service;

import com.jycz.qingyun.model.entity.Assignment;

public interface AsyncAnalysisService {

    /**
     * 异步生成薄弱知识点
     */
    void generateWeakPointsAsync(Assignment assignment, Long studentId);
}