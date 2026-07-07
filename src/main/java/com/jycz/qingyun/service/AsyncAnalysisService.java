package com.jycz.qingyun.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.entity.Assignment;
import com.jycz.qingyun.model.entity.AssignmentWeakPoints;
import com.jycz.qingyun.model.entity.ObjectSubmit;
import com.jycz.qingyun.model.entity.Question;
import com.jycz.qingyun.model.entity.Recommendation;
import com.jycz.qingyun.mapper.AssignmentMapper;
import com.jycz.qingyun.mapper.AssignmentWeakPointsMapper;
import com.jycz.qingyun.mapper.ObjectSubmitMapper;
import com.jycz.qingyun.mapper.QuestionMapper;
import com.jycz.qingyun.mapper.RecommendationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAnalysisService {

    private final AIService aiService;
    private final ObjectMapper objectMapper;
    private final AssignmentMapper assignmentMapper;
    private final AssignmentWeakPointsMapper assignmentWeakPointsMapper;
    private final ObjectSubmitMapper objectSubmitMapper;
    private final QuestionMapper questionMapper;
    private final RecommendationMapper recommendationMapper;
    private final NoticeService noticeService;

    @Async
    public void generateWeakPointsAsync(Assignment assignment, Long studentId) {
        try {
            log.info("开始异步分析薄弱知识点: studentId={}, assignmentId={}",
                    studentId, assignment.getId());

            // 1. 获取提交记录
            List<ObjectSubmit> submits = objectSubmitMapper.selectByAssignmentAndUser(assignment.getId(), studentId);
            List<Question> allQuestions = questionMapper.selectList(
                    new LambdaQueryWrapper<Question>()
                            .eq(Question::getAssignmentId, assignment.getId())
            );

            // 2. 区分错题和正确题
            List<Question> wrongQuestions = new ArrayList<>();
            List<Question> correctQuestions = new ArrayList<>();
            Map<Long, ObjectSubmit> submitMap = submits.stream()
                    .collect(Collectors.toMap(ObjectSubmit::getQuestionId, s -> s));

            for (Question q : allQuestions) {
                if (q.getType() == 5) continue;
                ObjectSubmit submit = submitMap.get(q.getId());
                if (submit != null) {
                    if (q.getAnswer() != null && !q.getAnswer().equals(submit.getAnswerWord())) {
                        wrongQuestions.add(q);
                    } else {
                        correctQuestions.add(q);
                    }
                }
            }

            if (wrongQuestions.isEmpty()) {
                log.info("学生 {} 作业 {} 全部正确，无需分析薄弱点", studentId, assignment.getId());
                return;
            }

            // 3. 调用 AI 分析薄弱知识点
            List<Map<String, Object>> weakPointMaps = aiService.analyzeWeakPoints(wrongQuestions, correctQuestions);

            if (weakPointMaps == null || weakPointMaps.isEmpty()) {
                log.warn("AI 分析薄弱知识点失败");
                return;
            }

            // 4. 保存到 assignment_weak_points 表
            String weakPointsJson = safeWriteValueAsString(weakPointMaps);
            if (weakPointsJson == null) {
                return;
            }

            LambdaQueryWrapper<AssignmentWeakPoints> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AssignmentWeakPoints::getAssignmentId, assignment.getId())
                    .eq(AssignmentWeakPoints::getUserId, studentId);
            AssignmentWeakPoints existing = assignmentWeakPointsMapper.selectOne(wrapper);

            if (existing != null) {
                existing.setWeakPoints(weakPointsJson);
                existing.setStatus(0);
                existing.setPracticeCount(0);
                assignmentWeakPointsMapper.updateById(existing);
                log.info("更新薄弱知识点: studentId={}, assignmentId={}", studentId, assignment.getId());
            } else {
                AssignmentWeakPoints awp = new AssignmentWeakPoints();
                awp.setAssignmentId(assignment.getId());
                awp.setUserId(studentId);
                awp.setWeakPoints(weakPointsJson);
                awp.setStatus(0);
                awp.setPracticeCount(0);
                assignmentWeakPointsMapper.insert(awp);
                log.info("新增薄弱知识点: studentId={}, assignmentId={}", studentId, assignment.getId());
            }

            log.info("薄弱知识点分析完成: studentId={}, assignmentId={}",
                    studentId, assignment.getId());

            // 5. 发送通知
            noticeService.addNotice(
                    studentId,
                    "📊 薄弱知识点分析完成",
                    "系统已分析完你的作业错题，快去查看薄弱知识点吧！",
                    14
            );

        } catch (Exception e) {
            log.error("异步生成薄弱知识点失败: studentId={}, assignmentId={}, error={}",
                    studentId, assignment.getId(), e.getMessage(), e);
        }
    }



    /**
     * 降级方案：从错题中随机选几道作为推荐
     */
    private List<Map<String, Object>> generateFallbackRecommendations(List<Question> wrongQuestions, int count) {
        List<Map<String, Object>> fallback = new ArrayList<>();
        int size = Math.min(count, wrongQuestions.size());
        Collections.shuffle(wrongQuestions);
        for (int i = 0; i < size; i++) {
            Question q = wrongQuestions.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("stem", q.getStem());
            item.put("type", q.getType());
            item.put("options", new ArrayList<>());
            item.put("answer", q.getAnswer());
            item.put("explanation", q.getExplanation() != null ? q.getExplanation() : "请复习相关知识点");
            item.put("knowledgePoint", "需要巩固的知识点");
            fallback.add(item);
        }
        return fallback;
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