package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycz.qingyun.model.entity.StudentAnalysis;
import com.jycz.qingyun.mapper.StudentAnalysisMapper;
import com.jycz.qingyun.service.StudentAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentAnalysisServiceImpl implements StudentAnalysisService {

    private final StudentAnalysisMapper studentAnalysisMapper;

    @Override
    public void addStudyDuration(Long userId, int seconds) {
        if (seconds <= 0) {
            return;
        }

        StudentAnalysis analysis = getByUserId(userId);

        if (analysis == null) {
            analysis = new StudentAnalysis();
            analysis.setUserId(userId);
            analysis.setTotalStudyDuration(seconds);
            analysis.setWeekStudyDuration(seconds);
            analysis.setUpdatedAt(LocalDateTime.now());
            studentAnalysisMapper.insert(analysis);
        } else {
            analysis.setTotalStudyDuration(analysis.getTotalStudyDuration() + seconds);
            analysis.setWeekStudyDuration(analysis.getWeekStudyDuration() + seconds);
            analysis.setUpdatedAt(LocalDateTime.now());
            studentAnalysisMapper.updateById(analysis);
        }

        log.info("学情更新: userId={}, 增加时长={}秒", userId, seconds);
    }

    @Override
    public StudentAnalysis getByUserId(Long userId) {
        LambdaQueryWrapper<StudentAnalysis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentAnalysis::getUserId, userId);
        return studentAnalysisMapper.selectOne(wrapper);
    }

    @Override
    public void update(StudentAnalysis analysis) {
        if (analysis != null) {
            analysis.setUpdatedAt(LocalDateTime.now());
            studentAnalysisMapper.updateById(analysis);
        }
    }
}