package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.RecommendationSubmitRequest;
import com.jycz.qingyun.model.vo.RecommendationQuestionVO;
import com.jycz.qingyun.model.vo.RecommendationSubmitVO;

public interface RecommendationService {

    /**
     * 获取推荐习题（单题）
     */
    RecommendationQuestionVO getRecommendationQuestion(Long weakPointId, Long userId);

    /**
     * 提交推荐习题答案
     */
    RecommendationSubmitVO submitRecommendation(RecommendationSubmitRequest request, Long userId);
}