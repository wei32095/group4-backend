package com.jycz.qingyun.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model:deepseek-chat}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private static final Long AI_USER_ID = 0L;

    // ========== 1. 生成 AI 回复（问题回复） ==========
    public String generateReply(String title, String content, List<Map<String, String>> existingReplies) {
        try {
            String prompt = buildReplyPrompt(title, content, existingReplies);
            String response = callAI(prompt);
            return response.trim();
        } catch (Exception e) {
            log.error("AI 回复失败: {}", e.getMessage());
            return "抱歉，AI 暂时无法回答，请稍后再试或等待老师回复。";
        }
    }

    private String buildReplyPrompt(String title, String content, List<Map<String, String>> existingReplies) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位资深学科教师，正在回答学生的问题。\n\n");

        sb.append("【学生问题】\n");
        sb.append("标题：").append(title).append("\n");
        sb.append("内容：").append(content).append("\n");

        if (existingReplies != null && !existingReplies.isEmpty()) {
            sb.append("\n【已有回复（供参考，避免重复）】\n");
            for (Map<String, String> reply : existingReplies) {
                sb.append("- ").append(reply.get("userName")).append("：").append(reply.get("content")).append("\n");
            }
        }

        sb.append("\n【要求】\n");
        sb.append("1. 回复应专业、清晰、有针对性\n");
        sb.append("2. 如果问题涉及知识点，应给出详细解释\n");
        sb.append("3. 语气友好，鼓励学生\n");
        sb.append("4. 回复控制在 200 字以内\n");
        sb.append("5. 直接返回回复内容，不要有其他说明文字");

        return sb.toString();
    }

    // ========== 2. 生成推荐习题 ==========
    public List<Map<String, Object>> generateExerciseRecommendation(
            List<com.jycz.qingyun.model.entity.Question> wrongQuestions,
            List<com.jycz.qingyun.model.entity.Question> allQuestions,
            int count) {

        if (wrongQuestions.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String prompt = buildRecommendationPrompt(wrongQuestions, allQuestions, count);
            String response = callAI(prompt);
            return parseJsonResponse(response);
        } catch (Exception e) {
            log.error("AI 推荐习题生成失败: {}", e.getMessage());
            return generateFallbackRecommendations(wrongQuestions, count);
        }
    }

    private String buildRecommendationPrompt(
            List<com.jycz.qingyun.model.entity.Question> wrongQuestions,
            List<com.jycz.qingyun.model.entity.Question> allQuestions,
            int count) {

        StringBuilder sb = new StringBuilder();
        sb.append("你是一位资深学科教师。请根据学生的错题情况，生成针对性的练习题。\n\n");

        sb.append("【学生做错的题目】\n");
        for (com.jycz.qingyun.model.entity.Question q : wrongQuestions) {
            sb.append("- ").append(q.getStem()).append("\n");
            sb.append("  正确答案：").append(q.getAnswer()).append("\n");
            sb.append("  解析：").append(q.getExplanation() != null ? q.getExplanation() : "无").append("\n");
        }

        if (allQuestions != null && allQuestions.size() > wrongQuestions.size()) {
            sb.append("\n【学生做对的题目（参考）】\n");
            for (com.jycz.qingyun.model.entity.Question q : allQuestions) {
                if (!wrongQuestions.contains(q)) {
                    sb.append("- ").append(q.getStem()).append("\n");
                    break;
                }
            }
        }

        sb.append("\n【要求】\n");
        sb.append("1. 针对错题涉及的知识点出题\n");
        sb.append("2. 生成 ").append(count).append(" 道练习题\n");
        sb.append("3. 题型可以是：单选、填空、简答\n");
        sb.append("4. 每题都要有正确答案和解析\n");
        sb.append("5. 每道题都要标明对应的知识点名称\n");
        sb.append("6. 返回 JSON 数组格式：\n");
        sb.append("   [{\n");
        sb.append("     \"stem\": \"题干\",\n");
        sb.append("     \"type\": 1,\n");
        sb.append("     \"options\": [\"A.xxx\", \"B.xxx\"],\n");
        sb.append("     \"answer\": \"答案\",\n");
        sb.append("     \"explanation\": \"解析\",\n");
        sb.append("     \"knowledgePoint\": \"知识点名称\"\n");
        sb.append("   }]\n");
        sb.append("请只返回 JSON 数组。");

        return sb.toString();
    }

    // ========== 3. 基于老师评语生成推荐 ==========
    public List<Map<String, Object>> generateRecommendationFromTeacherComments(
            String assignmentTitle,
            List<String> studentAnswers,
            List<String> teacherComments,
            int count) {

        try {
            String prompt = buildTeacherCommentPrompt(assignmentTitle, studentAnswers, teacherComments, count);
            String response = callAI(prompt);
            return parseJsonResponse(response);
        } catch (Exception e) {
            log.error("AI 基于老师评语生成推荐失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private String buildTeacherCommentPrompt(
            String assignmentTitle,
            List<String> studentAnswers,
            List<String> teacherComments,
            int count) {

        StringBuilder sb = new StringBuilder();
        sb.append("你是一位资深学科教师。请根据学生的作答和老师的批改评语，生成针对性练习题。\n\n");

        sb.append("【作业名称】").append(assignmentTitle).append("\n");

        sb.append("\n【学生答案与老师评语】\n");
        for (int i = 0; i < studentAnswers.size(); i++) {
            sb.append("- 学生答案：").append(studentAnswers.get(i)).append("\n");
            if (i < teacherComments.size() && teacherComments.get(i) != null) {
                sb.append("  老师评语：").append(teacherComments.get(i)).append("\n");
            }
        }

        sb.append("\n【要求】\n");
        sb.append("1. 根据老师评语中提到的薄弱点出题\n");
        sb.append("2. 生成 ").append(count).append(" 道练习题\n");
        sb.append("3. 每道题都要标明对应的知识点名称\n");
        sb.append("4. 返回 JSON 数组：\n");
        sb.append("   [{\n");
        sb.append("     \"stem\": \"题干\",\n");
        sb.append("     \"type\": 1,\n");
        sb.append("     \"options\": [\"A.xxx\", \"B.xxx\"],\n");
        sb.append("     \"answer\": \"答案\",\n");
        sb.append("     \"explanation\": \"解析\",\n");
        sb.append("     \"knowledgePoint\": \"知识点名称\"\n");
        sb.append("   }]\n");
        sb.append("请只返回 JSON 数组。");

        return sb.toString();
    }

    // ========== 4. 薄弱知识点分析 ==========
    public List<Map<String, Object>> analyzeWeakPoints(
            List<com.jycz.qingyun.model.entity.Question> wrongQuestions,
            List<com.jycz.qingyun.model.entity.Question> correctQuestions) {

        if (wrongQuestions.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String prompt = buildWeakPointPrompt(wrongQuestions, correctQuestions);
            String response = callAI(prompt);
            return parseJsonResponse(response);
        } catch (Exception e) {
            log.error("薄弱知识点分析失败: {}", e.getMessage());
            return generateFallbackWeakPoints(wrongQuestions);
        }
    }

    private String buildWeakPointPrompt(
            List<com.jycz.qingyun.model.entity.Question> wrongQuestions,
            List<com.jycz.qingyun.model.entity.Question> correctQuestions) {

        StringBuilder sb = new StringBuilder();
        sb.append("你是一位资深学科教师。请分析学生的错题，提炼薄弱知识点并给出针对性讲解。\n\n");

        sb.append("【学生做错的题目】\n");
        for (com.jycz.qingyun.model.entity.Question q : wrongQuestions) {
            sb.append("- 题干：").append(q.getStem()).append("\n");
            sb.append("  正确答案：").append(q.getAnswer()).append("\n");
            sb.append("  解析：").append(q.getExplanation() != null ? q.getExplanation() : "无").append("\n");
        }

        if (correctQuestions != null && !correctQuestions.isEmpty()) {
            sb.append("\n【学生做对的题目】\n");
            for (int i = 0; i < Math.min(2, correctQuestions.size()); i++) {
                sb.append("- ").append(correctQuestions.get(i).getStem()).append("\n");
            }
        }

        sb.append("\n【要求】\n");
        sb.append("1. 根据错题提炼 1-3 个薄弱知识点\n");
        sb.append("2. 每个知识点给出通俗易懂的讲解\n");
        sb.append("3. 列出每个知识点对应的错题序号\n");
        sb.append("4. 返回 JSON 数组：\n");
        sb.append("   [{\n");
        sb.append("     \"knowledgePoint\": \"知识点名称\",\n");
        sb.append("     \"explanation\": \"知识点讲解（通俗易懂）\",\n");
        sb.append("     \"wrongCount\": 错误次数,\n");
        sb.append("     \"wrongQuestions\": [\"错题题干1\", \"错题题干2\"]\n");
        sb.append("   }]\n");
        sb.append("请只返回 JSON 数组。");

        return sb.toString();
    }

    private List<Map<String, Object>> generateFallbackWeakPoints(
            List<com.jycz.qingyun.model.entity.Question> wrongQuestions) {
        List<Map<String, Object>> fallback = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("knowledgePoint", "需要巩固的知识点");
        item.put("explanation", "建议复习错题对应的知识点，理解基本原理和解题方法。");
        item.put("wrongCount", wrongQuestions.size());
        List<String> stems = new ArrayList<>();
        for (com.jycz.qingyun.model.entity.Question q : wrongQuestions) {
            stems.add(q.getStem());
        }
        item.put("wrongQuestions", stems);
        fallback.add(item);
        return fallback;
    }

    // ========== 5. 通用 AI 调用 ==========
    private String callAI(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 800);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "你是一位资深学科教师。"));
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String url = baseUrl + "/v1/chat/completions";

        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

        if (response != null && response.containsKey("choices")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        throw new RuntimeException("AI 响应异常");
    }

    // ========== 6. JSON 解析 ==========
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseJsonResponse(String response) {
        try {
            String clean = response.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(clean, List.class);
        } catch (Exception e) {
            log.error("解析 JSON 失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // ========== 7. 降级方案 ==========
    private List<Map<String, Object>> generateFallbackRecommendations(
            List<com.jycz.qingyun.model.entity.Question> wrongQuestions, int count) {
        List<Map<String, Object>> fallback = new ArrayList<>();
        int size = Math.min(count, wrongQuestions.size());
        Collections.shuffle(wrongQuestions);
        for (int i = 0; i < size; i++) {
            com.jycz.qingyun.model.entity.Question q = wrongQuestions.get(i);
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

    public Long getAiUserId() {
        return AI_USER_ID;
    }
}