package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.entity.Assignment;
import com.jycz.qingyun.model.entity.AssignmentWeakPoints;
import com.jycz.qingyun.model.entity.Course;
import com.jycz.qingyun.model.vo.WeakPointVO;
import com.jycz.qingyun.mapper.AssignmentMapper;
import com.jycz.qingyun.mapper.AssignmentWeakPointsMapper;
import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.service.WeakPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeakPointServiceImpl implements WeakPointService {

    private final AssignmentWeakPointsMapper assignmentWeakPointsMapper;
    private final AssignmentMapper assignmentMapper;
    private final CourseMapper courseMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<WeakPointVO> getWeakPointsList(Long userId, Long courseId, Long assignmentId) {
        // 1. 查询该学生的所有薄弱点
        LambdaQueryWrapper<AssignmentWeakPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentWeakPoints::getUserId, userId)
                .eq(AssignmentWeakPoints::getStatus, 0);


        List<AssignmentWeakPoints> list = assignmentWeakPointsMapper.selectList(wrapper);

        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 筛选：如果有 assignmentId，直接过滤
        List<AssignmentWeakPoints> filteredList = list;
        if (assignmentId != null) {
            filteredList = list.stream()
                    .filter(awp -> awp.getAssignmentId().equals(assignmentId))
                    .collect(Collectors.toList());
        }

        if (filteredList.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 如果有 courseId，需要筛选作业属于该课程的
        if (courseId != null) {
            List<Long> assignmentIds = filteredList.stream()
                    .map(AssignmentWeakPoints::getAssignmentId)
                    .distinct()
                    .collect(Collectors.toList());

            if (assignmentIds.isEmpty()) {
                return new ArrayList<>();
            }

            // 查询这些作业属于哪个课程
            List<Assignment> assignments = assignmentMapper.selectBatchIds(assignmentIds);
            List<Long> validAssignmentIds = assignments.stream()
                    .filter(a -> a.getCourseId().equals(courseId))
                    .map(Assignment::getId)
                    .collect(Collectors.toList());

            if (validAssignmentIds.isEmpty()) {
                return new ArrayList<>();
            }

            filteredList = filteredList.stream()
                    .filter(awp -> validAssignmentIds.contains(awp.getAssignmentId()))
                    .collect(Collectors.toList());
        }

        if (filteredList.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 查询作业信息
        List<Long> assignmentIds = filteredList.stream()
                .map(AssignmentWeakPoints::getAssignmentId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Assignment> assignmentMap = assignmentMapper.selectBatchIds(assignmentIds).stream()
                .collect(Collectors.toMap(Assignment::getId, a -> a));

        // 5. 组装结果
        List<WeakPointVO> result = new ArrayList<>();

        for (AssignmentWeakPoints awp : filteredList) {
            Assignment assignment = assignmentMap.get(awp.getAssignmentId());
            String assignmentTitle = assignment != null ? assignment.getAssignmentTitle() : "未知作业";

            List<Map<String, Object>> weakPoints;
            try {
                weakPoints = objectMapper.readValue(awp.getWeakPoints(), new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception e) {
                log.error("解析薄弱知识点失败: {}", e.getMessage());
                continue;
            }

            for (Map<String, Object> wp : weakPoints) {
                String status = awp.getStatus() == 1 ? "completed" : "pending";
                result.add(WeakPointVO.builder()
                        .weakPointId(awp.getId())
                        .assignmentId(awp.getAssignmentId())
                        .assignmentTitle(assignmentTitle)
                        .knowledgePoint((String) wp.get("knowledgePoint"))
                        .explanation((String) wp.get("explanation"))
                        .wrongCount((Integer) wp.getOrDefault("wrongCount", 0))
                        .practiceCount(awp.getPracticeCount())
                        .status(status)
                        .wrongQuestions((List<String>) wp.getOrDefault("wrongQuestions", new ArrayList<>()))
                        .build());
            }
        }

        return result;
    }
}