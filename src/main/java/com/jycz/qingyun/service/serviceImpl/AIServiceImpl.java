package com.jycz.qingyun.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycz.qingyun.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.openai.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model:deepseek-chat}")
    private String model;

    private static final Long AI_USER_ID = 999L;

    @Override
    public String generateReply(String title, String content, List<Map<String, String>> existingReplies) {
        try {
            log.info("AI 生成回复: title={}", title);
            String prompt = buildReplyPrompt(title, content, existingReplies);
            String result = callDeepSeek(prompt, 0.7);
            log.info("AI 回复生成成功");
            return result;
        } catch (Exception e) {
            log.error("AI 生成回复失败: {}", e.getMessage(), e);
            return "抱歉，AI 助教暂时无法生成回复，请稍后再试或等待老师回复。";
        }
    }

    @Override
    public Long getAiUserId() {
        return AI_USER_ID;
    }

    @Override
    public Map<String, Object> generateSingleQuestion(String knowledgePoint, List<String> wrongQuestions, int practiceCount) {
        try {
            log.info("AI 生成习题: knowledgePoint={}, practiceCount={}", knowledgePoint, practiceCount);
            String prompt = buildQuestionPrompt(knowledgePoint, wrongQuestions, practiceCount);
            String result = callDeepSeek(prompt, 0.8);
            String jsonContent = extractJson(result);
            return objectMapper.readValue(jsonContent, Map.class);
        } catch (Exception e) {
            log.error("AI 生成习题失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 生成习题失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> analyzeWeakPoints(List<Map<String, Object>> wrongQuestions, String assignmentTitle) {
        try {
            log.info("AI 分析薄弱知识点: 错题数量={}", wrongQuestions.size());
            String prompt = buildAnalyzePrompt(wrongQuestions, assignmentTitle);
            String result = callDeepSeek(prompt, 0.5);
            String jsonContent = extractJson(result);
            return objectMapper.readValue(jsonContent, Map.class);
        } catch (Exception e) {
            log.error("AI 分析薄弱知识点失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 分析薄弱知识点失败: " + e.getMessage());
        }
    }

    private String callDeepSeek(String prompt, double temperature) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "你是一位专业的教育专家。"),
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", 2000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        throw new RuntimeException("AI API 调用失败: " + response.getStatusCode());
    }

    private String buildReplyPrompt(String title, String content, List<Map<String, String>> existingReplies) {
        StringBuilder sb = new StringBuilder();
        sb.append("请根据以下问题生成专业、有帮助的回复。\n\n");
        sb.append("问题标题：").append(title).append("\n");
        sb.append("问题内容：").append(content).append("\n\n");

        if (existingReplies != null && !existingReplies.isEmpty()) {
            sb.append("已有回复（请避免重复）：\n");
            for (Map<String, String> reply : existingReplies) {
                sb.append("- ").append(reply.get("userName")).append("：").append(reply.get("content")).append("\n");
            }
            sb.append("\n");
        }

        sb.append("要求：\n");
        sb.append("1. 回复要专业、准确、有帮助\n");
        sb.append("2. 如果问题涉及具体知识点，请详细解释\n");
        sb.append("3. 回复长度控制在 100-300 字\n");
        sb.append("4. 语气要友好、鼓励\n");

        return sb.toString();
    }

    private String buildQuestionPrompt(String knowledgePoint, List<String> wrongQuestions, int practiceCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("请针对知识点「").append(knowledgePoint).append("」生成一道练习题。\n\n");

        if (wrongQuestions != null && !wrongQuestions.isEmpty()) {
            sb.append("参考错题（请避免与这些题目重复）：\n");
            for (String q : wrongQuestions) {
                sb.append("- ").append(q).append("\n");
            }
            sb.append("\n");
        }

        sb.append("练习次数：").append(practiceCount + 1).append("（请生成与之前完全不同的题目）\n\n");
        sb.append("要求：\n");
        sb.append("1. 每次生成的题目必须完全不同，不能重复\n");
        sb.append("2. 如果是选择题，提供4个选项，格式为 [\"A. xxx\", \"B. xxx\", \"C. xxx\", \"D. xxx\"]\n");
        sb.append("3. 必须返回有效的 JSON 格式\n");
        sb.append("4. type: 1-单选题, 2-多选题, 3-判断题, 4-填空题\n");
        sb.append("5. 如果 type 是判断题，options 为 [\"正确\", \"错误\"]\n");
        sb.append("6. 如果 type 是填空题，options 为空数组 []\n\n");
        sb.append("返回格式示例：\n");
        sb.append("{\n");
        sb.append("  \"type\": 1,\n");
        sb.append("  \"stem\": \"题目标题\",\n");
        sb.append("  \"options\": [\"A. 选项一\", \"B. 选项二\", \"C. 选项三\", \"D. 选项四\"],\n");
        sb.append("  \"answer\": \"A\",\n");
        sb.append("  \"explanation\": \"详细解释\"\n");
        sb.append("}\n");
        sb.append("只返回 JSON，不要有任何其他内容。");

        return sb.toString();
    }

    private String buildAnalyzePrompt(List<Map<String, Object>> wrongQuestions, String assignmentTitle) {
        StringBuilder sb = new StringBuilder();
        sb.append("请分析以下错题，提取学生的薄弱知识点。\n\n");
        sb.append("作业名称：").append(assignmentTitle).append("\n\n");
        sb.append("错题列表：\n");

        for (int i = 0; i < wrongQuestions.size(); i++) {
            Map<String, Object> q = wrongQuestions.get(i);
            sb.append((i + 1)).append(". 题干：").append(q.get("stem")).append("\n");
            sb.append("   正确答案：").append(q.get("correctAnswer")).append("\n");
            sb.append("   学生答案：").append(q.get("studentAnswer")).append("\n\n");
        }

        sb.append("请按以下要求返回：\n");
        sb.append("1. 将错题按知识点归类，每个知识点生成一个薄弱点\n");
        sb.append("2. 必须返回有效的 JSON 格式\n\n");
        sb.append("返回格式示例：\n");
        sb.append("{\n");
        sb.append("  \"weakPoints\": [\n");
        sb.append("    {\n");
        sb.append("      \"knowledgePoint\": \"知识点名称\",\n");
        sb.append("      \"explanation\": \"知识点详细讲解（200字以内）\",\n");
        sb.append("      \"wrongCount\": 错题数量,\n");
        sb.append("      \"wrongQuestions\": [\"错题题干1\", \"错题题干2\"]\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");
        sb.append("只返回 JSON，不要有任何其他内容。");

        return sb.toString();
    }

    private String extractJson(String content) {
        if (content == null) {
            throw new RuntimeException("AI 返回内容为空");
        }

        // 尝试提取 ```json ... ``` 中的内容
        int start = content.indexOf("```json");
        if (start != -1) {
            start = content.indexOf("\n", start) + 1;
            int end = content.indexOf("```", start);
            if (end != -1) {
                return content.substring(start, end).trim();
            }
        }

        // 尝试提取 ``` ... ``` 中的内容
        start = content.indexOf("```");
        if (start != -1) {
            start = content.indexOf("\n", start) + 1;
            int end = content.indexOf("```", start);
            if (end != -1) {
                return content.substring(start, end).trim();
            }
        }

        // 尝试提取 { ... } 中的内容
        int braceStart = content.indexOf("{");
        int braceEnd = content.lastIndexOf("}");
        if (braceStart != -1 && braceEnd != -1 && braceEnd > braceStart) {
            return content.substring(braceStart, braceEnd + 1);
        }

        return content.trim();
    }
}