package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.entity.Assignment;
import com.jycz.qingyun.model.entity.AssignmentWeakPoints;
import com.jycz.qingyun.model.entity.ObjectSubmit;
import com.jycz.qingyun.model.entity.Question;
import com.jycz.qingyun.mapper.AssignmentWeakPointsMapper;
import com.jycz.qingyun.mapper.ObjectSubmitMapper;
import com.jycz.qingyun.mapper.QuestionMapper;
import com.jycz.qingyun.service.AIService;
import com.jycz.qingyun.service.AsyncAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAnalysisServiceImpl implements AsyncAnalysisService {

    private final ObjectSubmitMapper objectSubmitMapper;
    private final QuestionMapper questionMapper;
    private final AssignmentWeakPointsMapper assignmentWeakPointsMapper;
    private final ObjectMapper objectMapper;
    private final AIService aiService;

    @Override
    @Async
    @Transactional
    public void generateWeakPointsAsync(Assignment assignment, Long studentId) {
        try {
            log.info("开始异步生成薄弱知识点: assignmentId={}, studentId={}",
                    assignment.getId(), studentId);

            // 1. 查询该学生的所有客观题提交记录
            LambdaQueryWrapper<ObjectSubmit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ObjectSubmit::getAssignmentId, assignment.getId())
                    .eq(ObjectSubmit::getUserId, studentId);
            List<ObjectSubmit> submits = objectSubmitMapper.selectList(wrapper);

            if (submits.isEmpty()) {
                log.info("学生没有提交客观题，跳过薄弱点分析");
                return;
            }

            // 2. 获取所有题目信息
            List<Long> questionIds = submits.stream()
                    .map(ObjectSubmit::getQuestionId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
            qWrapper.in(Question::getId, questionIds);
            List<Question> questions = questionMapper.selectList(qWrapper);

            Map<Long, Question> questionMap = questions.stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            // 3. 收集错题
            List<Map<String, Object>> wrongQuestions = new ArrayList<>();

            for (ObjectSubmit submit : submits) {
                Question question = questionMap.get(submit.getQuestionId());
                if (question == null) continue;

                String correctAnswer = question.getAnswer();
                String studentAnswer = submit.getAnswerWord();

                boolean isWrong = true;
                if (correctAnswer != null && studentAnswer != null) {
                    isWrong = !correctAnswer.trim().equals(studentAnswer.trim());
                }

                if (isWrong) {
                    Map<String, Object> wrongQ = new HashMap<>();
                    wrongQ.put("stem", question.getStem());
                    wrongQ.put("correctAnswer", correctAnswer);
                    wrongQ.put("studentAnswer", studentAnswer);
                    wrongQ.put("type", question.getType());
                    wrongQ.put("sortOrder", question.getSortOrder());
                    wrongQuestions.add(wrongQ);
                }
            }

            if (wrongQuestions.isEmpty()) {
                log.info("学生没有错题，不生成薄弱点");
                return;
            }

            log.info("发现 {} 道错题", wrongQuestions.size());

            // 4. 调用 AI 分析
            Map<String, Object> analysisResult = null;
            try {
                analysisResult = aiService.analyzeWeakPoints(wrongQuestions, assignment.getAssignmentTitle());
            } catch (Exception e) {
                log.warn("AI 分析失败: {}", e.getMessage());
            }

            // 5. 如果 AI 返回空，用本地规则生成
            if (analysisResult == null || analysisResult.isEmpty()) {
                analysisResult = buildFallbackWeakPoints(wrongQuestions);
            }

            // 6. 保存
            saveWeakPoints(assignment.getId(), studentId, analysisResult);

        } catch (Exception e) {
            log.error("生成薄弱知识点失败: {}", e.getMessage(), e);
        }
    }

    private Map<String, Object> buildFallbackWeakPoints(List<Map<String, Object>> wrongQuestions) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> weakPoints = new ArrayList<>();

        Map<String, Object> wp = new HashMap<>();
        wp.put("knowledgePoint", "综合错题巩固");
        wp.put("explanation", "以下题目存在错误，建议复习相关知识点，加强练习。共答错 " + wrongQuestions.size() + " 道题。");
        wp.put("wrongCount", wrongQuestions.size());

        List<String> wrongStems = new ArrayList<>();
        for (Map<String, Object> q : wrongQuestions) {
            wrongStems.add((String) q.get("stem"));
        }
        wp.put("wrongQuestions", wrongStems);

        weakPoints.add(wp);
        result.put("weakPoints", weakPoints);

        return result;
    }

    @SuppressWarnings("unchecked")
    private void saveWeakPoints(Long assignmentId, Long studentId, Map<String, Object> analysisResult) {
        try {
            List<Map<String, Object>> weakPointList = (List<Map<String, Object>>) analysisResult.get("weakPoints");

            if (weakPointList == null || weakPointList.isEmpty()) {
                log.warn("薄弱知识点列表为空");
                return;
            }

            for (Map<String, Object> wp : weakPointList) {
                String knowledgePoint = (String) wp.get("knowledgePoint");
                String explanation = (String) wp.get("explanation");
                Integer wrongCount = (Integer) wp.getOrDefault("wrongCount", 1);
                List<String> wrongQuestions = (List<String>) wp.get("wrongQuestions");

                if (knowledgePoint == null || knowledgePoint.trim().isEmpty()) {
                    log.warn("知识点名称为空，跳过");
                    continue;
                }

                LambdaQueryWrapper<AssignmentWeakPoints> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(AssignmentWeakPoints::getAssignmentId, assignmentId)
                        .eq(AssignmentWeakPoints::getUserId, studentId)
                        .eq(AssignmentWeakPoints::getKnowledgePoint, knowledgePoint);

                AssignmentWeakPoints existing = assignmentWeakPointsMapper.selectOne(wrapper);

                if (existing != null) {
                    // 更新已有记录
                    existing.setWrongCount(existing.getWrongCount() + wrongCount);
                    List<String> existingQuestions = new ArrayList<>();
                    if (existing.getWrongQuestions() != null && !existing.getWrongQuestions().isEmpty()) {
                        try {
                            existingQuestions = objectMapper.readValue(
                                    existing.getWrongQuestions(),
                                    new TypeReference<List<String>>() {}
                            );
                        } catch (Exception e) {
                            log.error("解析已有错题列表失败: {}", e.getMessage());
                        }
                    }
                    existingQuestions.addAll(wrongQuestions);
                    try {
                        existing.setWrongQuestions(objectMapper.writeValueAsString(existingQuestions));
                    } catch (JsonProcessingException e) {
                        log.error("序列化错题列表失败: {}", e.getMessage());
                        existing.setWrongQuestions("[]");
                    }
                    existing.setExplanation(explanation);
                    assignmentWeakPointsMapper.updateById(existing);
                } else {
                    // 创建新记录
                    AssignmentWeakPoints awp = new AssignmentWeakPoints();
                    awp.setAssignmentId(assignmentId);
                    awp.setUserId(studentId);
                    awp.setKnowledgePoint(knowledgePoint);
                    awp.setExplanation(explanation);
                    awp.setWrongCount(wrongCount);
                    try {
                        awp.setWrongQuestions(objectMapper.writeValueAsString(wrongQuestions));
                    } catch (JsonProcessingException e) {
                        log.error("序列化错题列表失败: {}", e.getMessage());
                        awp.setWrongQuestions("[]");
                    }
                    awp.setStatus(0);
                    awp.setPracticeCount(0);
                    assignmentWeakPointsMapper.insert(awp);
                }
            }

            log.info("保存薄弱知识点成功: assignmentId={}, studentId={}, 数量={}",
                    assignmentId, studentId, weakPointList.size());

        } catch (Exception e) {
            log.error("保存薄弱知识点失败: {}", e.getMessage(), e);
        }
    }
}