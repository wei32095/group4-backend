package com.jycz.qingyun.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SensitiveWordFilter {

    /**
     * 检查文本是否包含敏感词
     * @param text 待检查文本
     * @param sensitiveWords 敏感词列表
     * @return true-包含敏感词，false-不包含
     */
    public boolean containsSensitiveWord(String text, List<String> sensitiveWords) {
        if (text == null || text.isEmpty() || sensitiveWords == null || sensitiveWords.isEmpty()) {
            return false;
        }

        for (String word : sensitiveWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 替换文本中的敏感词为 ***
     * @param text 待处理文本
     * @param sensitiveWords 敏感词列表
     * @return 替换后的文本
     */
    public String replaceSensitiveWord(String text, List<String> sensitiveWords) {
        if (text == null || text.isEmpty() || sensitiveWords == null || sensitiveWords.isEmpty()) {
            return text;
        }

        String result = text;
        for (String word : sensitiveWords) {
            result = result.replace(word, "***");
        }
        return result;
    }
}