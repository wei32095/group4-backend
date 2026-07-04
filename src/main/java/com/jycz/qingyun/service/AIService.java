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

    /**
     * 生成 AI 回复内容
     */
    public String generateReply(String title, String content, List<Map<String, String>> existingReplies) {
        try {
            // 1. 构建 Prompt
            String prompt = buildPrompt(title, content, existingReplies);

            // 2. 构建请求体
            Map<String, Object> requestBody = buildRequestBody(prompt);

            // 3. 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 4. 调用 API
            String url = baseUrl + "/v1/chat/completions";
            log.info("调用 DeepSeek API: {}", url);

            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            // 5. 解析响应
            String result = parseResponse(response);
            log.info("AI 回复生成成功: {}", result);

            return result;

        } catch (Exception e) {
            log.error("AI 调用失败: {}", e.getMessage());
            return "抱歉，AI 暂时无法回答，请稍后再试或等待老师回复。";
        }
    }

    /**
     * 构建 Prompt
     */
    private String buildPrompt(String title, String content, List<Map<String, String>> existingReplies) {
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

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 500);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "你是一位资深学科教师，擅长解答学生的学科问题。"));
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        return requestBody;
    }

    /**
     * 解析 API 响应
     */
    @SuppressWarnings("unchecked")
    private String parseResponse(Map<String, Object> response) {
        if (response == null) {
            return "AI 响应为空，请重试。";
        }

        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                String content = (String) message.get("content");
                if (content != null && !content.isEmpty()) {
                    return content.trim();
                }
            }
        } catch (Exception e) {
            log.error("解析 AI 响应失败: {}", e.getMessage());
        }

        return "AI 返回格式异常，请重试。";
    }

    public Long getAiUserId() {
        return AI_USER_ID;
    }
}