package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.model.dto.RecommendationSubmitRequest;
import com.jycz.qingyun.model.entity.AssignmentWeakPoints;
import com.jycz.qingyun.model.entity.Recommendation;
import com.jycz.qingyun.model.vo.RecommendationQuestionVO;
import com.jycz.qingyun.model.vo.RecommendationSubmitVO;
import com.jycz.qingyun.mapper.AssignmentWeakPointsMapper;
import com.jycz.qingyun.mapper.RecommendationMapper;
import com.jycz.qingyun.service.AIService;
import com.jycz.qingyun.service.PointsRecordService;
import com.jycz.qingyun.service.RecommendationService;
import com.jycz.qingyun.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationMapper recommendationMapper;
    private final AssignmentWeakPointsMapper assignmentWeakPointsMapper;
    private final ObjectMapper objectMapper;
    private final AIService aiService;
    private final PointsRecordService pointsRecordService;

    /**
     * 过滤题目中的冗余字段，按 type, stem, options, answer, explanation 顺序排列
     */
    private Map<String, Object> filterQuestion(Map<String, Object> question) {
        Map<String, Object> filtered = new LinkedHashMap<>();
        filtered.put("type", question.get("type"));
        filtered.put("stem", question.get("stem"));
        filtered.put("options", question.get("options"));
        filtered.put("answer", question.get("answer"));
        filtered.put("explanation", question.get("explanation"));
        return filtered;
    }

    @Override
    @Transactional
    public RecommendationQuestionVO getRecommendationQuestion(Long weakPointId, Long userId) {
        AssignmentWeakPoints awp = assignmentWeakPointsMapper.selectById(weakPointId);
        if (awp == null) {
            throw new BusinessException(404, "薄弱知识点不存在");
        }
        if (!awp.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作");
        }
        if (awp.getStatus() == 1) {
            throw new BusinessException(400, "该薄弱点已完成");
        }

        LambdaQueryWrapper<Recommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recommendation::getWeakPointId, weakPointId)
                .eq(Recommendation::getUserId, userId)
                .eq(Recommendation::getIsCorrect, 0)
                .last("LIMIT 1");
        Recommendation existingRec = recommendationMapper.selectOne(wrapper);

        if (existingRec != null) {
            Map<String, Object> question;
            try {
                question = objectMapper.readValue(existingRec.getQuestion(), Map.class);
            } catch (Exception e) {
                throw new BusinessException(500, "解析题目失败");
            }
            return RecommendationQuestionVO.builder()
                    .recommendationId(existingRec.getId())
                    .weakPointId(weakPointId)
                    .knowledgePoint((String) question.get("knowledgePoint"))
                    .question(filterQuestion(question))
                    .build();
        }

        return generateNewQuestion(awp, userId);
    }

    /**
     * 生成新推荐习题（私有方法）
     */
    private RecommendationQuestionVO generateNewQuestion(AssignmentWeakPoints awp, Long userId) {
        try {
            List<Map<String, Object>> weakPoints = objectMapper.readValue(
                    awp.getWeakPoints(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            if (weakPoints.isEmpty()) {
                throw new BusinessException(500, "薄弱知识点为空");
            }

            Map<String, Object> wp = weakPoints.get(0);
            String knowledgePoint = (String) wp.get("knowledgePoint");
            List<String> wrongQuestions = (List<String>) wp.getOrDefault("wrongQuestions", new ArrayList<>());

            Map<String, Object> question = aiService.generateSingleQuestion(knowledgePoint, wrongQuestions);
            if (question == null) {
                throw new BusinessException(500, "生成推荐习题失败");
            }

            String questionJson;
            try {
                questionJson = objectMapper.writeValueAsString(question);
            } catch (Exception e) {
                log.error("序列化推荐习题失败: {}", e.getMessage());
                throw new BusinessException(500, "序列化推荐习题失败");
            }

            Recommendation rec = new Recommendation();
            rec.setUserId(userId);
            rec.setAssignmentId(awp.getAssignmentId());
            rec.setWeakPointId(awp.getId());
            rec.setQuestion(questionJson);
            rec.setIsCorrect(0);
            recommendationMapper.insert(rec);

            awp.setPracticeCount(awp.getPracticeCount() + 1);
            assignmentWeakPointsMapper.updateById(awp);

            return RecommendationQuestionVO.builder()
                    .recommendationId(rec.getId())
                    .weakPointId(awp.getId())
                    .knowledgePoint(knowledgePoint)
                    .question(filterQuestion(question))
                    .build();

        } catch (Exception e) {
            log.error("生成推荐习题失败: {}", e.getMessage());
            throw new BusinessException(500, "生成推荐习题失败");
        }
    }

    @Override
    @Transactional
    public RecommendationSubmitVO submitRecommendation(RecommendationSubmitRequest request, Long userId) {
        Recommendation rec = recommendationMapper.selectById(request.getRecommendationId());
        if (rec == null) {
            throw new BusinessException(404, "推荐记录不存在");
        }
        if (!rec.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作");
        }
        if (rec.getIsCorrect() != 0) {
            throw new BusinessException(400, "该题目已作答");
        }

        Map<String, Object> question;
        try {
            question = objectMapper.readValue(rec.getQuestion(), Map.class);
        } catch (Exception e) {
            throw new BusinessException(500, "解析题目失败");
        }
        String correctAnswer = (String) question.get("answer");

        boolean isCorrect = correctAnswer != null && correctAnswer.equals(request.getAnswer());

        rec.setIsCorrect(isCorrect ? 1 : 2);
        recommendationMapper.updateById(rec);

        AssignmentWeakPoints awp = assignmentWeakPointsMapper.selectById(rec.getWeakPointId());

        if (isCorrect) {
            awp.setStatus(1);
            assignmentWeakPointsMapper.updateById(awp);
            pointsRecordService.handleRecommendationPoints(userId);

            return RecommendationSubmitVO.builder()
                    .recommendationId(rec.getId())
                    .isCorrect(true)
                    .pointsEarned(5)
                    .message("✅ 回答正确！+5分，该薄弱点已消除")
                    .newQuestion(null)
                    .build();
        } else {
            awp.setPracticeCount(awp.getPracticeCount() + 1);
            assignmentWeakPointsMapper.updateById(awp);

            RecommendationQuestionVO newQuestion = generateNewQuestion(awp, userId);

            return RecommendationSubmitVO.builder()
                    .recommendationId(rec.getId())
                    .isCorrect(false)
                    .pointsEarned(0)
                    .message("❌ 回答错误，再练一题！")
                    .newQuestion(newQuestion.getQuestion())
                    .build();
        }
    }
}