package com.jycz.qingyun.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        // 1. 查询薄弱知识点
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

        String knowledgePoint = awp.getKnowledgePoint();
        List<String> wrongQuestions = parseWrongQuestions(awp.getWrongQuestions());

        // 2. 查询是否已有未答的推荐
        LambdaQueryWrapper<Recommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recommendation::getWeakPointId, weakPointId)
                .eq(Recommendation::getUserId, userId)
                .eq(Recommendation::getIsCorrect, 0)
                .last("LIMIT 1");
        Recommendation existingRec = recommendationMapper.selectOne(wrapper);

        if (existingRec != null) {
            Map<String, Object> question = parseQuestion(existingRec.getQuestion());
            return RecommendationQuestionVO.builder()
                    .recommendationId(existingRec.getId())
                    .weakPointId(weakPointId)
                    .knowledgePoint(knowledgePoint)
                    .question(filterQuestion(question))
                    .build();
        }

        // 3. 生成新题目
        return generateNewQuestion(awp, userId, knowledgePoint, wrongQuestions);
    }

    private RecommendationQuestionVO generateNewQuestion(AssignmentWeakPoints awp, Long userId,
                                                         String knowledgePoint, List<String> wrongQuestions) {
        try {
            int practiceCount = awp.getPracticeCount() != null ? awp.getPracticeCount() : 0;

            Map<String, Object> question = aiService.generateSingleQuestion(
                    knowledgePoint,
                    wrongQuestions,
                    practiceCount
            );

            if (question == null) {
                throw new BusinessException(500, "生成推荐习题失败");
            }

            String questionJson;
            try {
                questionJson = objectMapper.writeValueAsString(question);
            } catch (JsonProcessingException e) {
                log.error("序列化题目失败: {}", e.getMessage());
                throw new BusinessException(500, "序列化题目失败: " + e.getMessage());
            }

            Recommendation rec = new Recommendation();
            rec.setUserId(userId);
            rec.setAssignmentId(awp.getAssignmentId());
            rec.setWeakPointId(awp.getId());
            rec.setQuestion(questionJson);
            rec.setIsCorrect(0);
            recommendationMapper.insert(rec);

            awp.setPracticeCount(practiceCount + 1);
            assignmentWeakPointsMapper.updateById(awp);

            log.info("生成推荐习题成功: weakPointId={}, practiceCount={}", awp.getId(), practiceCount);

            return RecommendationQuestionVO.builder()
                    .recommendationId(rec.getId())
                    .weakPointId(awp.getId())
                    .knowledgePoint(knowledgePoint)
                    .question(filterQuestion(question))
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成推荐习题失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "生成推荐习题失败: " + e.getMessage());
        }
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
        if (rec.getIsCorrect() != 0) {
            throw new BusinessException(400, "该题目已作答");
        }

        // 2. 解析题目，获取正确答案
        Map<String, Object> question = parseQuestion(rec.getQuestion());
        String correctAnswer = (String) question.get("answer");

        // 3. 判断是否正确
        String studentAnswer = request.getAnswer();
        boolean isCorrect = false;
        if (correctAnswer != null && studentAnswer != null) {
            isCorrect = correctAnswer.trim().equals(studentAnswer.trim());
        }

        // 4. 更新推荐记录
        rec.setIsCorrect(isCorrect ? 1 : 2);
        recommendationMapper.updateById(rec);

        // 5. 查询薄弱知识点
        AssignmentWeakPoints awp = assignmentWeakPointsMapper.selectById(rec.getWeakPointId());
        if (awp == null) {
            throw new BusinessException(404, "薄弱知识点不存在");
        }

        if (isCorrect) {
            // ✅ 答对了：标记已完成
            awp.setStatus(1);
            assignmentWeakPointsMapper.updateById(awp);

            pointsRecordService.handleRecommendationPoints(userId);

            log.info("薄弱点消除成功: weakPointId={}, userId={}", rec.getWeakPointId(), userId);

            return RecommendationSubmitVO.builder()
                    .recommendationId(rec.getId())
                    .isCorrect(true)
                    .pointsEarned(5)
                    .message("✅ 回答正确！+5分，该薄弱点已消除")
                    .newQuestion(null)
                    .build();

        } else {
            // ❌ 答错了：增加练习次数，生成崭新的题目
            int practiceCount = awp.getPracticeCount() != null ? awp.getPracticeCount() : 0;
            awp.setPracticeCount(practiceCount + 1);
            assignmentWeakPointsMapper.updateById(awp);

            log.info("答错，生成新题目: weakPointId={}, practiceCount={}", rec.getWeakPointId(), practiceCount);

            List<String> wrongQuestions = parseWrongQuestions(awp.getWrongQuestions());

            // 生成崭新的题目（传入最新的练习次数）
            Map<String, Object> newQuestion = aiService.generateSingleQuestion(
                    awp.getKnowledgePoint(),
                    wrongQuestions,
                    practiceCount + 1
            );

            if (newQuestion == null) {
                throw new BusinessException(500, "生成新题目失败");
            }

            // 保存新的推荐记录（不是更新旧的，而是创建新的）
            String questionJson;
            try {
                questionJson = objectMapper.writeValueAsString(newQuestion);
            } catch (JsonProcessingException e) {
                log.error("序列化新题目失败: {}", e.getMessage());
                throw new BusinessException(500, "序列化新题目失败: " + e.getMessage());
            }

            Recommendation newRec = new Recommendation();
            newRec.setUserId(userId);
            newRec.setAssignmentId(awp.getAssignmentId());
            newRec.setWeakPointId(awp.getId());
            newRec.setQuestion(questionJson);
            newRec.setIsCorrect(0);
            recommendationMapper.insert(newRec);

            log.info("生成新推荐习题成功: weakPointId={}, newRecommendationId={}",
                    awp.getId(), newRec.getId());

            return RecommendationSubmitVO.builder()
                    .recommendationId(rec.getId())
                    .isCorrect(false)
                    .pointsEarned(0)
                    .message("❌ 回答错误，已为你生成新的练习题目！")
                    .newQuestion(filterQuestion(newQuestion))
                    .build();
        }
    }

    private List<String> parseWrongQuestions(String wrongQuestionsJson) {
        List<String> result = new ArrayList<>();
        if (wrongQuestionsJson != null && !wrongQuestionsJson.isEmpty()) {
            try {
                result = objectMapper.readValue(wrongQuestionsJson, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                log.error("解析错题列表失败: {}", e.getMessage());
            }
        }
        return result;
    }

    private Map<String, Object> parseQuestion(String questionJson) {
        try {
            return objectMapper.readValue(questionJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("解析题目失败: {}", e.getMessage());
            throw new BusinessException(500, "解析题目失败: " + e.getMessage());
        }
    }
}