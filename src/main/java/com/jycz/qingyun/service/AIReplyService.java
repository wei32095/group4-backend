package com.jycz.qingyun.service;

public interface AIReplyService {

    /**
     * 异步生成 AI 回复
     */
    void generateAIRepliesAsync(Long problemId, String title, String content);
}