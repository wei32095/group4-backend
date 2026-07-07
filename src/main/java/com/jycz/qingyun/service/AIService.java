package com.jycz.qingyun.service;

import java.util.List;
import java.util.Map;

public interface AIService {

    // ========== 原有方法 ==========

    /**
     * 生成 AI 回复（课程问题问答）
     */
    String generateReply(String title, String content, List<Map<String, String>> existingReplies);

    /**
     * 获取 AI 用户 ID
     */
    Long getAiUserId();

    // ========== 新增方法 ==========

    /**
     * 根据薄弱知识点生成推荐习题
     */
    Map<String, Object> generateSingleQuestion(String knowledgePoint, List<String> wrongQuestions, int practiceCount);

    /**
     * 分析错题，提取薄弱知识点
     */
    Map<String, Object> analyzeWeakPoints(List<Map<String, Object>> wrongQuestions, String assignmentTitle);
}