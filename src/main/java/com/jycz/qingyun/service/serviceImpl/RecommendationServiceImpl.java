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
        // 1. 查询薄弱知识点（单条记录）
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

        // 2. 获取知识点和错题
        String knowledgePoint = awp.getKnowledgePoint();

        List<String> wrongQuestions = new ArrayList<>();
        if (awp.getWrongQuestions() != null && !awp.getWrongQuestions().isEmpty()) {
            try {
                wrongQuestions = objectMapper.readValue(awp.getWrongQuestions(),
                        new TypeReference<List<String>>() {});
            } catch (Exception e) {
                log.error("解析错题列表失败: {}", e.getMessage());
            }
        }

        // 3. 查询是否已有未答的推荐
        LambdaQueryWrapper<Recommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recommendation::getWeakPointId, weakPointId)
                .eq(Recommendation::getUserId, userId)
                .eq(Recommendation::getIsCorrect, 0)
                .last("LIMIT 1");
        Recommendation existingRec = recommendationMapper.selectOne(wrapper);

        if (existingRec != null) {
            // 返回已有题目
            Map<String, Object> question;
            try {
                question = objectMapper.readValue(existingRec.getQuestion(), Map.class);
            } catch (Exception e) {
                throw new BusinessException(500, "解析题目失败");
            }
            return RecommendationQuestionVO.builder()
                    .recommendationId(existingRec.getId())
                    .weakPointId(weakPointId)
                    .knowledgePoint(knowledgePoint)
                    .question(filterQuestion(question))
                    .build();
        }

        // 4. 生成新题目
        return generateNewQuestion(awp, userId, knowledgePoint, wrongQuestions);
    }

    /**
     * 生成新推荐习题（私有方法）
     * 通过打乱错题顺序、添加前缀后缀等方式确保每次生成不同题目
     */
    private RecommendationQuestionVO generateNewQuestion(AssignmentWeakPoints awp, Long userId,
                                                         String knowledgePoint, List<String> wrongQuestions) {
        try {
            // 1. 使用当前时间戳作为随机种子
            long timestamp = System.currentTimeMillis();
            Random random = new Random(timestamp);

            // 2. 打乱错题顺序（确保每次不同）
            List<String> shuffledWrongQuestions = new ArrayList<>(wrongQuestions);
            Collections.shuffle(shuffledWrongQuestions, random);

            // 3. 添加随机前缀和后缀到知识点（确保每次生成不同题目）
            String[] prefixes = {"", "深入理解", "巩固练习", "变式训练", "进阶提升", "基础巩固", "专项突破"};
            String[] suffixes = {"", "核心要点", "常见考点", "易错点", "重点难点", "典型例题", "综合应用"};

            String enhancedKnowledgePoint = knowledgePoint;

            // 随机决定是否添加前缀
            if (random.nextBoolean()) {
                String prefix = prefixes[random.nextInt(prefixes.length)];
                if (!prefix.isEmpty()) {
                    enhancedKnowledgePoint = prefix + "·" + enhancedKnowledgePoint;
                }
            }

            // 随机决定是否添加后缀
            if (random.nextBoolean()) {
                String suffix = suffixes[random.nextInt(suffixes.length)];
                if (!suffix.isEmpty()) {
                    enhancedKnowledgePoint = enhancedKnowledgePoint + "·" + suffix;
                }
            }

            // 4. 添加练习次数到参数中（作为额外变化因素）
            int practiceCount = awp.getPracticeCount() != null ? awp.getPracticeCount() : 0;

            log.info("生成题目参数: 原始知识点={}, 增强后知识点={}, 练习次数={}, 错题数量={}",
                    knowledgePoint, enhancedKnowledgePoint, practiceCount, shuffledWrongQuestions.size());

            // 5. 调用 AI 生成题目（使用原有方法）
            Map<String, Object> question = aiService.generateSingleQuestion(
                    enhancedKnowledgePoint,
                    shuffledWrongQuestions
            );

            if (question == null) {
                throw new BusinessException(500, "生成推荐习题失败");
            }

            // 6. 在生成的题目中添加练习次数标识（可选，便于调试）
            String stem = (String) question.get("stem");
            if (stem != null && practiceCount > 0) {
                // 如果AI生成的题干没有包含练习信息，可以添加
                // 注意：这可能会影响题目质量，建议仅在调试时使用
                // question.put("stem", stem + " (练习" + (practiceCount + 1) + ")");
            }

            // 7. 保存推荐记录
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

            // 8. 更新练习次数
            awp.setPracticeCount(awp.getPracticeCount() + 1);
            assignmentWeakPointsMapper.updateById(awp);

            log.info("生成推荐习题成功: userId={}, weakPointId={}, practiceCount={}, recommendationId={}",
                    userId, awp.getId(), awp.getPracticeCount(), rec.getId());

            return RecommendationQuestionVO.builder()
                    .recommendationId(rec.getId())
                    .weakPointId(awp.getId())
                    .knowledgePoint(knowledgePoint)
                    .question(filterQuestion(question))
                    .build();

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

        // 2. 解析题目
        Map<String, Object> question;
        try {
            question = objectMapper.readValue(rec.getQuestion(), Map.class);
        } catch (Exception e) {
            throw new BusinessException(500, "解析题目失败");
        }
        String correctAnswer = (String) question.get("answer");

        // 3. 判断是否正确（去除首尾空格，统一比较）
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

            log.info("推荐习题答对: userId={}, weakPointId={}, +5分", userId, rec.getWeakPointId());

            return RecommendationSubmitVO.builder()
                    .recommendationId(rec.getId())
                    .isCorrect(true)
                    .pointsEarned(5)
                    .message("✅ 回答正确！+5分，该薄弱点已消除")
                    .newQuestion(null)
                    .build();

        } else {
            // ❌ 答错了：增加练习次数，生成新题目
            awp.setPracticeCount(awp.getPracticeCount() + 1);
            assignmentWeakPointsMapper.updateById(awp);

            log.info("推荐习题答错: userId={}, weakPointId={}, practiceCount={}",
                    userId, rec.getWeakPointId(), awp.getPracticeCount());

            // 获取知识点和错题
            String knowledgePoint = awp.getKnowledgePoint();
            List<String> wrongQuestions = new ArrayList<>();
            if (awp.getWrongQuestions() != null && !awp.getWrongQuestions().isEmpty()) {
                try {
                    wrongQuestions = objectMapper.readValue(awp.getWrongQuestions(),
                            new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    log.error("解析错题列表失败: {}", e.getMessage());
                }
            }

            // 生成新题目（传入不同的随机种子，让 AI 生成不同的题目）
            RecommendationQuestionVO newQuestion = generateNewQuestion(awp, userId, knowledgePoint, wrongQuestions);

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