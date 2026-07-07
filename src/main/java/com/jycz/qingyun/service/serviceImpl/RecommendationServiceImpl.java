package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.dto.RecommendationSubmitRequest;
import com.jycz.qingyun.model.entity.Assignment;
import com.jycz.qingyun.model.entity.Course;
import com.jycz.qingyun.model.entity.Recommendation;
import com.jycz.qingyun.model.vo.RecommendationListVO;
import com.jycz.qingyun.model.vo.RecommendationSubmitVO;
import com.jycz.qingyun.mapper.AssignmentMapper;
import com.jycz.qingyun.mapper.CourseMapper;
import com.jycz.qingyun.mapper.RecommendationMapper;
import com.jycz.qingyun.service.AIService;
import com.jycz.qingyun.service.NoticeService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.service.RecommendationService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationMapper recommendationMapper;
    private final AssignmentMapper assignmentMapper;
    private final CourseMapper courseMapper;
    private final ObjectMapper objectMapper;
    private final AIService aiService;
    private final PointsRecordService pointsRecordService;
    private final NoticeService noticeService;

    @Override
    public List<RecommendationListVO> getRecommendationList(Long userId) {
        LambdaQueryWrapper<Recommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recommendation::getUserId, userId)
                .eq(Recommendation::getStatus, 0);


        List<Recommendation> recommendations = recommendationMapper.selectList(wrapper);

        if (recommendations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> assignmentIds = recommendations.stream()
                .map(Recommendation::getAssignmentId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Assignment> assignmentMap = assignmentMapper.selectBatchIds(assignmentIds).stream()
                .collect(Collectors.toMap(Assignment::getId, a -> a));

        List<Long> courseIds = assignmentMap.values().stream()
                .map(Assignment::getCourseId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Course> courseMap = courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        List<RecommendationListVO> result = new ArrayList<>();

        for (Recommendation rec : recommendations) {
            Assignment assignment = assignmentMap.get(rec.getAssignmentId());
            if (assignment == null) continue;

            Course course = courseMap.get(assignment.getCourseId());

            List<Map<String, Object>> questions;
            try {
                questions = objectMapper.readValue(rec.getQuestions(), List.class);
                for (int i = 0; i < questions.size(); i++) {
                    Map<String, Object> q = questions.get(i);
                    // 如果没有 sortOrder 或 sortOrder 为 null，手动设置
                    if (!q.containsKey("sortOrder") || q.get("sortOrder") == null) {
                        q.put("sortOrder", i + 1);
                    }
                }
            } catch (Exception e) {
                log.error("解析推荐习题失败: {}", e.getMessage());
                continue;
            }

            String status = rec.getStatus() == 0 ? "pending" : "completed";
            Boolean isCompleted = rec.getIsCompleted() != null && rec.getIsCompleted() == 1;

            result.add(RecommendationListVO.builder()
                    .recommendationId(rec.getId())
                    .assignmentId(rec.getAssignmentId())
                    .assignmentTitle(assignment.getAssignmentTitle())
                    .courseId(assignment.getCourseId())
                    .courseName(course != null ? course.getCourseTitle() : "未知课程")
                    .status(status)
                    .isCompleted(isCompleted)
                    .questions(questions)
                    .build());
        }

        return result;
    }

    @Override
    @Transactional
    public RecommendationSubmitVO submitRecommendation(RecommendationSubmitRequest request, Long userId) {
        // 1. 查询推荐记录
        Recommendation rec = recommendationMapper.selectById(request.getRecommendationId());
        if (rec == null) {
            throw new BusinessException(404, "推荐记录不存在");
        }

        if (!rec.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作");
        }

        if (rec.getStatus() == 1) {
            throw new BusinessException(400, "该推荐习题已完成");
        }

        // 2. 解析题目
        List<Map<String, Object>> questions;
        try {
            questions = objectMapper.readValue(rec.getQuestions(), List.class);
        } catch (Exception e) {
            throw new BusinessException(500, "题目解析失败");
        }

        // 3. 创建序号 -> 正确答案映射
        Map<Integer, String> answerMap = new HashMap<>();
        for (Map<String, Object> q : questions) {
            Integer sortOrder = (Integer) q.get("sortOrder");
            String answer = (String) q.get("answer");
            if (sortOrder != null && answer != null) {
                answerMap.put(sortOrder, answer);
            }
        }

        // 4. 批改
        int correctCount = 0;
        int totalCount = request.getAnswers().size();
        int totalScore = 0;
        int maxScore = 0;
        List<Map<String, Object>> wrongQuestions = new ArrayList<>();

        for (RecommendationSubmitRequest.AnswerRequest answerReq : request.getAnswers()) {
            String correctAnswer = answerMap.get(answerReq.getSortOrder());
            if (correctAnswer != null) {
                maxScore += 10;
                if (correctAnswer.equals(answerReq.getAnswer())) {
                    correctCount++;
                    totalScore += 10;
                } else {
                    // 记录错题
                    for (Map<String, Object> q : questions) {
                        Integer sortOrder = (Integer) q.get("sortOrder");
                        if (sortOrder != null && sortOrder.equals(answerReq.getSortOrder())) {
                            wrongQuestions.add(q);
                            break;
                        }
                    }
                }
            } else {
                // ✅ 找不到对应题目，把当前题目加入错题列表
                log.warn("未找到题目序号 {} 的正确答案", answerReq.getSortOrder());
                for (Map<String, Object> q : questions) {
                    Integer sortOrder = (Integer) q.get("sortOrder");
                    if (sortOrder != null && sortOrder.equals(answerReq.getSortOrder())) {
                        wrongQuestions.add(q);
                        break;
                    }
                }
            }
        }

        boolean allCorrect = correctCount == totalCount;
        // 5. 处理结果
        List<Map<String, Object>> newRecommendations = new ArrayList<>();
        Integer pointsEarned = 0;

        if (allCorrect) {
            // ✅ 全部正确 → +5分
            pointsRecordService.handleRecommendationPoints(userId);
            pointsEarned = 5;

            rec.setIsCompleted(1);
            rec.setStatus(1);
            recommendationMapper.updateById(rec);

            log.info("推荐习题全部正确: userId={}, recId={}, +5分", userId, rec.getId());

        } else {
            // ❌ 有错误 → 针对错题生成新推荐
            if (!wrongQuestions.isEmpty()) {
                newRecommendations = aiService.generateRecommendationFromWrongQuestions(
                        wrongQuestions, Math.min(3, wrongQuestions.size() * 2)
                );

                if (!newRecommendations.isEmpty()) {
                    // 序列化新推荐
                    String newQuestionsJson = safeWriteValueAsString(newRecommendations);
                    if (newQuestionsJson == null) {
                        throw new BusinessException(500, "序列化推荐习题失败");
                    }

                    // 保存新推荐
                    Recommendation newRec = new Recommendation();
                    newRec.setUserId(userId);
                    newRec.setAssignmentId(rec.getAssignmentId());
                    newRec.setQuestions(newQuestionsJson);
                    newRec.setStatus(0);
                    newRec.setParentId(rec.getId());
                    newRec.setIsCompleted(0);
                    recommendationMapper.insert(newRec);

                    noticeService.addNotice(
                            userId,
                            "📚 新的推荐习题已生成",
                            "你还有 " + wrongQuestions.size() + " 道题目需要巩固，继续练习吧！",
                            13
                    );

                    log.info("基于错题生成新推荐: userId={}, parentId={}", userId, rec.getId());
                }
            }

            // 当前推荐标记为已完成（但不是全部正确）
            rec.setStatus(1);
            rec.setIsCompleted(0);
            recommendationMapper.updateById(rec);
        }

        return RecommendationSubmitVO.builder()
                .recommendationId(request.getRecommendationId())
                .status("COMPLETED")
                .submitTime(LocalDateTime.now())
                .score(totalScore)
                .maxScore(maxScore)
                .correctCount(correctCount)
                .totalCount(totalCount)
                .allCorrect(allCorrect)
                .pointsEarned(pointsEarned)
                .newRecommendations(newRecommendations)
                .build();
    }

    /**
     * 安全序列化 JSON
     */
    private String safeWriteValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("JSON 序列化失败: {}", e.getMessage());
            return null;
        }
    }
}