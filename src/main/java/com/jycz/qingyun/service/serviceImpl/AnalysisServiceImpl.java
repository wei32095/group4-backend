package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.mapper.StudentAnalysisMapper;
import com.jycz.qingyun.model.entity.StudentAnalysis;
import com.jycz.qingyun.model.vo.AnalysisReportVO;
import com.jycz.qingyun.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private StudentAnalysisMapper studentAnalysisMapper;

    @Override
    public AnalysisReportVO getReport(Long userId) {
        LambdaQueryWrapper<StudentAnalysis> wrapper = new LambdaQueryWrapper<StudentAnalysis>()
                .eq(StudentAnalysis::getUserId, userId);
        StudentAnalysis analysis = studentAnalysisMapper.selectOne(wrapper);

        AnalysisReportVO vo = new AnalysisReportVO();
        if (analysis == null) {
            vo.setTotalStudyDuration(0);
            vo.setWeekStudyDuration(0);
            vo.setAssignmentCorrectRate(BigDecimal.ZERO);
        } else {
            vo.setTotalStudyDuration(analysis.getTotalStudyDuration());
            vo.setWeekStudyDuration(analysis.getWeekStudyDuration());
            vo.setAssignmentCorrectRate(analysis.getAssignmentCorrectRate() != null
                    ? analysis.getAssignmentCorrectRate() : BigDecimal.ZERO);
        }
        return vo;
    }

    @Override
    @Transactional
    public void addStudyDuration(Long userId, int durationSeconds) {
        LambdaQueryWrapper<StudentAnalysis> wrapper = new LambdaQueryWrapper<StudentAnalysis>()
                .eq(StudentAnalysis::getUserId, userId);
        StudentAnalysis analysis = studentAnalysisMapper.selectOne(wrapper);

        if (analysis == null) {
            analysis = new StudentAnalysis();
            analysis.setUserId(userId);
            analysis.setTotalStudyDuration(durationSeconds);
            analysis.setWeekStudyDuration(durationSeconds);
            analysis.setAssignmentCorrectRate(BigDecimal.ZERO);
            analysis.setUpdatedAt(LocalDateTime.now());
            studentAnalysisMapper.insert(analysis);
        } else {
            // 判断上次更新是否在本周之前，是则重置周时长
            LocalDateTime thisMonday = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
            if (analysis.getUpdatedAt() == null || analysis.getUpdatedAt().isBefore(thisMonday)) {
                analysis.setWeekStudyDuration(0);
            }
            analysis.setTotalStudyDuration(analysis.getTotalStudyDuration() + durationSeconds);
            analysis.setWeekStudyDuration(analysis.getWeekStudyDuration() + durationSeconds);
            analysis.setUpdatedAt(LocalDateTime.now());
            studentAnalysisMapper.updateById(analysis);
        }
    }
}
