package com.jycz.qingyun.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.entity.Assignment;
import com.jycz.qingyun.model.entity.AssignmentWeakPoints;
import com.jycz.qingyun.model.entity.ObjectSubmit;
import com.jycz.qingyun.model.entity.Question;
import com.jycz.qingyun.mapper.AssignmentMapper;
import com.jycz.qingyun.mapper.AssignmentWeakPointsMapper;
import com.jycz.qingyun.mapper.ObjectSubmitMapper;
import com.jycz.qingyun.mapper.QuestionMapper;
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
    private final NoticeService noticeService;

    /**
     * 异步生成薄弱知识点分析
     */
    @Async
    public void generateWeakPointsAsync(Assignment assignment, Long studentId) {
        try {
            log.info("开始异步分析薄弱知识点: studentId={}, assignmentId={}, 线程: {}",
                    studentId, assignment.getId(), Thread.currentThread().getName());

            // 1. 获取提交记录
            List<ObjectSubmit> submits = objectSubmitMapper.selectByAssignmentAndUser(assignment.getId(), studentId);
            List<Question> allQuestions = questionMapper.selectList(
                    new LambdaQueryWrapper<Question>()
                            .eq(Question::getAssignmentId, assignment.getId())
            );

            if (submits.isEmpty() || allQuestions.isEmpty()) {
                log.info("学生 {} 作业 {} 无提交记录或题目", studentId, assignment.getId());
                return;
            }

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

            // 4. ✅ 每个薄弱点单独插入一条记录
            for (Map<String, Object> wp : weakPointMaps) {
                String knowledgePoint = (String) wp.get("knowledgePoint");
                String explanation = (String) wp.get("explanation");
                Integer wrongCount = (Integer) wp.getOrDefault("wrongCount", 0);
                @SuppressWarnings("unchecked")
                List<String> wrongQuestionsList = (List<String>) wp.getOrDefault("wrongQuestions", new ArrayList<>());

                // 检查是否已存在
                LambdaQueryWrapper<AssignmentWeakPoints> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(AssignmentWeakPoints::getAssignmentId, assignment.getId())
                        .eq(AssignmentWeakPoints::getUserId, studentId)
                        .eq(AssignmentWeakPoints::getKnowledgePoint, knowledgePoint);
                AssignmentWeakPoints existing = assignmentWeakPointsMapper.selectOne(wrapper);

                String wrongQuestionsJson = safeWriteValueAsString(wrongQuestionsList);
                if (wrongQuestionsJson == null) {
                    log.warn("序列化错题列表失败，跳过该知识点: {}", knowledgePoint);
                    continue;
                }

                if (existing != null) {
                    existing.setExplanation(explanation);
                    existing.setWrongCount(wrongCount);
                    existing.setWrongQuestions(wrongQuestionsJson);
                    existing.setStatus(0);
                    existing.setPracticeCount(0);
                    assignmentWeakPointsMapper.updateById(existing);
                    log.info("更新薄弱知识点: studentId={}, assignmentId={}, knowledgePoint={}",
                            studentId, assignment.getId(), knowledgePoint);
                } else {
                    AssignmentWeakPoints awp = new AssignmentWeakPoints();
                    awp.setAssignmentId(assignment.getId());
                    awp.setUserId(studentId);
                    awp.setKnowledgePoint(knowledgePoint);
                    awp.setExplanation(explanation);
                    awp.setWrongCount(wrongCount);
                    awp.setWrongQuestions(wrongQuestionsJson);
                    awp.setStatus(0);
                    awp.setPracticeCount(0);
                    assignmentWeakPointsMapper.insert(awp);
                    log.info("新增薄弱知识点: studentId={}, assignmentId={}, knowledgePoint={}",
                            studentId, assignment.getId(), knowledgePoint);
                }
            }

            log.info("薄弱知识点分析完成: studentId={}, assignmentId={}, count={}",
                    studentId, assignment.getId(), weakPointMaps.size());

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