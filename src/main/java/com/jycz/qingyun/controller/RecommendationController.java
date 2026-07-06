package com.jycz.qingyun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.entity.Assignment;
import com.jycz.qingyun.model.entity.Course;
import com.jycz.qingyun.model.entity.Recommendation;
import com.jycz.qingyun.model.vo.RecommendationListVO;
import com.jycz.qingyun.mapper.AssignmentMapper;
import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.mapper.RecommendationMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/qingyun/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationMapper recommendationMapper;
    private final AssignmentMapper assignmentMapper;
    private final CourseMapper courseMapper;
    private final ObjectMapper objectMapper;

    /**
     * 获取推荐习题列表（按作业分组）
     * GET /qingyun/recommendation/list
     */
    @GetMapping("/list")
    public ApiResult<List<RecommendationListVO>> getRecommendations(
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        log.info("查询推荐习题: userId={}", userId);

        // 1. 查询该学生的所有推荐记录（status = 0 待练习）
        LambdaQueryWrapper<Recommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recommendation::getUserId, userId)
                .eq(Recommendation::getStatus, 0)
                .orderByDesc(Recommendation::getCreatedAt);

        List<Recommendation> recommendations = recommendationMapper.selectList(wrapper);

        if (recommendations.isEmpty()) {
            return ApiResult.success(new ArrayList<>());
        }

        // 2. 查询作业和课程信息
        List<Long> assignmentIds = recommendations.stream()
                .map(Recommendation::getAssignmentId)
                .distinct()
                .collect(Collectors.toList());

        List<Assignment> assignments = assignmentMapper.selectBatchIds(assignmentIds);
        Map<Long, Assignment> assignmentMap = assignments.stream()
                .collect(Collectors.toMap(Assignment::getId, a -> a));

        List<Long> courseIds = assignments.stream()
                .map(Assignment::getCourseId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Course> courseMap = courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        // 3. 组装响应
        List<RecommendationListVO> result = new ArrayList<>();

        for (Recommendation rec : recommendations) {
            Assignment assignment = assignmentMap.get(rec.getAssignmentId());
            if (assignment == null) continue;

            Course course = courseMap.get(assignment.getCourseId());

            List<Map<String, Object>> questions;
            try {
                questions = objectMapper.readValue(rec.getQuestions(), List.class);
            } catch (Exception e) {
                log.error("解析推荐习题失败: {}", e.getMessage());
                continue;
            }

            String status = rec.getStatus() == 0 ? "pending" : "completed";

            result.add(RecommendationListVO.builder()
                    .assignmentId(rec.getAssignmentId())
                    .assignmentTitle(assignment.getAssignmentTitle())
                    .courseId(assignment.getCourseId())
                    .courseName(course != null ? course.getCourseTitle() : "未知课程")
                    .createdAt(rec.getCreatedAt())
                    .status(status)
                    .questions(questions)
                    .build());
        }

        return ApiResult.success(result);
    }

    /**
     * 标记推荐习题已完成
     * PUT /qingyun/recommendation/complete
     */
    @PutMapping("/complete")
    public ApiResult<Void> completeRecommendation(
            @RequestParam Long recommendationId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");

        Recommendation rec = recommendationMapper.selectById(recommendationId);
        if (rec == null) {
            return ApiResult.error(404, "推荐记录不存在");
        }

        if (!rec.getUserId().equals(userId)) {
            return ApiResult.error(403, "无权操作");
        }

        if (rec.getStatus() == 1) {
            return ApiResult.success("已完成", null);
        }

        rec.setStatus(1);
        recommendationMapper.updateById(rec);

        return ApiResult.success("练习完成", null);
    }
}