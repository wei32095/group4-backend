package com.jycz.qingyun.service;

import com.jycz.qingyun.model.entity.StudentAnalysis;

public interface StudentAnalysisService {

    /**
     * 添加学习时长（自习室结束时调用）
     */
    void addStudyDuration(Long userId, int seconds);

    /**
     * 获取学生学情数据
     */
    StudentAnalysis getByUserId(Long userId);

    /**
     * 更新学情数据
     */
    void update(StudentAnalysis analysis);
}