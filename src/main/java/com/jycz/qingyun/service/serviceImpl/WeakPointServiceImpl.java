package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.entity.Assignment;
import com.jycz.qingyun.model.entity.AssignmentWeakPoints;
import com.jycz.qingyun.model.vo.WeakPointVO;
import com.jycz.qingyun.mapper.AssignmentMapper;
import com.jycz.qingyun.mapper.AssignmentWeakPointsMapper;
import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.service.WeakPointService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        // 1. 查询该学生的薄弱点（下推 assignmentId 过滤到 SQL）
        LambdaQueryWrapper<AssignmentWeakPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentWeakPoints::getUserId, userId)
                .eq(AssignmentWeakPoints::getStatus, 0);
        if (assignmentId != null) {
            wrapper.eq(AssignmentWeakPoints::getAssignmentId, assignmentId);
        }

        List<AssignmentWeakPoints> list = assignmentWeakPointsMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 如果有 courseId，需要筛选作业属于该课程的
        if (courseId != null) {
            List<Long> assignmentIdsInList = list.stream()
                    .map(AssignmentWeakPoints::getAssignmentId)
                    .distinct()
                    .collect(Collectors.toList());

            List<Assignment> assignments = assignmentMapper.selectBatchIds(assignmentIdsInList);
            Set<Long> validAssignmentIds = assignments.stream()
                    .filter(a -> a.getCourseId().equals(courseId))
                    .map(Assignment::getId)
                    .collect(Collectors.toSet());

            if (validAssignmentIds.isEmpty()) {
                return new ArrayList<>();
            }

            list.removeIf(awp -> !validAssignmentIds.contains(awp.getAssignmentId()));
        }

        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 查询作业信息
        List<Long> assignmentIds = list.stream()
                .map(AssignmentWeakPoints::getAssignmentId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Assignment> assignmentMap = assignmentMapper.selectBatchIds(assignmentIds).stream()
                .collect(Collectors.toMap(Assignment::getId, a -> a));

        // 4. 组装结果
        List<WeakPointVO> result = new ArrayList<>();

        for (AssignmentWeakPoints awp : list) {
            Assignment assignment = assignmentMap.get(awp.getAssignmentId());
            String assignmentTitle = assignment != null ? assignment.getAssignmentTitle() : "未知作业";

            // 解析 wrong_questions
            List<String> wrongQuestions = new ArrayList<>();
            if (awp.getWrongQuestions() != null && !awp.getWrongQuestions().isEmpty()) {
                try {
                    wrongQuestions = objectMapper.readValue(awp.getWrongQuestions(),
                            new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    log.error("解析错题列表失败: {}", e.getMessage());
                }
            }

            String status = awp.getStatus() == 1 ? "completed" : "pending";

            result.add(WeakPointVO.builder()
                    .weakPointId(awp.getId())
                    .assignmentId(awp.getAssignmentId())
                    .assignmentTitle(assignmentTitle)
                    .knowledgePoint(awp.getKnowledgePoint())
                    .explanation(awp.getExplanation())
                    .wrongCount(awp.getWrongCount())
                    .practiceCount(awp.getPracticeCount())
                    .status(status)
                    .wrongQuestions(wrongQuestions)
                    .build());
        }

        return result;
    }
}